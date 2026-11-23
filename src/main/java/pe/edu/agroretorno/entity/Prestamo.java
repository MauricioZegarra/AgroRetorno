package pe.edu.agroretorno.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false, unique = true)
    private Solicitud solicitud;

    @Column(name = "cantidad_entregada", nullable = false)
    private int cantidadEntregada;

    @Column(name = "cantidad_devuelta", nullable = false)
    private int cantidadDevuelta;

    @Column(name = "cantidad_buen_estado", nullable = false)
    private int cantidadBuenEstado;

    @Column(name = "cantidad_danada", nullable = false)
    private int cantidadDanada;

    @Column(name = "cantidad_mantenimiento", nullable = false)
    private int cantidadMantenimiento;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDateTime fechaEntrega;

    @Column(name = "fecha_devolucion_prevista", nullable = false)
    private LocalDate fechaDevolucionPrevista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entregado_por", nullable = false)
    private Usuario entregadoPor;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    protected Prestamo() {
    }

    public Prestamo(Solicitud solicitud, int cantidadEntregada, LocalDateTime fechaEntrega, Usuario entregadoPor) {
        this.solicitud = solicitud;
        this.cantidadEntregada = cantidadEntregada;
        this.fechaEntrega = fechaEntrega;
        this.fechaDevolucionPrevista = solicitud.getFechaDevolucionPrevista();
        this.entregadoPor = entregadoPor;
        this.estado = EstadoPrestamo.ACTIVO;
    }

    /** Unidades entregadas que aún no han sido devueltas. */
    public int getCantidadPendiente() {
        return cantidadEntregada - cantidadDevuelta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Solicitud getSolicitud() { return solicitud; }
    public int getCantidadEntregada() { return cantidadEntregada; }
    public int getCantidadDevuelta() { return cantidadDevuelta; }
    public void setCantidadDevuelta(int cantidadDevuelta) { this.cantidadDevuelta = cantidadDevuelta; }
    public int getCantidadBuenEstado() { return cantidadBuenEstado; }
    public void setCantidadBuenEstado(int cantidadBuenEstado) { this.cantidadBuenEstado = cantidadBuenEstado; }
    public int getCantidadDanada() { return cantidadDanada; }
    public void setCantidadDanada(int cantidadDanada) { this.cantidadDanada = cantidadDanada; }
    public int getCantidadMantenimiento() { return cantidadMantenimiento; }
    public void setCantidadMantenimiento(int cantidadMantenimiento) { this.cantidadMantenimiento = cantidadMantenimiento; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public LocalDate getFechaDevolucionPrevista() { return fechaDevolucionPrevista; }
    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }
    public Usuario getEntregadoPor() { return entregadoPor; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
}
