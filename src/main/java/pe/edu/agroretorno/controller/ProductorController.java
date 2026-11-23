package pe.edu.agroretorno.controller;

import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.agroretorno.config.ReglasNegocioProperties;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.SolicitudRequest;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.exception.AgroRetornoException;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.InventarioService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.ProductorService;
import pe.edu.agroretorno.service.SolicitudService;

/** Pantallas del productor agrícola: inventario, solicitudes, préstamos e historial. */
@Controller
@RequestMapping("/productor")
@PreAuthorize("hasRole('PRODUCTOR')")
public class ProductorController {

    private final InventarioService inventarioService;
    private final SolicitudService solicitudService;
    private final PrestamoService prestamoService;
    private final DevolucionService devolucionService;
    private final ProductorService productorService;
    private final ReglasNegocioProperties reglas;
    private final Clock clock;

    public ProductorController(InventarioService inventarioService, SolicitudService solicitudService,
                               PrestamoService prestamoService, DevolucionService devolucionService,
                               ProductorService productorService, ReglasNegocioProperties reglas, Clock clock) {
        this.inventarioService = inventarioService;
        this.solicitudService = solicitudService;
        this.prestamoService = prestamoService;
        this.devolucionService = devolucionService;
        this.productorService = productorService;
        this.reglas = reglas;
        this.clock = clock;
    }

    @GetMapping("/panel")
    public String panel(Authentication auth, Model model) {
        List<SolicitudResponse> solicitudes = solicitudService.listarPorProductor(auth.getName());
        List<PrestamoResponse> activos = prestamoService.listarPorProductor(auth.getName()).stream()
                .filter(p -> p.estado() == EstadoPrestamo.ACTIVO).toList();
        model.addAttribute("productor", productorService.obtenerPorUsername(auth.getName()));
        model.addAttribute("pendientes", contar(solicitudes, EstadoSolicitud.PENDIENTE));
        model.addAttribute("aprobadas", contar(solicitudes, EstadoSolicitud.APROBADA));
        model.addAttribute("rechazadas", contar(solicitudes, EstadoSolicitud.RECHAZADA));
        model.addAttribute("prestamosActivos", activos);
        model.addAttribute("ultimasSolicitudes", solicitudes.stream().limit(5).toList());
        return "productor/panel";
    }

    /** RF05: inventario disponible con búsqueda por texto y categoría. */
    @GetMapping("/inventario")
    public String inventario(@RequestParam(name = "q", required = false) String q,
                             @RequestParam(name = "categoria", required = false) CategoriaMaterial categoria,
                             Model model) {
        model.addAttribute("materiales", inventarioService.listarInventario(q, categoria, true));
        model.addAttribute("q", q);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("categorias", CategoriaMaterial.values());
        return "productor/inventario";
    }

    /** RF06: formulario de solicitud. */
    @GetMapping("/solicitudes/nueva")
    public String nuevaSolicitud(@RequestParam(name = "materialId", required = false) Long materialId, Model model) {
        SolicitudRequest req = new SolicitudRequest();
        req.setMaterialId(materialId);
        req.setCantidad(1);
        return formulario(req, model);
    }

    @PostMapping("/solicitudes")
    public String registrarSolicitud(@Valid @ModelAttribute("solicitud") SolicitudRequest solicitud,
                                     BindingResult binding, Authentication auth, Model model,
                                     RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return formulario(solicitud, model);
        }
        try {
            SolicitudResponse creada = solicitudService.registrar(auth.getName(), solicitud);
            flash.addFlashAttribute("exito", "Solicitud N.° " + creada.id() + " registrada correctamente. Estado: "
                    + creada.estado());
            flash.addFlashAttribute("idCreado", creada.id());
            return "redirect:/productor/solicitudes";
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(solicitud, model);
        }
    }

    @GetMapping("/solicitudes")
    public String misSolicitudes(Authentication auth, Model model) {
        model.addAttribute("solicitudes", solicitudService.listarPorProductor(auth.getName()));
        return "productor/solicitudes";
    }

    @GetMapping("/prestamos")
    public String misPrestamos(Authentication auth, Model model) {
        model.addAttribute("prestamos", prestamoService.listarPorProductor(auth.getName()));
        return "productor/prestamos";
    }

    @GetMapping("/historial")
    public String historial(Authentication auth, Model model) {
        model.addAttribute("solicitudes", solicitudService.listarPorProductor(auth.getName()));
        model.addAttribute("prestamos", prestamoService.listarPorProductor(auth.getName()));
        model.addAttribute("devoluciones", devolucionService.listarPorProductor(auth.getName()));
        return "productor/historial";
    }

    private String formulario(SolicitudRequest req, Model model) {
        List<MaterialResponse> materiales = inventarioService.listarInventario(null, true);
        LocalDate hoy = LocalDate.now(clock);
        model.addAttribute("solicitud", req);
        model.addAttribute("materiales", materiales);
        model.addAttribute("materialSeleccionado", materiales.stream()
                .filter(m -> m.id().equals(req.getMaterialId())).findFirst().orElse(null));
        model.addAttribute("fechaMinima", hoy.plusDays(reglas.getPlazoMinimoDias()));
        model.addAttribute("fechaMaxima", hoy.plusDays(reglas.getPlazoMaximoDias()));
        model.addAttribute("longitudMinimaMotivo", reglas.getLongitudMinimaMotivo());
        return "productor/solicitud-form";
    }

    private static long contar(List<SolicitudResponse> lista, EstadoSolicitud estado) {
        return lista.stream().filter(s -> s.estado() == estado).count();
    }
}
