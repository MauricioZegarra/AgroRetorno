package pe.edu.agroretorno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AgroRetorno: gestión de préstamos y devoluciones de materiales agrícolas reutilizables
 * para cooperativas de la región Arequipa (arquitectura MVC monolítica).
 */
@SpringBootApplication
public class AgroRetornoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroRetornoApplication.class, args);
    }
}
