package pe.edu.agroretorno.dto;

import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoMaterial;
import pe.edu.agroretorno.entity.Inventario;
import pe.edu.agroretorno.entity.Material;

/** Material con sus existencias (RF04, RF05). */
public record MaterialResponse(
        Long id,
        String codigo,
        String nombre,
        CategoriaMaterial categoria,
        String categoriaEtiqueta,
        String descripcion,
        String unidad,
        EstadoMaterial estado,
        int cantidadTotal,
        int cantidadDisponible,
        int cantidadPrestada,
        int cantidadDanada,
        int cantidadMantenimiento) {

    public static MaterialResponse from(Material m) {
        Inventario i = m.getInventario();
        return new MaterialResponse(m.getId(), m.getCodigo(), m.getNombre(), m.getCategoria(),
                m.getCategoria().getEtiqueta(), m.getDescripcion(), m.getUnidad(), m.getEstado(),
                i == null ? 0 : i.getCantidadTotal(),
                i == null ? 0 : i.getCantidadDisponible(),
                i == null ? 0 : i.getCantidadPrestada(),
                i == null ? 0 : i.getCantidadDanada(),
                i == null ? 0 : i.getCantidadMantenimiento());
    }

    public boolean activo() {
        return estado == EstadoMaterial.ACTIVO;
    }

    /** Porcentaje disponible respecto al total (para el indicador visual). */
    public int porcentajeDisponible() {
        return cantidadTotal == 0 ? 0 : (int) Math.round(cantidadDisponible * 100.0 / cantidadTotal);
    }
}
