package pe.edu.agroretorno.entity;

/** Roles del sistema (RF02). */
public enum Rol {
    PRODUCTOR("Productor agrícola"),
    ENCARGADO("Encargado del almacén");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Nombre de autoridad que usa Spring Security (ROLE_PRODUCTOR / ROLE_ENCARGADO). */
    public String authority() {
        return "ROLE_" + name();
    }
}
