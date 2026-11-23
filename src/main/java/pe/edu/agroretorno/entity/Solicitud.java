package pe.edu.agroretorno.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productor_id", nullable = false)
    private Productor productor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_devolucion_prevista", nullable = false)
    private LocalDate fechaDevolucionPrevista;

    @Column(nullable = false, length = 300)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    /** Encargado que aprobó o rechazó la solicitud. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondido_por")
    private Usuario respondidoPor;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "motivo_rechazo", length = 300)
    private String motivoRechazo;

    @OneToOne(mappedBy = "solicitud", fetch = FetchType.LAZY)
    private Prestamo prestamo;

    protected Solicitud() {
    }

    public Solicitud(Productor productor, Material material, int cantidad, LocalDateTime fechaSolicitud,
                     LocalDate fechaDevolucionPrevista, String motivo) {
        this.productor = productor;
        this.material = material;
        this.cantidad = cantidad;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.motivo = motivo;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Productor getProductor() { return productor; }
    public Material getMaterial() { return material; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public LocalDate getFechaDevolucionPrevista() { return fechaDevolucionPrevista; }
    public String getMotivo() { return motivo; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public Usuario getRespondidoPor() { return respondidoPor; }
    public void setRespondidoPor(Usuario respondidoPor) { this.respondidoPor = respondidoPor; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public Prestamo getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }
}
