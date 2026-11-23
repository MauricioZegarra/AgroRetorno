package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.DevolucionRequest;
import pe.edu.agroretorno.dto.DevolucionResponse;
import pe.edu.agroretorno.entity.CondicionDevolucion;
import pe.edu.agroretorno.entity.Devolucion;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.DevolucionRepository;
import pe.edu.agroretorno.repository.PrestamoRepository;
import pe.edu.agroretorno.repository.ProductorRepository;

/**
 * Componente 3 de negocio: devoluciones totales o parciales (Proceso 3, RF09).
 * El préstamo se cierra cuando todas las unidades entregadas han sido devueltas y clasificadas.
 */
@Service
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final PrestamoRepository prestamoRepository;
    private final ProductorRepository productorRepository;
    private final InventarioService inventarioService;
    private final UsuarioService usuarioService;
    private final HistorialService historialService;
    private final Clock clock;

    public DevolucionService(DevolucionRepository devolucionRepository, PrestamoRepository prestamoRepository,
                             ProductorRepository productorRepository, InventarioService inventarioService,
                             UsuarioService usuarioService, HistorialService historialService, Clock clock) {
        this.devolucionRepository = devolucionRepository;
        this.prestamoRepository = prestamoRepository;
        this.productorRepository = productorRepository;
        this.inventarioService = inventarioService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.clock = clock;
    }

    @Transactional
    public DevolucionResponse registrar(Long prestamoId, DevolucionRequest req, String usernameEncargado) {
        int buen = valor(req.getCantidadBuenEstado());
        int danadas = valor(req.getCantidadDanada());
        int mant = valor(req.getCantidadMantenimiento());
        if (buen < 0 || danadas < 0 || mant < 0) {
            throw new ValidacionException("Las cantidades devueltas no pueden ser negativas");
        }
        int total = buen + danadas + mant;
        if (total == 0) {
            throw new ValidacionException("Registre al menos una unidad devuelta");
        }
        String observaciones = req.getObservaciones() == null ? null : req.getObservaciones().trim();
        if ((danadas > 0 || mant > 0) && (observaciones == null || observaciones.isEmpty())) {
            throw new ValidacionException(
                    "Registre observaciones cuando haya unidades dañadas o que requieren mantenimiento");
        }

        Usuario encargado = usuarioService.obtenerPorUsername(usernameEncargado);
        Prestamo prestamo = prestamoRepository.findByIdParaActualizar(prestamoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo", prestamoId));
        if (prestamo.getEstado() == EstadoPrestamo.CERRADO) {
            throw new ReglaNegocioException("El préstamo " + prestamoId + " ya está cerrado");
        }
        if (total > prestamo.getCantidadPendiente()) {
            throw new ReglaNegocioException("La cantidad devuelta (" + total + ") supera las unidades pendientes ("
                    + prestamo.getCantidadPendiente() + ")");
        }

        inventarioService.registrarRetorno(prestamo.getSolicitud().getMaterial().getId(), buen, danadas, mant,
                encargado, "PRESTAMO-" + prestamo.getId());

        prestamo.setCantidadDevuelta(prestamo.getCantidadDevuelta() + total);
        prestamo.setCantidadBuenEstado(prestamo.getCantidadBuenEstado() + buen);
        prestamo.setCantidadDanada(prestamo.getCantidadDanada() + danadas);
        prestamo.setCantidadMantenimiento(prestamo.getCantidadMantenimiento() + mant);
        LocalDateTime ahora = LocalDateTime.now(clock);
        if (prestamo.getCantidadPendiente() == 0) {
            // Todas las unidades entregadas están justificadas: se cierra el préstamo
            prestamo.setEstado(EstadoPrestamo.CERRADO);
            prestamo.setFechaCierre(ahora);
        }

        Devolucion devolucion = new Devolucion(prestamo, ahora, encargado, observaciones);
        devolucion.agregarDetalle(CondicionDevolucion.BUEN_ESTADO, buen);
        devolucion.agregarDetalle(CondicionDevolucion.DANADO, danadas);
        devolucion.agregarDetalle(CondicionDevolucion.REQUIERE_MANTENIMIENTO, mant);
        devolucion = devolucionRepository.save(devolucion);
        prestamoRepository.save(prestamo);

        historialService.registrar(encargado.getUsername(), "REGISTRAR_DEVOLUCION", "Prestamo", prestamo.getId(),
                "Devueltas " + total + " (buen estado " + buen + ", dañadas " + danadas + ", mantenimiento " + mant
                        + "). Pendientes: " + prestamo.getCantidadPendiente()
                        + (prestamo.getEstado() == EstadoPrestamo.CERRADO ? ". Préstamo cerrado" : ""));
        return DevolucionResponse.from(devolucion);
    }

    @Transactional(readOnly = true)
    public List<DevolucionResponse> listarPorPrestamo(Long prestamoId) {
        return devolucionRepository.findByPrestamoId(prestamoId).stream().map(DevolucionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<DevolucionResponse> listarPorProductor(String username) {
        Productor productor = productorRepository.findByUsername(username)
                .orElseThrow(() -> new OperacionNoPermitidaException("El usuario no es un productor"));
        return devolucionRepository.findByProductorId(productor.getId()).stream()
                .map(DevolucionResponse::from).toList();
    }

    private static int valor(Integer v) {
        return v == null ? 0 : v;
    }
}
