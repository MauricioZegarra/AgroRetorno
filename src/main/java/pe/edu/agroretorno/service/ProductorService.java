package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.ProductorRequest;
import pe.edu.agroretorno.dto.ProductorResponse;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Rol;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.ProductorRepository;
import pe.edu.agroretorno.repository.UsuarioRepository;

/** Registro y actualización de productores agrícolas por el encargado (RF03). */
@Service
public class ProductorService {

    static final int LONGITUD_MINIMA_CLAVE = 8;

    private final ProductorRepository productorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final HistorialService historialService;
    private final Clock clock;

    public ProductorService(ProductorRepository productorRepository, UsuarioRepository usuarioRepository,
                            PasswordEncoder passwordEncoder, HistorialService historialService, Clock clock) {
        this.productorRepository = productorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.historialService = historialService;
        this.clock = clock;
    }

    @Transactional
    public ProductorResponse registrar(ProductorRequest req, String actor) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim().toLowerCase();
        if (!username.matches("^[a-z0-9._]{4,30}$")) {
            throw new ValidacionException("Usuario: 4 a 30 caracteres en minúscula, números, punto o guion bajo");
        }
        validarClave(req.getPassword(), true);
        if (usuarioRepository.existsByUsername(username)) {
            throw new ReglaNegocioException("El usuario '" + username + "' ya existe");
        }
        if (productorRepository.existsByDni(req.getDni())) {
            throw new ReglaNegocioException("Ya existe un productor con DNI " + req.getDni());
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        // La contraseña se almacena únicamente como hash BCrypt (RNF02)
        Usuario usuario = new Usuario(username, passwordEncoder.encode(req.getPassword()),
                req.getNombres().trim() + " " + req.getApellidos().trim(), Rol.PRODUCTOR, ahora);
        usuario.setActivo(req.isActivo());
        usuario = usuarioRepository.save(usuario);
        Productor productor = productorRepository.save(new Productor(usuario, req.getDni(), req.getNombres().trim(),
                req.getApellidos().trim(), vacioANulo(req.getTelefono()), req.getSector().trim(), ahora));
        historialService.registrar(actor, "REGISTRAR_PRODUCTOR", "Productor", productor.getId(),
                productor.getNombreCompleto() + " (DNI " + productor.getDni() + ", usuario " + username + ")");
        return ProductorResponse.from(productor);
    }

    /** Actualiza datos de contacto y estado. El DNI y el usuario no se modifican. */
    @Transactional
    public ProductorResponse actualizar(Long id, ProductorRequest req, String actor) {
        Productor productor = productorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Productor", id));
        validarClave(req.getPassword(), false);
        productor.setNombres(req.getNombres().trim());
        productor.setApellidos(req.getApellidos().trim());
        productor.setTelefono(vacioANulo(req.getTelefono()));
        productor.setSector(req.getSector().trim());
        productor.setFechaActualizacion(LocalDateTime.now(clock));
        Usuario usuario = productor.getUsuario();
        usuario.setNombreCompleto(productor.getNombreCompleto());
        usuario.setActivo(req.isActivo());
        boolean cambioClave = req.getPassword() != null && !req.getPassword().isBlank();
        if (cambioClave) {
            usuario.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        productorRepository.save(productor);
        historialService.registrar(actor, "ACTUALIZAR_PRODUCTOR", "Productor", productor.getId(),
                productor.getNombreCompleto() + "; activo=" + usuario.isActivo()
                        + (cambioClave ? "; contraseña restablecida" : ""));
        return ProductorResponse.from(productor);
    }

    @Transactional(readOnly = true)
    public List<ProductorResponse> listar(String texto) {
        return productorRepository.buscar(InventarioService.normalizar(texto)).stream()
                .map(ProductorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductorResponse obtener(Long id) {
        return productorRepository.findById(id).map(ProductorResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Productor", id));
    }

    @Transactional(readOnly = true)
    public ProductorResponse obtenerPorUsername(String username) {
        return productorRepository.findByUsername(username).map(ProductorResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Productor", username));
    }

    private static void validarClave(String clave, boolean obligatoria) {
        boolean vacia = clave == null || clave.isBlank();
        if (vacia && obligatoria) {
            throw new ValidacionException("Indique una contraseña inicial");
        }
        if (!vacia && clave.length() < LONGITUD_MINIMA_CLAVE) {
            throw new ValidacionException("La contraseña debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres");
        }
    }

    private static String vacioANulo(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
