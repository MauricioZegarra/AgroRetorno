package pe.edu.agroretorno.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Existencias de un material.
 * Invariante: total = disponible + prestada + dañada + mantenimiento
 * (también protegida con un CHECK en PostgreSQL).
 */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false, unique = true)
    private Material material;

    @Column(name = "cantidad_total", nullable = false)
    private int cantidadTotal;

    @Column(name = "cantidad_disponible", nullable = false)
    private int cantidadDisponible;

    @Column(name = "cantidad_prestada", nullable = false)
    private int cantidadPrestada;

    @Column(name = "cantidad_danada", nullable = false)
    private int cantidadDanada;

    @Column(name = "cantidad_mantenimiento", nullable = false)
    private int cantidadMantenimiento;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected Inventario() {
    }

    public Inventario(Material material, int cantidadInicial, LocalDateTime fecha) {
        this.material = material;
        this.cantidadTotal = cantidadInicial;
        this.cantidadDisponible = cantidadInicial;
        this.fechaActualizacion = fecha;
    }

    /** Comprueba la igualdad de cuadre de existencias. */
    public boolean estaCuadrado() {
        return cantidadTotal == cantidadDisponible + cantidadPrestada + cantidadDanada + cantidadMantenimiento
                && cantidadTotal >= 0 && cantidadDisponible >= 0 && cantidadPrestada >= 0
                && cantidadDanada >= 0 && cantidadMantenimiento >= 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Material getMaterial() { return material; }
    public int getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(int cantidadTotal) { this.cantidadTotal = cantidadTotal; }
    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public int getCantidadPrestada() { return cantidadPrestada; }
    public void setCantidadPrestada(int cantidadPrestada) { this.cantidadPrestada = cantidadPrestada; }
    public int getCantidadDanada() { return cantidadDanada; }
    public void setCantidadDanada(int cantidadDanada) { this.cantidadDanada = cantidadDanada; }
    public int getCantidadMantenimiento() { return cantidadMantenimiento; }
    public void setCantidadMantenimiento(int cantidadMantenimiento) { this.cantidadMantenimiento = cantidadMantenimiento; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
