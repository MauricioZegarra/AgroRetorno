package pe.edu.agroretorno.dto;

import pe.edu.agroretorno.entity.EstadoMaterial;

/** Resultado de la consulta de disponibilidad (RF05). */
public record DisponibilidadResponse(
        Long materialId,
        String nombre,
        EstadoMaterial estado,
        int cantidadDisponible,
        int cantidadSolicitada,
        boolean disponible,
        String mensaje) {
}
