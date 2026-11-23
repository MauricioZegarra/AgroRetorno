package pe.edu.agroretorno.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.agroretorno.entity.Productor;

public interface ProductorRepository extends JpaRepository<Productor, Long> {

    @Query("select p from Productor p join fetch p.usuario u where u.username = :username")
    Optional<Productor> findByUsername(@Param("username") String username);

    boolean existsByDni(String dni);

    @Query("""
            select p from Productor p join fetch p.usuario u
            where :texto = ''
               or lower(p.nombres) like concat('%', :texto, '%')
               or lower(p.apellidos) like concat('%', :texto, '%')
               or p.dni like concat('%', :texto, '%')
               or lower(u.username) like concat('%', :texto, '%')
            order by p.apellidos, p.nombres
            """)
    List<Productor> buscar(@Param("texto") String texto);
}
