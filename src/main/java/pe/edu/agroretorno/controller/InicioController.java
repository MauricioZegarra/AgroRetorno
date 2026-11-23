package pe.edu.agroretorno.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Inicio de sesión (RF01) y redirección al panel según el rol (RF02). */
@Controller
public class InicioController {

    @GetMapping("/login")
    public String login(Authentication auth) {
        if (estaAutenticado(auth)) {
            return "redirect:/panel";
        }
        return "login";
    }

    @GetMapping({"/", "/panel"})
    public String panel(Authentication auth) {
        boolean encargado = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENCARGADO"));
        return encargado ? "redirect:/encargado/panel" : "redirect:/productor/panel";
    }

    private static boolean estaAutenticado(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
}
