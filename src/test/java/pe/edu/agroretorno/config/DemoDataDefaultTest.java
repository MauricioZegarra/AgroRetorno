package pe.edu.agroretorno.config;

import java.time.Clock;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.agroretorno.repository.UsuarioRepository;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.MaterialService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.ProductorService;
import pe.edu.agroretorno.service.SolicitudService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DemoDataDefaultTest {

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withInitializer(context -> {
                String fuente = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
                context.getEnvironment().getPropertySources().replace(fuente,
                        new SystemEnvironmentPropertySource(fuente, Map.of()));
            })
            .withUserConfiguration(DatosDemoInitializer.class)
            .withBean(UsuarioRepository.class, () -> mock(UsuarioRepository.class))
            .withBean(PasswordEncoder.class, () -> mock(PasswordEncoder.class))
            .withBean(ProductorService.class, () -> mock(ProductorService.class))
            .withBean(MaterialService.class, () -> mock(MaterialService.class))
            .withBean(SolicitudService.class, () -> mock(SolicitudService.class))
            .withBean(PrestamoService.class, () -> mock(PrestamoService.class))
            .withBean(DevolucionService.class, () -> mock(DevolucionService.class))
            .withBean(Clock.class, Clock::systemUTC);

    private final ApplicationContextRunner aplicacion = contexto
            .withInitializer(new ConfigDataApplicationContextInitializer());

    @Test
    void noCargaDatosCuandoFaltaLaVariableDeEntorno() {
        aplicacion.run(context -> assertThat(context).doesNotHaveBean(DatosDemoInitializer.class));
    }

    @Test
    void noCargaDatosCuandoLaConfiguracionEstaDesactivada() {
        aplicacion.withPropertyValues("DEMO_DATA=false")
                .run(context -> assertThat(context).doesNotHaveBean(DatosDemoInitializer.class));
    }

    @Test
    void habilitaElInicializadorCuandoLaDemostracionSeActivaExplicitamente() {
        aplicacion.withPropertyValues("DEMO_DATA=true")
                .run(context -> assertThat(context).hasSingleBean(DatosDemoInitializer.class));
    }

    @Test
    void noRegistraElInicializadorSiFaltaLaPropiedadDeAplicacion() {
        contexto.run(context -> assertThat(context).doesNotHaveBean(DatosDemoInitializer.class));
    }
}
