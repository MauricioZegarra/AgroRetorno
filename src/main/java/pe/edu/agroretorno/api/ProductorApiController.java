package pe.edu.agroretorno.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.agroretorno.dto.DevolucionResponse;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.SolicitudRequest;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.SolicitudService;

/** Operaciones del productor. Cada productor solo accede a su propia información. */
@RestController
@RequestMapping("/api/productor")
@PreAuthorize("hasRole('PRODUCTOR')")
public class ProductorApiController {

    private final SolicitudService solicitudService;
    private final PrestamoService prestamoService;
    private final DevolucionService devolucionService;

    public ProductorApiController(SolicitudService solicitudService, PrestamoService prestamoService,
                                  DevolucionService devolucionService) {
        this.solicitudService = solicitudService;
        this.prestamoService = prestamoService;
        this.devolucionService = devolucionService;
    }

    /** Transacción crítica 2 (JMeter): registrar una solicitud de préstamo (RF06). */
    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudResponse> registrar(@Valid @RequestBody SolicitudRequest req, Authentication auth) {
        SolicitudResponse creada = solicitudService.registrar(auth.getName(), req);
        return ResponseEntity.created(URI.create("/api/productor/solicitudes/" + creada.id())).body(creada);
    }

    @GetMapping("/solicitudes")
    public List<SolicitudResponse> misSolicitudes(Authentication auth) {
        return solicitudService.listarPorProductor(auth.getName());
    }

    @GetMapping("/solicitudes/{id}")
    public SolicitudResponse miSolicitud(@PathVariable Long id, Authentication auth) {
        return solicitudService.obtenerParaProductor(auth.getName(), id);
    }

    @GetMapping("/prestamos")
    public List<PrestamoResponse> misPrestamos(Authentication auth) {
        return prestamoService.listarPorProductor(auth.getName());
    }

    @GetMapping("/devoluciones")
    public List<DevolucionResponse> misDevoluciones(Authentication auth) {
        return devolucionService.listarPorProductor(auth.getName());
    }
}
