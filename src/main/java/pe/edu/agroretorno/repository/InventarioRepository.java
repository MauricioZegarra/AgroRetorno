package pe.edu.agroretorno.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByMaterialId(Long materialId);

    /**
     * Bloqueo de fila (SELECT ... FOR UPDATE) para que dos operaciones simultáneas
     * no dejen existencias negativas.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i where i.material.id = :materialId")
    Optional<Inventario> findByMaterialIdParaActualizar(@Param("materialId") Long materialId);
}
