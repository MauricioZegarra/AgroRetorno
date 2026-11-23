package pe.edu.agroretorno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pe.edu.agroretorno.entity.HistorialOperacion;

public interface HistorialOperacionRepository
        extends JpaRepository<HistorialOperacion, Long>, JpaSpecificationExecutor<HistorialOperacion> {
}
