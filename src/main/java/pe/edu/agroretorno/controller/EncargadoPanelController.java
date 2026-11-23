package pe.edu.agroretorno.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.agroretorno.dto.FiltroReporte;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.ReporteConsolidado;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.service.ReporteService;
import pe.edu.agroretorno.service.SolicitudService;

/** Panel administrativo del encargado del almacén. */
@Controller
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoPanelController {

    private final ReporteService reporteService;
    private final SolicitudService solicitudService;

    public EncargadoPanelController(ReporteService reporteService, SolicitudService solicitudService) {
        this.reporteService = reporteService;
        this.solicitudService = solicitudService;
    }

    @GetMapping("/encargado/panel")
    public String panel(Model model) {
        ReporteConsolidado r = reporteService.generar(new FiltroReporte());
        List<PrestamoResponse> vencidos = r.pendientesDevolucion().stream().filter(PrestamoResponse::vencido).toList();
        model.addAttribute("reporte", r);
        model.addAttribute("solicitudesPendientes", solicitudService.listar(EstadoSolicitud.PENDIENTE));
        model.addAttribute("solicitudesAprobadas", solicitudService.listar(EstadoSolicitud.APROBADA));
        model.addAttribute("prestamosVencidos", vencidos);
        return "encargado/panel";
    }
}
