package pe.edu.agroretorno.entity;

/**
 * Ciclo de vida de una solicitud:
 * PENDIENTE -> APROBADA -> ENTREGADA
 * PENDIENTE -> RECHAZADA
 */
public enum EstadoSolicitud {
    PENDIENTE, APROBADA, RECHAZADA, ENTREGADA
}
