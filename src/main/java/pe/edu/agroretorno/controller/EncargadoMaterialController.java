package pe.edu.agroretorno.controller;

import jakarta.validation.Valid;
import java.util.List;
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
import pe.edu.agroretorno.dto.AjusteExistenciasRequest;
import pe.edu.agroretorno.dto.MaterialRequest;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoMaterial;
import pe.edu.agroretorno.entity.TipoMovimiento;
import pe.edu.agroretorno.exception.AgroRetornoException;
import pe.edu.agroretorno.service.InventarioService;
import pe.edu.agroretorno.service.MaterialService;
import pe.edu.agroretorno.service.ReporteService;

/** RF04: gestión de materiales y de sus existencias. */
@Controller
@RequestMapping("/encargado")
@PreAuthorize("hasRole('ENCARGADO')")
public class EncargadoMaterialController {

    /** Tipos de ajuste manual permitidos (los demás movimientos los generan entregas y devoluciones). */
    static final List<TipoMovimiento> TIPOS_AJUSTE = List.of(TipoMovimiento.INGRESO, TipoMovimiento.RETIRO,
            TipoMovimiento.ENVIO_MANTENIMIENTO, TipoMovimiento.REPARACION, TipoMovimiento.BAJA_DANADO);

    private final MaterialService materialService;
    private final InventarioService inventarioService;

    public EncargadoMaterialController(MaterialService materialService, InventarioService inventarioService) {
        this.materialService = materialService;
        this.inventarioService = inventarioService;
    }

    // ---------------------------------------------------------------- materiales

    @GetMapping("/materiales")
    public String listar(@RequestParam(name = "q", required = false) String q,
                         @RequestParam(name = "categoria", required = false) CategoriaMaterial categoria,
                         Model model) {
        model.addAttribute("materiales", inventarioService.listarInventario(q, categoria, false));
        model.addAttribute("q", q);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("categorias", CategoriaMaterial.values());
        return "encargado/materiales";
    }

    @GetMapping("/materiales/nuevo")
    public String nuevo(Model model) {
        return formulario(new MaterialRequest(), null, model);
    }

    @PostMapping("/materiales")
    public String registrar(@Valid @ModelAttribute("material") MaterialRequest material, BindingResult binding,
                            Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return formulario(material, null, model);
        }
        try {
            MaterialResponse m = materialService.registrar(material, auth.getName());
            flash.addFlashAttribute("exito", "Material " + m.codigo() + " registrado con " + m.cantidadTotal()
                    + " unidades");
            return "redirect:/encargado/materiales";
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(material, null, model);
        }
    }

    @GetMapping("/materiales/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        MaterialResponse m = materialService.obtener(id);
        MaterialRequest req = new MaterialRequest();
        req.setCodigo(m.codigo());
        req.setNombre(m.nombre());
        req.setCategoria(m.categoria());
        req.setDescripcion(m.descripcion());
        req.setUnidad(m.unidad());
        req.setEstado(m.estado());
        req.setCantidadInicial(m.cantidadTotal());
        return formulario(req, id, model);
    }

    @PostMapping("/materiales/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("material") MaterialRequest material,
                             BindingResult binding, Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return formulario(material, id, model);
        }
        try {
            MaterialResponse m = materialService.actualizar(id, material, auth.getName());
            flash.addFlashAttribute("exito", "Material " + m.codigo() + " actualizado");
            return "redirect:/encargado/materiales";
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(material, id, model);
        }
    }

    // ---------------------------------------------------------------- existencias

    @GetMapping("/existencias")
    public String existencias(Model model) {
        List<MaterialResponse> materiales = inventarioService.listarInventario(null, false);
        model.addAttribute("materiales", materiales);
        model.addAttribute("resumen", ReporteService.resumirInventario(materiales));
        return "encargado/existencias";
    }

    @GetMapping("/existencias/{id}")
    public String detalleExistencias(@PathVariable Long id, Model model) {
        AjusteExistenciasRequest ajuste = new AjusteExistenciasRequest();
        ajuste.setTipo(TipoMovimiento.INGRESO);
        return detalle(id, ajuste, model);
    }

    @PostMapping("/existencias/{id}/ajustes")
    public String ajustar(@PathVariable Long id, @Valid @ModelAttribute("ajuste") AjusteExistenciasRequest ajuste,
                          BindingResult binding, Authentication auth, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return detalle(id, ajuste, model);
        }
        try {
            MaterialResponse m = inventarioService.ajustarExistencias(id, ajuste, auth.getName());
            flash.addFlashAttribute("exito", "Ajuste registrado. Disponibles: " + m.cantidadDisponible()
                    + " de " + m.cantidadTotal());
            return "redirect:/encargado/existencias/" + id;
        } catch (AgroRetornoException e) {
            model.addAttribute("error", e.getMessage());
            return detalle(id, ajuste, model);
        }
    }

    private String detalle(Long id, AjusteExistenciasRequest ajuste, Model model) {
        model.addAttribute("material", materialService.obtener(id));
        model.addAttribute("movimientos", inventarioService.listarMovimientos(id));
        model.addAttribute("ajuste", ajuste);
        model.addAttribute("tiposAjuste", TIPOS_AJUSTE);
        return "encargado/existencias-detalle";
    }

    private String formulario(MaterialRequest req, Long id, Model model) {
        model.addAttribute("material", req);
        model.addAttribute("materialId", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("categorias", CategoriaMaterial.values());
        model.addAttribute("estados", EstadoMaterial.values());
        return "encargado/material-form";
    }
}
