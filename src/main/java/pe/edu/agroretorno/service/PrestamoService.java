package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Solicitud;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.repository.PrestamoRepository;
import pe.edu.agroretorno.repository.ProductorRepository;
import pe.edu.agroretorno.repository.SolicitudRepository;

/** Proceso 2 (segunda parte): registro de la entrega física y consulta de préstamos (RF08). */
@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final SolicitudRepository solicitudRepository;
    private final ProductorRepository productorRepository;
    private final InventarioService inventarioService;
    private final UsuarioService usuarioService;
    private final HistorialService historialService;
    private final Clock clock;

    public PrestamoService(PrestamoRepository prestamoRepository, SolicitudRepository solicitudRepository,
                           ProductorRepository productorRepository, InventarioService inventarioService,
                           UsuarioService usuarioService, HistorialService historialService, Clock clock) {
        this.prestamoRepository = prestamoRepository;
        this.solicitudRepository = solicitudRepository;
        this.productorRepository = productorRepository;
        this.inventarioService = inventarioService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.clock = clock;
    }

    /**
     * Registra la entrega de una solicitud APROBADA:
     * revalida existencias con bloqueo, descuenta disponible, incrementa prestado,
     * crea el préstamo (máximo uno por solicitud) y marca la solicitud como ENTREGADA.
     */
    @Transactional
    public PrestamoResponse registrarEntrega(Long solicitudId, String usernameEncargado) {
        Usuario encargado = usuarioService.obtenerPorUsername(usernameEncargado);
        Solicitud s = solicitudRepository.findByIdParaActualizar(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", solicitudId));
        if (s.getEstado() == EstadoSolicitud.ENTREGADA || prestamoRepository.existsBySolicitudId(solicitudId)) {
            throw new ReglaNegocioException("La solicitud " + solicitudId + " ya fue entregada");
        }
        if (s.getEstado() != EstadoSolicitud.APROBADA) {
            throw new ReglaNegocioException("No se puede entregar material sin aprobación. Estado actual: "
                    + s.getEstado());
        }
        if (!s.getMaterial().isActivo()) {
            throw new ReglaNegocioException("El material " + s.getMaterial().getNombre() + " está inactivo");
        }
        // Revalida las existencias en el momento de la entrega (fila de inventario bloqueada)
        inventarioService.descontarPorEntrega(s.getMaterial().getId(), s.getCantidad(), encargado,
                "SOLICITUD-" + s.getId());

        Prestamo prestamo = prestamoRepository.save(
                new Prestamo(s, s.getCantidad(), LocalDateTime.now(clock), encargado));
        s.setEstado(EstadoSolicitud.ENTREGADA);
        s.setPrestamo(prestamo);
        historialService.registrar(encargado.getUsername(), "REGISTRAR_ENTREGA", "Prestamo", prestamo.getId(),
                "Entrega de " + s.getCantidad() + " " + s.getMaterial().getUnidad() + " de "
                        + s.getMaterial().getNombre() + " (solicitud " + s.getId() + ")");
        return PrestamoResponse.from(prestamo, hoy());
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar(EstadoPrestamo estado) {
        EstadoPrestamo filtro = estado == null ? EstadoPrestamo.ACTIVO : estado;
        LocalDate hoy = hoy();
        return prestamoRepository.findByEstado(filtro).stream().map(p -> PrestamoResponse.from(p, hoy)).toList();
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorProductor(String username) {
        Productor productor = productorRepository.findByUsername(username)
                .orElseThrow(() -> new OperacionNoPermitidaException("El usuario no es un productor"));
        LocalDate hoy = hoy();
        return prestamoRepository.findByProductorId(productor.getId()).stream()
                .map(p -> PrestamoResponse.from(p, hoy)).toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse obtener(Long prestamoId) {
        Prestamo p = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo", prestamoId));
        return PrestamoResponse.from(p, hoy());
    }

    @Transactional(readOnly = true)
    public Optional<PrestamoResponse> buscarPorSolicitud(Long solicitudId) {
        LocalDate hoy = hoy();
        return prestamoRepository.findBySolicitudId(solicitudId).map(p -> PrestamoResponse.from(p, hoy));
    }

    private LocalDate hoy() {
        return LocalDate.now(clock);
    }
}
