package pe.edu.agroretorno.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pe.edu.agroretorno.entity.TipoMovimiento;

/** Ajuste manual de existencias por el encargado (RF04). */
public class AjusteExistenciasRequest {

    @NotNull(message = "Seleccione el tipo de ajuste")
    private TipoMovimiento tipo;

    @NotNull(message = "Indique la cantidad")
    @Positive(message = "La cantidad debe ser un número entero positivo")
    @Max(value = 100000, message = "Cantidad fuera de rango")
    private Integer cantidad;

    @NotBlank(message = "Indique el motivo del ajuste")
    @Size(max = 300, message = "Máximo 300 caracteres")
    private String observacion;

    public AjusteExistenciasRequest() {
    }

    public AjusteExistenciasRequest(TipoMovimiento tipo, Integer cantidad, String observacion) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
