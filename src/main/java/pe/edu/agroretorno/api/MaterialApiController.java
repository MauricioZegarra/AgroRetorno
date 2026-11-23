package pe.edu.agroretorno.api;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.agroretorno.dto.DisponibilidadResponse;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.service.InventarioService;
import pe.edu.agroretorno.service.MaterialService;

/** Transacción crítica 1 (JMeter): consulta de inventario y disponibilidad (RF05). */
@RestController
@RequestMapping("/api/materiales")
public class MaterialApiController {

    private final InventarioService inventarioService;
    private final MaterialService materialService;

    public MaterialApiController(InventarioService inventarioService, MaterialService materialService) {
        this.inventarioService = inventarioService;
        this.materialService = materialService;
    }

    /** El productor solo ve materiales activos; el encargado ve todo el inventario. */
    @GetMapping
    public List<MaterialResponse> listar(@RequestParam(name = "q", required = false) String q, Authentication auth) {
        boolean soloActivos = auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ENCARGADO"));
        return inventarioService.listarInventario(q, soloActivos);
    }

    @GetMapping("/{id}")
    public MaterialResponse obtener(@PathVariable Long id) {
        return materialService.obtener(id);
    }

    @GetMapping("/{id}/disponibilidad")
    public DisponibilidadResponse disponibilidad(@PathVariable Long id,
                                                 @RequestParam(name = "cantidad", defaultValue = "1") int cantidad) {
        return inventarioService.consultarDisponibilidad(id, cantidad);
    }
}
