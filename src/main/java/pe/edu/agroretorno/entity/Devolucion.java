package pe.edu.agroretorno.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recibido_por", nullable = false)
    private Usuario recibidoPor;

    @Column(length = 500)
    private String observaciones;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucion> detalles = new ArrayList<>();

    protected Devolucion() {
    }

    public Devolucion(Prestamo prestamo, LocalDateTime fecha, Usuario recibidoPor, String observaciones) {
        this.prestamo = prestamo;
        this.fecha = fecha;
        this.recibidoPor = recibidoPor;
        this.observaciones = observaciones;
    }

    /** Agrega un detalle solo si la cantidad es positiva. */
    public void agregarDetalle(CondicionDevolucion condicion, int cantidad) {
        if (cantidad > 0) {
            detalles.add(new DetalleDevolucion(this, condicion, cantidad));
        }
    }

    public int cantidadPor(CondicionDevolucion condicion) {
        return detalles.stream()
                .filter(d -> d.getEstadoMaterial() == condicion)
                .mapToInt(DetalleDevolucion::getCantidad)
                .sum();
    }

    public int getCantidadTotal() {
        return detalles.stream().mapToInt(DetalleDevolucion::getCantidad).sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Prestamo getPrestamo() { return prestamo; }
    public LocalDateTime getFecha() { return fecha; }
    public Usuario getRecibidoPor() { return recibidoPor; }
    public String getObservaciones() { return observaciones; }
    public List<DetalleDevolucion> getDetalles() { return detalles; }
}
