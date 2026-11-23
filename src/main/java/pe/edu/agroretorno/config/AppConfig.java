package pe.edu.agroretorno.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ReglasNegocioProperties.class)
public class AppConfig {

    /** Reloj inyectable: permite pruebas reproducibles con fechas fijas. */
    @Bean
    public Clock clock(@Value("${agroretorno.zona-horaria:America/Lima}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }
}
