package pe.edu.agroretorno.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Unidades devueltas clasificadas por condición (RF09). */
public class DevolucionRequest {

    @NotNull(message = "Indique las unidades en buen estado (0 si no hay)")
    @PositiveOrZero(message = "Las cantidades no pueden ser negativas")
    @Max(value = 10000, message = "Cantidad fuera de rango")
    private Integer cantidadBuenEstado = 0;

    @NotNull(message = "Indique las unidades dañadas (0 si no hay)")
    @PositiveOrZero(message = "Las cantidades no pueden ser negativas")
    @Max(value = 10000, message = "Cantidad fuera de rango")
    private Integer cantidadDanada = 0;

    @NotNull(message = "Indique las unidades que requieren mantenimiento (0 si no hay)")
    @PositiveOrZero(message = "Las cantidades no pueden ser negativas")
    @Max(value = 10000, message = "Cantidad fuera de rango")
    private Integer cantidadMantenimiento = 0;

    @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
    private String observaciones;

    public DevolucionRequest() {
    }

    public DevolucionRequest(Integer cantidadBuenEstado, Integer cantidadDanada, Integer cantidadMantenimiento,
                             String observaciones) {
        this.cantidadBuenEstado = cantidadBuenEstado;
        this.cantidadDanada = cantidadDanada;
        this.cantidadMantenimiento = cantidadMantenimiento;
        this.observaciones = observaciones;
    }

    public Integer getCantidadBuenEstado() { return cantidadBuenEstado; }
    public void setCantidadBuenEstado(Integer cantidadBuenEstado) { this.cantidadBuenEstado = cantidadBuenEstado; }
    public Integer getCantidadDanada() { return cantidadDanada; }
    public void setCantidadDanada(Integer cantidadDanada) { this.cantidadDanada = cantidadDanada; }
    public Integer getCantidadMantenimiento() { return cantidadMantenimiento; }
    public void setCantidadMantenimiento(Integer cantidadMantenimiento) { this.cantidadMantenimiento = cantidadMantenimiento; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
