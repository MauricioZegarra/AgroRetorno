package pe.edu.agroretorno.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Reglas de negocio parametrizables (G02: permiten aplicar solicitudes de cambio
 * sin reescribir los servicios). Se configuran en application.properties con el prefijo
 * "agroretorno.reglas".
 */
@ConfigurationProperties(prefix = "agroretorno.reglas")
public class ReglasNegocioProperties {

    /** Días mínimos entre hoy y la fecha prevista de devolución. */
    private int plazoMinimoDias = 1;

    /** Días máximos entre hoy y la fecha prevista de devolución. */
    private int plazoMaximoDias = 90;

    /** Caracteres mínimos para el motivo de una solicitud o de un rechazo. */
    private int longitudMinimaMotivo = 5;

    public int getPlazoMinimoDias() { return plazoMinimoDias; }
    public void setPlazoMinimoDias(int plazoMinimoDias) { this.plazoMinimoDias = plazoMinimoDias; }
    public int getPlazoMaximoDias() { return plazoMaximoDias; }
    public void setPlazoMaximoDias(int plazoMaximoDias) { this.plazoMaximoDias = plazoMaximoDias; }
    public int getLongitudMinimaMotivo() { return longitudMinimaMotivo; }
    public void setLongitudMinimaMotivo(int longitudMinimaMotivo) { this.longitudMinimaMotivo = longitudMinimaMotivo; }
}
