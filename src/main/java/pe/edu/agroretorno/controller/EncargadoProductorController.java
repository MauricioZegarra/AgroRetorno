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
import pe.edu.agroretorno.dto.ProductorRequest;
import pe.edu.agroretorno.dto.ProductorResponse;
import pe.edu.agroretorno.exception.AgroRetornoException;
import pe.edu.agroretorno.service.ProductorService;

/** RF03: registrar, actualizar y consultar productores agrícolas. */
@Controller
@RequestMapping("/encargado/productores")
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoProductorController {

    private final ProductorService productorService;

    public EncargadoProductorController(ProductorService productorService) {
        this.productorService = productorService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("productores", productorService.listar(q));
        model.addAttribute("q", q);
        return "encargado/productores";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return formulario(new ProductorRequest(), null, model);
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("productor") ProductorRequest productor, BindingResult binding,
                            Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return formulario(productor, null, model);
        }
        try {
            ProductorResponse creado = productorService.registrar(productor, auth.getName());
            flash.addFlashAttribute("exito", "Productor " + creado.nombreCompleto() + " registrado correctamente");
            return "redirect:/encargado/productores";
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(productor, null, model);
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        ProductorResponse p = productorService.obtener(id);
        ProductorRequest req = new ProductorRequest();
        req.setUsername(p.username());
        req.setDni(p.dni());
        req.setNombres(p.nombres());
        req.setApellidos(p.apellidos());
        req.setTelefono(p.telefono());
        req.setSector(p.sector());
        req.setActivo(p.activo());
        return formulario(req, id, model);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("productor") ProductorRequest productor,
                             BindingResult binding, Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return formulario(productor, id, model);
        }
        try {
            ProductorResponse actualizado = productorService.actualizar(id, productor, auth.getName());
            flash.addFlashAttribute("exito", "Productor " + actualizado.nombreCompleto() + " actualizado");
            return "redirect:/encargado/productores";
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(productor, id, model);
        }
    }

    private String formulario(ProductorRequest req, Long id, Model model) {
        if (id != null && req.getUsername() == null) {
            // El usuario no se envía al editar (campo deshabilitado); se recupera para mostrarlo
            req.setUsername(productorService.obtener(id).username());
        }
        model.addAttribute("productor", req);
        model.addAttribute("productorId", id);
        model.addAttribute("edicion", id != null);
        return "encargado/productor-form";
    }
}
