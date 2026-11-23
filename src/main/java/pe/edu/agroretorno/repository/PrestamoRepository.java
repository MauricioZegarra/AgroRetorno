package pe.edu.agroretorno.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long>, JpaSpecificationExecutor<Prestamo> {

    boolean existsBySolicitudId(Long solicitudId);

    Optional<Prestamo> findBySolicitudId(Long solicitudId);

    @Query("""
            select pr from Prestamo pr join fetch pr.solicitud s join fetch s.material
            join fetch s.productor p join fetch p.usuario
            where pr.estado = :estado order by pr.fechaDevolucionPrevista asc
            """)
    List<Prestamo> findByEstado(@Param("estado") EstadoPrestamo estado);

    @Query("""
            select pr from Prestamo pr join fetch pr.solicitud s join fetch s.material
            join fetch s.productor p join fetch p.usuario
            where p.id = :productorId order by pr.fechaEntrega desc
            """)
    List<Prestamo> findByProductorId(@Param("productorId") Long productorId);

    long countByEstado(EstadoPrestamo estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pr from Prestamo pr where pr.id = :id")
    Optional<Prestamo> findByIdParaActualizar(@Param("id") Long id);
}
