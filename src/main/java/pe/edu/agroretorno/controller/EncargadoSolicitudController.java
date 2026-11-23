package pe.edu.agroretorno.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.RechazoRequest;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.exception.AgroRetornoException;
import pe.edu.agroretorno.service.InventarioService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.ProductorService;
import pe.edu.agroretorno.service.SolicitudService;

/** Proceso 2: revisión, aprobación, rechazo (RF07) y entrega (RF08) de solicitudes. */
@Controller
@RequestMapping("/encargado/solicitudes")
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoSolicitudController {

    private final SolicitudService solicitudService;
    private final PrestamoService prestamoService;
    private final ProductorService productorService;
    private final InventarioService inventarioService;

    public EncargadoSolicitudController(SolicitudService solicitudService, PrestamoService prestamoService,
                                        ProductorService productorService, InventarioService inventarioService) {
        this.solicitudService = solicitudService;
        this.prestamoService = prestamoService;
        this.productorService = productorService;
        this.inventarioService = inventarioService;
    }

    /** Lista por estado; por defecto muestra las PENDIENTES. estado=TODAS muestra todas. */
    @GetMapping
    public String listar(@RequestParam(name = "estado", defaultValue = "PENDIENTE") String estado, Model model) {
        EstadoSolicitud filtro = convertirEstado(estado);
        model.addAttribute("solicitudes", solicitudService.listar(filtro));
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoSolicitud.values());
        return "encargado/solicitudes";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        return vistaDetalle(id, new RechazoRequest(), model);
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        try {
            solicitudService.aprobar(id, auth.getName());
            flash.addFlashAttribute("exito", "Solicitud N.° " + id + " APROBADA. Registre la entrega física.");
        } catch (AgroRetornoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/encargado/solicitudes/" + id;
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id, @Valid @ModelAttribute("rechazo") RechazoRequest rechazo,
                           BindingResult binding, Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return vistaDetalle(id, rechazo, model);
        }
        try {
            solicitudService.rechazar(id, rechazo.getMotivo(), auth.getName());
            flash.addFlashAttribute("exito", "Solicitud N.° " + id + " RECHAZADA");
            return "redirect:/encargado/solicitudes/" + id;
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return vistaDetalle(id, rechazo, model);
        }
    }

    @PostMapping("/{id}/entregar")
    public String entregar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        try {
            PrestamoResponse p = prestamoService.registrarEntrega(id, auth.getName());
            flash.addFlashAttribute("exito", "Entrega registrada: préstamo N.° " + p.id() + " con "
                    + p.cantidadEntregada() + " " + p.unidad() + ". Inventario actualizado.");
        } catch (AgroRetornoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/encargado/solicitudes/" + id;
    }

    private static EstadoSolicitud convertirEstado(String estado) {
        for (EstadoSolicitud e : EstadoSolicitud.values()) {
            if (e.name().equals(estado)) {
                return e;
            }
        }
        return null; // "TODAS" o valor desconocido: sin filtro
    }

    private String vistaDetalle(Long id, RechazoRequest rechazo, Model model) {
        SolicitudResponse s = solicitudService.obtener(id);
        model.addAttribute("solicitud", s);
        model.addAttribute("productor", productorService.obtener(s.productorId()));
        model.addAttribute("disponibilidad", inventarioService.consultarDisponibilidad(s.materialId(), s.cantidad()));
        model.addAttribute("prestamo", prestamoService.buscarPorSolicitud(id).orElse(null));
        model.addAttribute("rechazo", rechazo);
        return "encargado/solicitud-detalle";
    }
}
