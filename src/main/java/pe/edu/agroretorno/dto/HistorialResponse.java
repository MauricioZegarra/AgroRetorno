package pe.edu.agroretorno.dto;

import java.time.LocalDateTime;
import pe.edu.agroretorno.entity.HistorialOperacion;

public record HistorialResponse(
        Long id,
        LocalDateTime fechaHora,
        String actor,
        String accion,
        String entidad,
        Long entidadId,
        String detalle) {

    public static HistorialResponse from(HistorialOperacion h) {
        return new HistorialResponse(h.getId(), h.getFechaHora(), h.getActor(), h.getAccion(), h.getEntidad(),
                h.getEntidadId(), h.getDetalle());
    }
}
