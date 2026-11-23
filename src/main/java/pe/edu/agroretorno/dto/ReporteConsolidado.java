package pe.edu.agroretorno.dto;

import java.util.List;
import java.util.Map;
import pe.edu.agroretorno.entity.EstadoSolicitud;

/** Reporte consolidado de préstamos, devoluciones y materiales pendientes (RF10). */
public record ReporteConsolidado(
        ResumenInventario inventario,
        Map<EstadoSolicitud, Long> solicitudesPorEstado,
        long prestamosActivos,
        long prestamosCerrados,
        long devolucionesRegistradas,
        int unidadesPendientesDevolucion,
        List<MaterialResponse> materiales,
        List<PrestamoResponse> pendientesDevolucion,
        List<SolicitudResponse> solicitudes,
        List<PrestamoResponse> prestamos,
        List<DevolucionResponse> devoluciones) {

    public record ResumenInventario(
            int materialesRegistrados,
            int cantidadTotal,
            int cantidadDisponible,
            int cantidadPrestada,
            int cantidadDanada,
            int cantidadMantenimiento) {
    }
}
