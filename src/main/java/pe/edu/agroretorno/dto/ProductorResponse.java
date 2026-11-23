package pe.edu.agroretorno.dto;

import java.time.LocalDateTime;
import pe.edu.agroretorno.entity.Productor;

public record ProductorResponse(
        Long id,
        String username,
        String dni,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String telefono,
        String sector,
        boolean activo,
        LocalDateTime fechaRegistro) {

    public static ProductorResponse from(Productor p) {
        return new ProductorResponse(p.getId(), p.getUsuario().getUsername(), p.getDni(), p.getNombres(),
                p.getApellidos(), p.getNombreCompleto(), p.getTelefono(), p.getSector(),
                p.getUsuario().isActivo(), p.getFechaRegistro());
    }
}
