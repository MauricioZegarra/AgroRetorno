package pe.edu.agroretorno.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 100)
    private String referencia;

    @Column(length = 300)
    private String observacion;

    protected MovimientoInventario() {
    }

    public MovimientoInventario(Material material, TipoMovimiento tipo, int cantidad, LocalDateTime fecha,
                                Usuario usuario, String referencia, String observacion) {
        this.material = material;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.usuario = usuario;
        this.referencia = referencia;
        this.observacion = observacion;
    }

    public Long getId() { return id; }
    public Material getMaterial() { return material; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFecha() { return fecha; }
    public Usuario getUsuario() { return usuario; }
    public String getReferencia() { return referencia; }
    public String getObservacion() { return observacion; }
}
