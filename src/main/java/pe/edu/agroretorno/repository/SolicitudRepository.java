package pe.edu.agroretorno.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long>, JpaSpecificationExecutor<Solicitud> {

    @Query("""
            select s from Solicitud s join fetch s.material join fetch s.productor p join fetch p.usuario
            where p.id = :productorId order by s.fechaSolicitud desc
            """)
    List<Solicitud> findByProductorId(@Param("productorId") Long productorId);

    @Query("""
            select s from Solicitud s join fetch s.material join fetch s.productor p join fetch p.usuario
            where s.estado = :estado order by s.fechaSolicitud asc
            """)
    List<Solicitud> findByEstado(@Param("estado") EstadoSolicitud estado);

    @Query("""
            select s from Solicitud s join fetch s.material join fetch s.productor p join fetch p.usuario
            order by s.fechaSolicitud desc
            """)
    List<Solicitud> findAllOrdenadas();

    long countByEstado(EstadoSolicitud estado);

    /** Bloquea la solicitud para impedir dos entregas simultáneas de la misma solicitud. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Solicitud s where s.id = :id")
    Optional<Solicitud> findByIdParaActualizar(@Param("id") Long id);
}
