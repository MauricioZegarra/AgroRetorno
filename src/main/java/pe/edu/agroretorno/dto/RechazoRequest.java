package pe.edu.agroretorno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Motivo de rechazo de una solicitud (RF07). */
public class RechazoRequest {

    @NotBlank(message = "Indique el motivo del rechazo")
    @Size(max = 300, message = "El motivo no puede superar 300 caracteres")
    private String motivo;

    public RechazoRequest() {
    }

    public RechazoRequest(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
