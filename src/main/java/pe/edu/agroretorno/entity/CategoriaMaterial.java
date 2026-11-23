package pe.edu.agroretorno.entity;

/** Cinco categorías de materiales agrícolas reutilizables. */
public enum CategoriaMaterial {
    JABAS_COSECHA("Jabas de cosecha"),
    CARRETILLAS("Carretillas"),
    BALANZAS("Balanzas"),
    FUMIGADORAS("Fumigadoras"),
    HERRAMIENTAS("Herramientas agrícolas");

    private final String etiqueta;

    CategoriaMaterial(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
