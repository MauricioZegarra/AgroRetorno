package pe.edu.agroretorno.dto;

import java.time.LocalDateTime;
import pe.edu.agroretorno.entity.CondicionDevolucion;
import pe.edu.agroretorno.entity.Devolucion;
import pe.edu.agroretorno.entity.EstadoPrestamo;

public record DevolucionResponse(
        Long id,
        Long prestamoId,
        String materialNombre,
        LocalDateTime fecha,
        String recibidoPor,
        String observaciones,
        int cantidadBuenEstado,
        int cantidadDanada,
        int cantidadMantenimiento,
        int cantidadTotal,
        int pendienteDespues,
        EstadoPrestamo estadoPrestamo) {

    public static DevolucionResponse from(Devolucion d) {
        var p = d.getPrestamo();
        return new DevolucionResponse(d.getId(), p.getId(), p.getSolicitud().getMaterial().getNombre(),
                d.getFecha(), d.getRecibidoPor().getNombreCompleto(), d.getObservaciones(),
                d.cantidadPor(CondicionDevolucion.BUEN_ESTADO), d.cantidadPor(CondicionDevolucion.DANADO),
                d.cantidadPor(CondicionDevolucion.REQUIERE_MANTENIMIENTO), d.getCantidadTotal(),
                p.getCantidadPendiente(), p.getEstado());
    }
}
