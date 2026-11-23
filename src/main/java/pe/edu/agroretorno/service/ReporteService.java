package pe.edu.agroretorno.service;

import jakarta.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.DevolucionResponse;
import pe.edu.agroretorno.dto.FiltroReporte;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.ReporteConsolidado;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.Devolucion;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.entity.Solicitud;
import pe.edu.agroretorno.repository.DevolucionRepository;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.PrestamoRepository;
import pe.edu.agroretorno.repository.SolicitudRepository;

/**
 * Reporte consolidado de préstamos, devoluciones y materiales pendientes (RF10).
 * Filtros: periodo (desde/hasta), productor, material y estado de solicitud.
 */
@Service
public class ReporteService {

    private final MaterialRepository materialRepository;
    private final SolicitudRepository solicitudRepository;
    private final PrestamoRepository prestamoRepository;
    private final DevolucionRepository devolucionRepository;
    private final Clock clock;

    public ReporteService(MaterialRepository materialRepository, SolicitudRepository solicitudRepository,
                          PrestamoRepository prestamoRepository, DevolucionRepository devolucionRepository,
                          Clock clock) {
        this.materialRepository = materialRepository;
        this.solicitudRepository = solicitudRepository;
        this.prestamoRepository = prestamoRepository;
        this.devolucionRepository = devolucionRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ReporteConsolidado generar(FiltroReporte filtro) {
        FiltroReporte f = filtro == null ? new FiltroReporte() : filtro;
        LocalDate hoy = LocalDate.now(clock);

        List<MaterialResponse> materiales = materialRepository.buscarConInventario("").stream()
                .filter(m -> f.getMaterialId() == null || m.getId().equals(f.getMaterialId()))
                .map(MaterialResponse::from).toList();

        List<SolicitudResponse> solicitudes = solicitudRepository
                .findAll(especificacionSolicitudes(f), Sort.by(Sort.Direction.DESC, "fechaSolicitud"))
                .stream().map(SolicitudResponse::from).toList();
        Map<EstadoSolicitud, Long> porEstado = new EnumMap<>(EstadoSolicitud.class);
        for (EstadoSolicitud e : EstadoSolicitud.values()) {
            porEstado.put(e, solicitudes.stream().filter(s -> s.estado() == e).count());
        }

        List<PrestamoResponse> prestamos = prestamoRepository
                .findAll(especificacionPrestamos(f), Sort.by(Sort.Direction.DESC, "fechaEntrega"))
                .stream().map(p -> PrestamoResponse.from(p, hoy)).toList();
        // Materiales pendientes de devolución: préstamos activos (sin filtro de periodo)
        List<PrestamoResponse> pendientes = prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO).stream()
                .filter(p -> coincideProductorMaterial(p.getSolicitud(), f))
                .map(p -> PrestamoResponse.from(p, hoy)).toList();
        List<DevolucionResponse> devoluciones = devolucionRepository
                .findAll(especificacionDevoluciones(f), Sort.by(Sort.Direction.DESC, "fecha"))
                .stream().map(DevolucionResponse::from).toList();

        return new ReporteConsolidado(
                resumirInventario(materiales),
                porEstado,
                prestamos.stream().filter(p -> p.estado() == EstadoPrestamo.ACTIVO).count(),
                prestamos.stream().filter(p -> p.estado() == EstadoPrestamo.CERRADO).count(),
                devoluciones.size(),
                pendientes.stream().mapToInt(PrestamoResponse::cantidadPendiente).sum(),
                materiales, pendientes, solicitudes, prestamos, devoluciones);
    }

    public static ReporteConsolidado.ResumenInventario resumirInventario(List<MaterialResponse> materiales) {
        return new ReporteConsolidado.ResumenInventario(
                materiales.size(),
                materiales.stream().mapToInt(MaterialResponse::cantidadTotal).sum(),
                materiales.stream().mapToInt(MaterialResponse::cantidadDisponible).sum(),
                materiales.stream().mapToInt(MaterialResponse::cantidadPrestada).sum(),
                materiales.stream().mapToInt(MaterialResponse::cantidadDanada).sum(),
                materiales.stream().mapToInt(MaterialResponse::cantidadMantenimiento).sum());
    }

    private static boolean coincideProductorMaterial(Solicitud s, FiltroReporte f) {
        return (f.getProductorId() == null || s.getProductor().getId().equals(f.getProductorId()))
                && (f.getMaterialId() == null || s.getMaterial().getId().equals(f.getMaterialId()));
    }

    private static Specification<Solicitud> especificacionSolicitudes(FiltroReporte f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("fechaSolicitud"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                p.add(cb.lessThan(root.get("fechaSolicitud"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            if (f.getProductorId() != null) {
                p.add(cb.equal(root.get("productor").get("id"), f.getProductorId()));
            }
            if (f.getMaterialId() != null) {
                p.add(cb.equal(root.get("material").get("id"), f.getMaterialId()));
            }
            if (f.getEstado() != null) {
                p.add(cb.equal(root.get("estado"), f.getEstado()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static Specification<Prestamo> especificacionPrestamos(FiltroReporte f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            var solicitud = root.get("solicitud");
            if (f.getDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("fechaEntrega"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                p.add(cb.lessThan(root.get("fechaEntrega"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            if (f.getProductorId() != null) {
                p.add(cb.equal(solicitud.get("productor").get("id"), f.getProductorId()));
            }
            if (f.getMaterialId() != null) {
                p.add(cb.equal(solicitud.get("material").get("id"), f.getMaterialId()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static Specification<Devolucion> especificacionDevoluciones(FiltroReporte f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            var solicitud = root.get("prestamo").get("solicitud");
            if (f.getDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("fecha"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                p.add(cb.lessThan(root.get("fecha"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            if (f.getProductorId() != null) {
                p.add(cb.equal(solicitud.get("productor").get("id"), f.getProductorId()));
            }
            if (f.getMaterialId() != null) {
                p.add(cb.equal(solicitud.get("material").get("id"), f.getMaterialId()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
