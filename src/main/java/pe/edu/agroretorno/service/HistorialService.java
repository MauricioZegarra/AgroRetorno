package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.FiltroHistorial;
import pe.edu.agroretorno.dto.HistorialResponse;
import pe.edu.agroretorno.entity.HistorialOperacion;
import pe.edu.agroretorno.repository.HistorialOperacionRepository;

/**
 * Registro y consulta del historial de operaciones (RF10, trazabilidad para G06).
 * Se ejecuta dentro de la misma transacción de la operación: si la operación falla,
 * tampoco queda registrado en el historial.
 */
@Service
public class HistorialService {

    static final int LIMITE_CONSULTA = 500;

    private final HistorialOperacionRepository historialRepository;
    private final Clock clock;

    public HistorialService(HistorialOperacionRepository historialRepository, Clock clock) {
        this.historialRepository = historialRepository;
        this.clock = clock;
    }

    @Transactional
    public void registrar(String actor, String accion, String entidad, Long entidadId, String detalle) {
        String detalleRecortado = detalle != null && detalle.length() > 1000 ? detalle.substring(0, 1000) : detalle;
        historialRepository.save(new HistorialOperacion(LocalDateTime.now(clock), actor, accion, entidad,
                entidadId, detalleRecortado));
    }

    @Transactional(readOnly = true)
    public List<HistorialResponse> consultar(FiltroHistorial filtro) {
        Specification<HistorialOperacion> spec = construirEspecificacion(filtro);
        var pagina = PageRequest.of(0, LIMITE_CONSULTA, Sort.by(Sort.Direction.DESC, "fechaHora", "id"));
        return historialRepository.findAll(spec, pagina).map(HistorialResponse::from).getContent();
    }

    private Specification<HistorialOperacion> construirEspecificacion(FiltroHistorial f) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> p = new ArrayList<>();
            if (f.getDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                p.add(cb.lessThan(root.get("fechaHora"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            if (tieneTexto(f.getActor())) {
                p.add(cb.equal(root.get("actor"), f.getActor().trim()));
            }
            if (tieneTexto(f.getEntidad())) {
                p.add(cb.equal(root.get("entidad"), f.getEntidad().trim()));
            }
            if (tieneTexto(f.getAccion())) {
                p.add(cb.equal(root.get("accion"), f.getAccion().trim()));
            }
            return cb.and(p.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private static boolean tieneTexto(String s) {
        return s != null && !s.isBlank();
    }
}
