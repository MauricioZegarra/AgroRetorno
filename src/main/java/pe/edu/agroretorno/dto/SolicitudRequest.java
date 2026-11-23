package pe.edu.agroretorno.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Entrada para registrar una solicitud de préstamo (RF06). Usada por la web y por la API. */
public class SolicitudRequest {

    @NotNull(message = "Seleccione un material")
    private Long materialId;

    @NotNull(message = "Indique la cantidad")
    @Positive(message = "La cantidad debe ser un número entero positivo")
    @Max(value = 10000, message = "La cantidad no puede superar 10000 unidades")
    private Integer cantidad;

    @NotNull(message = "Indique la fecha prevista de devolución")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDevolucionPrevista;

    @NotBlank(message = "Indique el motivo de la solicitud")
    @Size(max = 300, message = "El motivo no puede superar 300 caracteres")
    private String motivo;

    public SolicitudRequest() {
    }

    public SolicitudRequest(Long materialId, Integer cantidad, LocalDate fechaDevolucionPrevista, String motivo) {
        this.materialId = materialId;
        this.cantidad = cantidad;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.motivo = motivo;
    }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public LocalDate getFechaDevolucionPrevista() { return fechaDevolucionPrevista; }
    public void setFechaDevolucionPrevista(LocalDate fechaDevolucionPrevista) { this.fechaDevolucionPrevista = fechaDevolucionPrevista; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
