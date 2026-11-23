package pe.edu.agroretorno.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato JSON uniforme de errores de la API. */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        Map<String, String> errores) {
}
