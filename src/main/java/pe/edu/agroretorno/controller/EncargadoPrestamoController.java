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
import pe.edu.agroretorno.dto.DevolucionRequest;
import pe.edu.agroretorno.dto.DevolucionResponse;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.exception.AgroRetornoException;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.PrestamoService;

/** Proceso 3: préstamos activos y registro de devoluciones totales o parciales (RF09). */
@Controller
@RequestMapping("/encargado/prestamos")
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoPrestamoController {

    private final PrestamoService prestamoService;
    private final DevolucionService devolucionService;

    public EncargadoPrestamoController(PrestamoService prestamoService, DevolucionService devolucionService) {
        this.prestamoService = prestamoService;
        this.devolucionService = devolucionService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "estado", defaultValue = "ACTIVO") EstadoPrestamo estado, Model model) {
        model.addAttribute("prestamos", prestamoService.listar(estado));
        model.addAttribute("estadoSeleccionado", estado);
        return "encargado/prestamos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        return vista(id, new DevolucionRequest(0, 0, 0, null), model);
    }

    @PostMapping("/{id}/devoluciones")
    public String devolver(@PathVariable Long id, @Valid @ModelAttribute("devolucion") DevolucionRequest devolucion,
                           BindingResult binding, Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return vista(id, devolucion, model);
        }
        try {
            DevolucionResponse d = devolucionService.registrar(id, devolucion, auth.getName());
            String cierre = d.estadoPrestamo() == EstadoPrestamo.CERRADO
                    ? " Todas las unidades fueron justificadas: préstamo CERRADO."
                    : " Unidades pendientes: " + d.pendienteDespues() + ".";
            flash.addFlashAttribute("exito", "Devolución registrada (" + d.cantidadTotal() + " unidades)." + cierre);
            return "redirect:/encargado/prestamos/" + id;
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return vista(id, devolucion, model);
        }
    }

    private String vista(Long id, DevolucionRequest devolucion, Model model) {
        model.addAttribute("prestamo", prestamoService.obtener(id));
        model.addAttribute("devoluciones", devolucionService.listarPorPrestamo(id));
        model.addAttribute("devolucion", devolucion);
        return "encargado/prestamo-detalle";
    }
}
