package pe.edu.agroretorno.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Filtros de consulta del historial de operaciones (RF10). */
public class FiltroHistorial {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;

    private String actor;
    private String entidad;
    private String accion;

    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
}
