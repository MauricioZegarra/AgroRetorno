package pe.edu.agroretorno.entity;

/** Tipos de movimiento de existencias registrados en movimientos_inventario. */
public enum TipoMovimiento {
    ALTA_INICIAL("Alta inicial"),
    INGRESO("Ingreso de unidades"),
    RETIRO("Retiro de unidades disponibles"),
    ENVIO_MANTENIMIENTO("Envío a mantenimiento"),
    REPARACION("Retorno de mantenimiento"),
    BAJA_DANADO("Baja de unidades dañadas"),
    ENTREGA("Entrega por préstamo"),
    DEVOLUCION_BUEN_ESTADO("Devolución en buen estado"),
    DEVOLUCION_DANADO("Devolución dañada"),
    DEVOLUCION_MANTENIMIENTO("Devolución para mantenimiento");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
