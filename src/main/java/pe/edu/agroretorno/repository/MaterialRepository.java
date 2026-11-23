package pe.edu.agroretorno.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByCodigo(String codigo);

    /** Consulta de inventario con sus existencias en una sola sentencia (evita N+1). */
    @Query("""
            select m from Material m join fetch m.inventario i
            where (:texto = '' or lower(m.nombre) like concat('%', :texto, '%')
                   or lower(m.codigo) like concat('%', :texto, '%'))
            order by m.categoria, m.nombre
            """)
    List<Material> buscarConInventario(@Param("texto") String texto);

    @Query("select m from Material m join fetch m.inventario where m.id = :id")
    Optional<Material> findByIdConInventario(@Param("id") Long id);
}
