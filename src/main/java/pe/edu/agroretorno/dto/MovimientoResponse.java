package pe.edu.agroretorno.dto;

import java.time.LocalDateTime;
import pe.edu.agroretorno.entity.MovimientoInventario;
import pe.edu.agroretorno.entity.TipoMovimiento;

public record MovimientoResponse(
        Long id,
        TipoMovimiento tipo,
        String tipoEtiqueta,
        int cantidad,
        LocalDateTime fecha,
        String usuario,
        String referencia,
        String observacion) {

    public static MovimientoResponse from(MovimientoInventario m) {
        return new MovimientoResponse(m.getId(), m.getTipo(), m.getTipo().getEtiqueta(), m.getCantidad(),
                m.getFecha(), m.getUsuario().getUsername(), m.getReferencia(), m.getObservacion());
    }
}
