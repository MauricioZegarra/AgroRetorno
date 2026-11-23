package pe.edu.agroretorno.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.agroretorno.dto.FiltroHistorial;
import pe.edu.agroretorno.dto.FiltroReporte;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.service.HistorialService;
import pe.edu.agroretorno.service.InventarioService;
import pe.edu.agroretorno.service.ProductorService;
import pe.edu.agroretorno.service.ReporteService;

/** RF10: historial de operaciones y reporte consolidado. */
@Controller
@RequestMapping("/encargado")
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoReporteController {

    private final ReporteService reporteService;
    private final HistorialService historialService;
    private final ProductorService productorService;
    private final InventarioService inventarioService;

    public EncargadoReporteController(ReporteService reporteService, HistorialService historialService,
                                      ProductorService productorService, InventarioService inventarioService) {
        this.reporteService = reporteService;
        this.historialService = historialService;
        this.productorService = productorService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/reportes")
    public String reporte(@ModelAttribute("filtro") FiltroReporte filtro, Model model) {
        model.addAttribute("reporte", reporteService.generar(filtro));
        model.addAttribute("productores", productorService.listar(null));
        model.addAttribute("materiales", inventarioService.listarInventario(null, false));
        model.addAttribute("estados", EstadoSolicitud.values());
        return "encargado/reporte";
    }

    @GetMapping("/historial")
    public String historial(@ModelAttribute("filtro") FiltroHistorial filtro, Model model) {
        model.addAttribute("operaciones", historialService.consultar(filtro));
        return "encargado/historial";
    }
}
