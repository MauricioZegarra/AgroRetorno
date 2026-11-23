package pe.edu.agroretorno.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Solicitud;

public record SolicitudResponse(
        Long id,
        Long productorId,
        String productorNombre,
        String productorDni,
        Long materialId,
        String materialCodigo,
        String materialNombre,
        String unidad,
        int cantidad,
        LocalDateTime fechaSolicitud,
        LocalDate fechaDevolucionPrevista,
        String motivo,
        EstadoSolicitud estado,
        String respondidoPor,
        LocalDateTime fechaRespuesta,
        String motivoRechazo) {

    public static SolicitudResponse from(Solicitud s) {
        return new SolicitudResponse(s.getId(), s.getProductor().getId(), s.getProductor().getNombreCompleto(),
                s.getProductor().getDni(), s.getMaterial().getId(), s.getMaterial().getCodigo(),
                s.getMaterial().getNombre(), s.getMaterial().getUnidad(), s.getCantidad(), s.getFechaSolicitud(),
                s.getFechaDevolucionPrevista(), s.getMotivo(), s.getEstado(),
                s.getRespondidoPor() == null ? null : s.getRespondidoPor().getNombreCompleto(),
                s.getFechaRespuesta(), s.getMotivoRechazo());
    }
}
