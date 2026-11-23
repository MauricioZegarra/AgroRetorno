package pe.edu.agroretorno.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import pe.edu.agroretorno.entity.EstadoSolicitud;

/** Filtros del reporte consolidado (RF10): periodo, productor, material y estado. */
public class FiltroReporte {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;

    private Long productorId;
    private Long materialId;
    private EstadoSolicitud estado;

    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public Long getProductorId() { return productorId; }
    public void setProductorId(Long productorId) { this.productorId = productorId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}
