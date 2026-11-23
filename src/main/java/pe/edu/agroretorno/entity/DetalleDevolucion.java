package pe.edu.agroretorno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_devolucion")
public class DetalleDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devolucion devolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_material", nullable = false, length = 25)
    private CondicionDevolucion estadoMaterial;

    @Column(nullable = false)
    private int cantidad;

    protected DetalleDevolucion() {
    }

    public DetalleDevolucion(Devolucion devolucion, CondicionDevolucion estadoMaterial, int cantidad) {
        this.devolucion = devolucion;
        this.estadoMaterial = estadoMaterial;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public Devolucion getDevolucion() { return devolucion; }
    public CondicionDevolucion getEstadoMaterial() { return estadoMaterial; }
    public int getCantidad() { return cantidad; }
}
