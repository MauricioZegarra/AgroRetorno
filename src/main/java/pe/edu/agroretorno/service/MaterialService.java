package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.MaterialRequest;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.entity.EstadoMaterial;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.repository.MaterialRepository;

/** Registro y modificación de materiales (RF04). Las existencias se gestionan en InventarioService. */
@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final InventarioService inventarioService;
    private final UsuarioService usuarioService;
    private final HistorialService historialService;
    private final Clock clock;

    public MaterialService(MaterialRepository materialRepository, InventarioService inventarioService,
                           UsuarioService usuarioService, HistorialService historialService, Clock clock) {
        this.materialRepository = materialRepository;
        this.inventarioService = inventarioService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.clock = clock;
    }

    @Transactional
    public MaterialResponse registrar(MaterialRequest req, String username) {
        String codigo = req.getCodigo().trim().toUpperCase();
        if (materialRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un material con el código " + codigo);
        }
        Usuario actor = usuarioService.obtenerPorUsername(username);
        Material material = new Material(codigo, req.getNombre().trim(), req.getCategoria(),
                textoOpcional(req.getDescripcion()), req.getUnidad().trim(), LocalDateTime.now(clock));
        material.setEstado(req.getEstado() == null ? EstadoMaterial.ACTIVO : req.getEstado());
        material = materialRepository.save(material);
        int cantidadInicial = req.getCantidadInicial() == null ? 0 : req.getCantidadInicial();
        inventarioService.crearInventarioInicial(material, cantidadInicial, actor);
        historialService.registrar(username, "REGISTRAR_MATERIAL", "Material", material.getId(),
                codigo + " - " + material.getNombre() + " con " + cantidadInicial + " unidades");
        return MaterialResponse.from(material);
    }

    /** Modifica los datos descriptivos y el estado; las cantidades solo cambian mediante ajustes. */
    @Transactional
    public MaterialResponse actualizar(Long id, MaterialRequest req, String username) {
        Material material = materialRepository.findByIdConInventario(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Material", id));
        material.setNombre(req.getNombre().trim());
        material.setCategoria(req.getCategoria());
        material.setDescripcion(textoOpcional(req.getDescripcion()));
        material.setUnidad(req.getUnidad().trim());
        EstadoMaterial anterior = material.getEstado();
        if (req.getEstado() != null) {
            material.setEstado(req.getEstado());
        }
        materialRepository.save(material);
        historialService.registrar(username, "ACTUALIZAR_MATERIAL", "Material", material.getId(),
                material.getCodigo() + " - " + material.getNombre() + "; estado " + anterior + " -> "
                        + material.getEstado());
        return MaterialResponse.from(material);
    }

    @Transactional(readOnly = true)
    public MaterialResponse obtener(Long id) {
        return materialRepository.findByIdConInventario(id).map(MaterialResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Material", id));
    }

    private static String textoOpcional(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
