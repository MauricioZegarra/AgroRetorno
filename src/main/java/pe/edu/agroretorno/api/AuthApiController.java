package pe.edu.agroretorno.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.agroretorno.dto.UsuarioActualResponse;

/** Verificación de credenciales de la API (HTTP Basic) para JMeter y pruebas (RF01). */
@RestController
public class AuthApiController {

    @GetMapping("/api/auth/me")
    public UsuarioActualResponse yo(Authentication auth) {
        return new UsuarioActualResponse(auth.getName(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
