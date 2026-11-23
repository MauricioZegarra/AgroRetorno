package pe.edu.agroretorno.entity;

/** Estado en que se reciben las unidades devueltas (RF09). */
public enum CondicionDevolucion {
    BUEN_ESTADO("Buen estado"),
    DANADO("Dañado"),
    REQUIERE_MANTENIMIENTO("Requiere mantenimiento");

    private final String etiqueta;

    CondicionDevolucion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
