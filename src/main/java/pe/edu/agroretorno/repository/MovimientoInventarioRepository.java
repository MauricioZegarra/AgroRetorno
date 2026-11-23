package pe.edu.agroretorno.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.MovimientoInventario;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    @Query("""
            select mv from MovimientoInventario mv join fetch mv.usuario
            where mv.material.id = :materialId order by mv.fecha desc, mv.id desc
            """)
    List<MovimientoInventario> findByMaterialId(@Param("materialId") Long materialId);
}
