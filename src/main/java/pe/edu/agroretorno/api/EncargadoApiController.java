package pe.edu.agroretorno.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.agroretorno.dto.DevolucionRequest;
import pe.edu.agroretorno.dto.DevolucionResponse;
import pe.edu.agroretorno.dto.FiltroHistorial;
import pe.edu.agroretorno.dto.FiltroReporte;
import pe.edu.agroretorno.dto.HistorialResponse;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.RechazoRequest;
import pe.edu.agroretorno.dto.ReporteConsolidado;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.HistorialService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.ReporteService;
import pe.edu.agroretorno.service.SolicitudService;

/** Operaciones del encargado del almacén (RF07, RF08, RF09, RF10). */
@RestController
@RequestMapping("/api/encargado")
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoApiController {

    private final SolicitudService solicitudService;
    private final PrestamoService prestamoService;
    private final DevolucionService devolucionService;
    private final HistorialService historialService;
    private final ReporteService reporteService;

    public EncargadoApiController(SolicitudService solicitudService, PrestamoService prestamoService,
                                  DevolucionService devolucionService, HistorialService historialService,
                                  ReporteService reporteService) {
        this.solicitudService = solicitudService;
        this.prestamoService = prestamoService;
        this.devolucionService = devolucionService;
        this.historialService = historialService;
        this.reporteService = reporteService;
    }

    @GetMapping("/solicitudes")
    public List<SolicitudResponse> solicitudes(@RequestParam(name = "estado", required = false) EstadoSolicitud estado) {
        return solicitudService.listar(estado);
    }

    @GetMapping("/solicitudes/{id}")
    public SolicitudResponse solicitud(@PathVariable Long id) {
        return solicitudService.obtener(id);
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public SolicitudResponse aprobar(@PathVariable Long id, Authentication auth) {
        return solicitudService.aprobar(id, auth.getName());
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public SolicitudResponse rechazar(@PathVariable Long id, @Valid @RequestBody RechazoRequest req,
                                      Authentication auth) {
        return solicitudService.rechazar(id, req.getMotivo(), auth.getName());
    }

    @PostMapping("/solicitudes/{id}/entrega")
    public ResponseEntity<PrestamoResponse> entregar(@PathVariable Long id, Authentication auth) {
        PrestamoResponse p = prestamoService.registrarEntrega(id, auth.getName());
        return ResponseEntity.created(URI.create("/api/encargado/prestamos/" + p.id())).body(p);
    }

    @GetMapping("/prestamos")
    public List<PrestamoResponse> prestamos(@RequestParam(name = "estado", required = false) EstadoPrestamo estado) {
        return prestamoService.listar(estado);
    }

    @GetMapping("/prestamos/{id}")
    public PrestamoResponse prestamo(@PathVariable Long id) {
        return prestamoService.obtener(id);
    }

    @GetMapping("/prestamos/{id}/devoluciones")
    public List<DevolucionResponse> devoluciones(@PathVariable Long id) {
        return devolucionService.listarPorPrestamo(id);
    }

    @PostMapping("/prestamos/{id}/devoluciones")
    public ResponseEntity<DevolucionResponse> devolver(@PathVariable Long id, @Valid @RequestBody DevolucionRequest req,
                                                       Authentication auth) {
        DevolucionResponse d = devolucionService.registrar(id, req, auth.getName());
        return ResponseEntity.created(URI.create("/api/encargado/prestamos/" + id + "/devoluciones")).body(d);
    }

    @GetMapping("/historial")
    public List<HistorialResponse> historial(@ModelAttribute FiltroHistorial filtro) {
        return historialService.consultar(filtro);
    }

    @GetMapping("/reportes/consolidado")
    public ReporteConsolidado reporte(@ModelAttribute FiltroReporte filtro) {
        return reporteService.generar(filtro);
    }
}
