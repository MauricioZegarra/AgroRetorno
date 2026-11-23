package pe.edu.agroretorno.dto;

import java.util.List;

/** Respuesta de /api/auth/me: confirma la autenticación de la API. */
public record UsuarioActualResponse(String username, List<String> roles) {
}
