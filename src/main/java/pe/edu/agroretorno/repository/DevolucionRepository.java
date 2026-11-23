package pe.edu.agroretorno.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.Devolucion;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long>, JpaSpecificationExecutor<Devolucion> {

    @Query("""
            select distinct d from Devolucion d left join fetch d.detalles join fetch d.recibidoPor
            where d.prestamo.id = :prestamoId order by d.fecha asc
            """)
    List<Devolucion> findByPrestamoId(@Param("prestamoId") Long prestamoId);

    @Query("""
            select distinct d from Devolucion d left join fetch d.detalles join fetch d.recibidoPor
            join fetch d.prestamo pr join fetch pr.solicitud s join fetch s.material
            where s.productor.id = :productorId order by d.fecha desc
            """)
    List<Devolucion> findByProductorId(@Param("productorId") Long productorId);
}
