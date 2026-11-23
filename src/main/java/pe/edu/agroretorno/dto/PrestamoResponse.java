package pe.edu.agroretorno.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.entity.Solicitud;

public record PrestamoResponse(
        Long id,
        Long solicitudId,
        Long productorId,
        String productorNombre,
        Long materialId,
        String materialNombre,
        String unidad,
        int cantidadEntregada,
        int cantidadDevuelta,
        int cantidadPendiente,
        int cantidadBuenEstado,
        int cantidadDanada,
        int cantidadMantenimiento,
        LocalDateTime fechaEntrega,
        LocalDate fechaDevolucionPrevista,
        EstadoPrestamo estado,
        boolean vencido,
        String entregadoPor,
        LocalDateTime fechaCierre) {

    /** @param hoy fecha actual para calcular si el préstamo activo está vencido */
    public static PrestamoResponse from(Prestamo p, LocalDate hoy) {
        Solicitud s = p.getSolicitud();
        boolean vencido = p.getEstado() == EstadoPrestamo.ACTIVO && p.getFechaDevolucionPrevista().isBefore(hoy);
        return new PrestamoResponse(p.getId(), s.getId(), s.getProductor().getId(),
                s.getProductor().getNombreCompleto(), s.getMaterial().getId(), s.getMaterial().getNombre(),
                s.getMaterial().getUnidad(), p.getCantidadEntregada(), p.getCantidadDevuelta(),
                p.getCantidadPendiente(), p.getCantidadBuenEstado(), p.getCantidadDanada(),
                p.getCantidadMantenimiento(), p.getFechaEntrega(), p.getFechaDevolucionPrevista(), p.getEstado(),
                vencido, p.getEntregadoPor().getNombreCompleto(), p.getFechaCierre());
    }
}
