package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.AjusteExistenciasRequest;
import pe.edu.agroretorno.dto.DisponibilidadResponse;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.dto.MovimientoResponse;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.Inventario;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.MovimientoInventario;
import pe.edu.agroretorno.entity.TipoMovimiento;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.InventarioRepository;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.MovimientoInventarioRepository;

/**
 * Componente 1 de negocio: disponibilidad y control de inventario (RF04, RF05, RF08, RF09).
 * Toda modificación de existencias pasa por este servicio y conserva la igualdad:
 * total = disponible + prestada + dañada + mantenimiento.
 */
@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MaterialRepository materialRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioService usuarioService;
    private final HistorialService historialService;
    private final Clock clock;

    public InventarioService(InventarioRepository inventarioRepository, MaterialRepository materialRepository,
                             MovimientoInventarioRepository movimientoRepository, UsuarioService usuarioService,
                             HistorialService historialService, Clock clock) {
        this.inventarioRepository = inventarioRepository;
        this.materialRepository = materialRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.clock = clock;
    }

    // ------------------------------------------------------------------ consultas (RF05)

    /** Consulta si se puede atender una cantidad de un material. No modifica datos. */
    @Transactional(readOnly = true)
    public DisponibilidadResponse consultarDisponibilidad(Long materialId, int cantidad) {
        validarCantidadPositiva(cantidad);
        Material material = materialRepository.findByIdConInventario(materialId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Material", materialId));
        int disponible = material.getInventario().getCantidadDisponible();
        if (!material.isActivo()) {
            return new DisponibilidadResponse(material.getId(), material.getNombre(), material.getEstado(),
                    disponible, cantidad, false, "El material está inactivo y no puede solicitarse");
        }
        boolean alcanza = disponible >= cantidad;
        String mensaje = alcanza
                ? "Hay " + disponible + " unidades disponibles"
                : "Solo hay " + disponible + " unidades disponibles";
        return new DisponibilidadResponse(material.getId(), material.getNombre(), material.getEstado(),
                disponible, cantidad, alcanza, mensaje);
    }

    /**
     * Regla de negocio: un material solo puede solicitarse si está activo y la cantidad
     * no supera las unidades disponibles en ese momento.
     */
    public void validarDisponibilidad(Material material, int cantidad) {
        validarCantidadPositiva(cantidad);
        if (!material.isActivo()) {
            throw new ReglaNegocioException("El material " + material.getNombre() + " está inactivo");
        }
        int disponible = material.getInventario().getCantidadDisponible();
        if (cantidad > disponible) {
            throw new ReglaNegocioException("Disponibilidad insuficiente: solicitó " + cantidad
                    + " y solo hay " + disponible + " unidades disponibles de " + material.getNombre());
        }
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> listarInventario(String texto, boolean soloActivos) {
        return listarInventario(texto, null, soloActivos);
    }

    /** Búsqueda de materiales por texto (nombre o código) y categoría. */
    @Transactional(readOnly = true)
    public List<MaterialResponse> listarInventario(String texto, CategoriaMaterial categoria, boolean soloActivos) {
        return materialRepository.buscarConInventario(normalizar(texto)).stream()
                .filter(m -> !soloActivos || m.isActivo())
                .filter(m -> categoria == null || m.getCategoria() == categoria)
                .map(MaterialResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientos(Long materialId) {
        return movimientoRepository.findByMaterialId(materialId).stream().map(MovimientoResponse::from).toList();
    }

    // ------------------------------------------------------------------ altas y ajustes (RF04)

    /** Crea las existencias iniciales de un material recién registrado. */
    @Transactional
    public Inventario crearInventarioInicial(Material material, int cantidadInicial, Usuario actor) {
        if (cantidadInicial < 0) {
            throw new ValidacionException("La cantidad inicial no puede ser negativa");
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        Inventario inventario = inventarioRepository.save(new Inventario(material, cantidadInicial, ahora));
        material.setInventario(inventario);
        if (cantidadInicial > 0) {
            registrarMovimiento(material, TipoMovimiento.ALTA_INICIAL, cantidadInicial, actor,
                    "MATERIAL-" + material.getCodigo(), "Existencias iniciales");
        }
        return inventario;
    }

    /** Ajustes manuales del encargado: ingresos, retiros, mantenimiento, reparación y bajas. */
    @Transactional
    public MaterialResponse ajustarExistencias(Long materialId, AjusteExistenciasRequest req, String username) {
        if (req.getTipo() == null) {
            throw new ValidacionException("Seleccione el tipo de ajuste");
        }
        int cantidad = req.getCantidad() == null ? 0 : req.getCantidad();
        validarCantidadPositiva(cantidad);
        if (req.getObservacion() == null || req.getObservacion().isBlank()) {
            throw new ValidacionException("Indique el motivo del ajuste");
        }
        Usuario actor = usuarioService.obtenerPorUsername(username);
        Inventario inv = bloquear(materialId);
        switch (req.getTipo()) {
            case INGRESO -> {
                inv.setCantidadTotal(inv.getCantidadTotal() + cantidad);
                inv.setCantidadDisponible(inv.getCantidadDisponible() + cantidad);
            }
            case RETIRO -> {
                exigir(cantidad <= inv.getCantidadDisponible(), "No puede retirar más unidades de las disponibles ("
                        + inv.getCantidadDisponible() + ")");
                inv.setCantidadTotal(inv.getCantidadTotal() - cantidad);
                inv.setCantidadDisponible(inv.getCantidadDisponible() - cantidad);
            }
            case ENVIO_MANTENIMIENTO -> {
                exigir(cantidad <= inv.getCantidadDisponible(), "No puede enviar a mantenimiento más unidades de las disponibles ("
                        + inv.getCantidadDisponible() + ")");
                inv.setCantidadDisponible(inv.getCantidadDisponible() - cantidad);
                inv.setCantidadMantenimiento(inv.getCantidadMantenimiento() + cantidad);
            }
            case REPARACION -> {
                exigir(cantidad <= inv.getCantidadMantenimiento(), "Solo hay " + inv.getCantidadMantenimiento()
                        + " unidades en mantenimiento");
                inv.setCantidadMantenimiento(inv.getCantidadMantenimiento() - cantidad);
                inv.setCantidadDisponible(inv.getCantidadDisponible() + cantidad);
            }
            case BAJA_DANADO -> {
                exigir(cantidad <= inv.getCantidadDanada(), "Solo hay " + inv.getCantidadDanada()
                        + " unidades dañadas");
                inv.setCantidadDanada(inv.getCantidadDanada() - cantidad);
                inv.setCantidadTotal(inv.getCantidadTotal() - cantidad);
            }
            default -> throw new ValidacionException("Tipo de ajuste no permitido: " + req.getTipo().getEtiqueta());
        }
        guardar(inv);
        registrarMovimiento(inv.getMaterial(), req.getTipo(), cantidad, actor, "AJUSTE", req.getObservacion().trim());
        historialService.registrar(actor.getUsername(), "AJUSTE_EXISTENCIAS", "Material", materialId,
                req.getTipo().getEtiqueta() + ": " + cantidad + " unidades. " + req.getObservacion().trim());
        return MaterialResponse.from(inv.getMaterial());
    }

    // ------------------------------------------------------------------ entregas y devoluciones

    /**
     * RF08: descuenta las unidades entregadas. Vuelve a validar el stock con la fila bloqueada,
     * por lo que dos entregas simultáneas no pueden dejar existencias negativas.
     */
    @Transactional
    public Inventario descontarPorEntrega(Long materialId, int cantidad, Usuario actor, String referencia) {
        validarCantidadPositiva(cantidad);
        Inventario inv = bloquear(materialId);
        if (cantidad > inv.getCantidadDisponible()) {
            throw new ReglaNegocioException("Existencias insuficientes para la entrega: se requieren " + cantidad
                    + " y hay " + inv.getCantidadDisponible() + " disponibles");
        }
        inv.setCantidadDisponible(inv.getCantidadDisponible() - cantidad);
        inv.setCantidadPrestada(inv.getCantidadPrestada() + cantidad);
        guardar(inv);
        registrarMovimiento(inv.getMaterial(), TipoMovimiento.ENTREGA, cantidad, actor, referencia, null);
        return inv;
    }

    /**
     * RF09: las unidades en buen estado vuelven a disponible; las dañadas y las que requieren
     * mantenimiento quedan registradas como no disponibles.
     */
    @Transactional
    public Inventario registrarRetorno(Long materialId, int buenEstado, int danadas, int mantenimiento,
                                       Usuario actor, String referencia) {
        if (buenEstado < 0 || danadas < 0 || mantenimiento < 0) {
            throw new ValidacionException("Las cantidades devueltas no pueden ser negativas");
        }
        int total = buenEstado + danadas + mantenimiento;
        validarCantidadPositiva(total);
        Inventario inv = bloquear(materialId);
        if (total > inv.getCantidadPrestada()) {
            throw new ReglaNegocioException("La devolución (" + total + ") supera las unidades prestadas del material ("
                    + inv.getCantidadPrestada() + ")");
        }
        inv.setCantidadPrestada(inv.getCantidadPrestada() - total);
        inv.setCantidadDisponible(inv.getCantidadDisponible() + buenEstado);
        inv.setCantidadDanada(inv.getCantidadDanada() + danadas);
        inv.setCantidadMantenimiento(inv.getCantidadMantenimiento() + mantenimiento);
        guardar(inv);
        Material material = inv.getMaterial();
        if (buenEstado > 0) {
            registrarMovimiento(material, TipoMovimiento.DEVOLUCION_BUEN_ESTADO, buenEstado, actor, referencia, null);
        }
        if (danadas > 0) {
            registrarMovimiento(material, TipoMovimiento.DEVOLUCION_DANADO, danadas, actor, referencia, null);
        }
        if (mantenimiento > 0) {
            registrarMovimiento(material, TipoMovimiento.DEVOLUCION_MANTENIMIENTO, mantenimiento, actor, referencia, null);
        }
        return inv;
    }

    // ------------------------------------------------------------------ utilidades

    private Inventario bloquear(Long materialId) {
        return inventarioRepository.findByMaterialIdParaActualizar(materialId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario del material", materialId));
    }

    private void guardar(Inventario inv) {
        if (!inv.estaCuadrado()) {
            // Protección defensiva: nunca debe persistirse un inventario descuadrado.
            throw new IllegalStateException("Inventario descuadrado para el material " + inv.getMaterial().getId());
        }
        inv.setFechaActualizacion(LocalDateTime.now(clock));
        inventarioRepository.save(inv);
    }

    private void registrarMovimiento(Material material, TipoMovimiento tipo, int cantidad, Usuario actor,
                                     String referencia, String observacion) {
        movimientoRepository.save(new MovimientoInventario(material, tipo, cantidad, LocalDateTime.now(clock),
                actor, referencia, observacion));
    }

    static void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad debe ser un número entero positivo");
        }
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new ReglaNegocioException(mensaje);
        }
    }

    static String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
}
