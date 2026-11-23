package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.config.ReglasNegocioProperties;
import pe.edu.agroretorno.dto.SolicitudRequest;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Solicitud;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.ProductorRepository;
import pe.edu.agroretorno.repository.SolicitudRepository;

/**
 * Componente 2 de negocio: registro, aprobación y rechazo de solicitudes
 * (Proceso 1 y primera parte del Proceso 2; RF06 y RF07).
 */
@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ProductorRepository productorRepository;
    private final MaterialRepository materialRepository;
    private final InventarioService inventarioService;
    private final UsuarioService usuarioService;
    private final HistorialService historialService;
    private final ReglasNegocioProperties reglas;
    private final Clock clock;

    public SolicitudService(SolicitudRepository solicitudRepository, ProductorRepository productorRepository,
                            MaterialRepository materialRepository, InventarioService inventarioService,
                            UsuarioService usuarioService, HistorialService historialService,
                            ReglasNegocioProperties reglas, Clock clock) {
        this.solicitudRepository = solicitudRepository;
        this.productorRepository = productorRepository;
        this.materialRepository = materialRepository;
        this.inventarioService = inventarioService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.reglas = reglas;
        this.clock = clock;
    }

    // ------------------------------------------------------------------ Proceso 1: registrar (RF06)

    @Transactional
    public SolicitudResponse registrar(String username, SolicitudRequest req) {
        Productor productor = productorRepository.findByUsername(username)
                .orElseThrow(() -> new OperacionNoPermitidaException("Solo los productores pueden registrar solicitudes"));
        validarDatos(req);
        Material material = materialRepository.findByIdConInventario(req.getMaterialId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Material", req.getMaterialId()));
        // Comprueba que el material esté activo y que la cantidad no supere la disponibilidad actual
        inventarioService.validarDisponibilidad(material, req.getCantidad());

        Solicitud solicitud = new Solicitud(productor, material, req.getCantidad(), LocalDateTime.now(clock),
                req.getFechaDevolucionPrevista(), req.getMotivo().trim());
        solicitud = solicitudRepository.save(solicitud);
        historialService.registrar(username, "REGISTRAR_SOLICITUD", "Solicitud", solicitud.getId(),
                req.getCantidad() + " " + material.getUnidad() + " de " + material.getNombre()
                        + "; devolución prevista " + req.getFechaDevolucionPrevista());
        return SolicitudResponse.from(solicitud);
    }

    /** Validaciones de entrada en el backend (RNF05), independientes de la interfaz. */
    void validarDatos(SolicitudRequest req) {
        if (req == null || req.getMaterialId() == null) {
            throw new ValidacionException("Seleccione un material");
        }
        if (req.getCantidad() == null || req.getCantidad() <= 0) {
            throw new ValidacionException("La cantidad debe ser un número entero positivo");
        }
        LocalDate fecha = req.getFechaDevolucionPrevista();
        if (fecha == null) {
            throw new ValidacionException("Indique la fecha prevista de devolución");
        }
        LocalDate hoy = LocalDate.now(clock);
        LocalDate minima = hoy.plusDays(reglas.getPlazoMinimoDias());
        LocalDate maxima = hoy.plusDays(reglas.getPlazoMaximoDias());
        if (fecha.isBefore(minima)) {
            throw new ValidacionException("La fecha prevista de devolución debe ser igual o posterior a " + minima);
        }
        if (fecha.isAfter(maxima)) {
            throw new ValidacionException("La fecha prevista de devolución no puede superar "
                    + reglas.getPlazoMaximoDias() + " días (máximo " + maxima + ")");
        }
        validarMotivo(req.getMotivo(), "El motivo de la solicitud");
    }

    private void validarMotivo(String motivo, String campo) {
        if (motivo == null || motivo.trim().length() < reglas.getLongitudMinimaMotivo()) {
            throw new ValidacionException(campo + " debe tener al menos " + reglas.getLongitudMinimaMotivo()
                    + " caracteres");
        }
        if (motivo.trim().length() > 300) {
            throw new ValidacionException(campo + " no puede superar 300 caracteres");
        }
    }

    // ------------------------------------------------------------------ consultas

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarPorProductor(String username) {
        Productor productor = productorRepository.findByUsername(username)
                .orElseThrow(() -> new OperacionNoPermitidaException("El usuario no es un productor"));
        return solicitudRepository.findByProductorId(productor.getId()).stream().map(SolicitudResponse::from).toList();
    }

    /** Un productor solo puede consultar sus propias solicitudes (RNF02). */
    @Transactional(readOnly = true)
    public SolicitudResponse obtenerParaProductor(String username, Long solicitudId) {
        Solicitud s = buscar(solicitudId);
        if (!s.getProductor().getUsuario().getUsername().equals(username)) {
            throw new OperacionNoPermitidaException("No puede consultar solicitudes de otro productor");
        }
        return SolicitudResponse.from(s);
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listar(EstadoSolicitud estado) {
        List<Solicitud> lista = estado == null ? solicitudRepository.findAllOrdenadas()
                : solicitudRepository.findByEstado(estado);
        return lista.stream().map(SolicitudResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SolicitudResponse obtener(Long solicitudId) {
        return SolicitudResponse.from(buscar(solicitudId));
    }

    // ------------------------------------------------------------------ Proceso 2: aprobar / rechazar (RF07)

    /** Aprueba una solicitud PENDIENTE. La aprobación NO descuenta inventario; eso ocurre en la entrega. */
    @Transactional
    public SolicitudResponse aprobar(Long solicitudId, String usernameEncargado) {
        Usuario encargado = usuarioService.obtenerPorUsername(usernameEncargado);
        Solicitud s = solicitudRepository.findByIdParaActualizar(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", solicitudId));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden aprobar solicitudes PENDIENTES. Estado actual: "
                    + s.getEstado());
        }
        // Verifica la disponibilidad en el momento de aprobar
        inventarioService.validarDisponibilidad(s.getMaterial(), s.getCantidad());
        s.setEstado(EstadoSolicitud.APROBADA);
        s.setRespondidoPor(encargado);
        s.setFechaRespuesta(LocalDateTime.now(clock));
        historialService.registrar(encargado.getUsername(), "APROBAR_SOLICITUD", "Solicitud", s.getId(),
                "Solicitud aprobada (" + s.getCantidad() + " de " + s.getMaterial().getNombre() + ")");
        return SolicitudResponse.from(s);
    }

    /**
     * Rechaza una solicitud con motivo. Se permite desde PENDIENTE o APROBADA (aún no entregada);
     * una solicitud RECHAZADA o ENTREGADA no puede cambiar de estado.
     */
    @Transactional
    public SolicitudResponse rechazar(Long solicitudId, String motivo, String usernameEncargado) {
        validarMotivo(motivo, "El motivo del rechazo");
        Usuario encargado = usuarioService.obtenerPorUsername(usernameEncargado);
        Solicitud s = solicitudRepository.findByIdParaActualizar(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", solicitudId));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE && s.getEstado() != EstadoSolicitud.APROBADA) {
            throw new ReglaNegocioException("No se puede rechazar una solicitud en estado " + s.getEstado());
        }
        s.setEstado(EstadoSolicitud.RECHAZADA);
        s.setRespondidoPor(encargado);
        s.setFechaRespuesta(LocalDateTime.now(clock));
        s.setMotivoRechazo(motivo.trim());
        historialService.registrar(encargado.getUsername(), "RECHAZAR_SOLICITUD", "Solicitud", s.getId(),
                "Motivo: " + motivo.trim());
        return SolicitudResponse.from(s);
    }

    private Solicitud buscar(Long id) {
        return solicitudRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", id));
    }
}
