package pe.edu.agroretorno.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Clock;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.agroretorno.repository.UsuarioRepository;

/**
 * RNF02 y control de riesgo 1 (autorización por roles) y 2 (validación de entradas),
 * verificados a través de la capa HTTP real (web y API).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integración - seguridad, autorización y validación HTTP")
class SeguridadIntegrationTest {

    static final String CLAVE_PRODUCTOR = "Productor2026!";
    static final String CLAVE_ENCARGADO = "Encargado2026!";

    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired Clock clock;

    @Test
    @DisplayName("SEG-01 [RNF02] Las contraseñas se almacenan con hash BCrypt, nunca en texto plano")
    void contrasenasConHash() {
        String hash = usuarioRepository.findByUsername("pquispe").orElseThrow().getPasswordHash();
        assertThat(hash).startsWith("$2a$").isNotEqualTo(CLAVE_PRODUCTOR);
    }

    @Test
    @DisplayName("SEG-02 [RF01] Inicio de sesión web correcto, credenciales inválidas y cierre de sesión")
    void inicioYCierreDeSesion() throws Exception {
        mvc.perform(formLogin("/login").user("pquispe").password(CLAVE_PRODUCTOR))
                .andExpect(authenticated().withUsername("pquispe"))
                .andExpect(redirectedUrl("/panel"));
        mvc.perform(formLogin("/login").user("pquispe").password("incorrecta"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
        mvc.perform(logout("/logout")).andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @DisplayName("SEG-03 [RNF02] Formularios web sin token CSRF son rechazados (403)")
    void csrfObligatorio() throws Exception {
        mvc.perform(post("/login").param("username", "pquispe").param("password", CLAVE_PRODUCTOR))
                .andExpect(status().isForbidden());
        mvc.perform(post("/encargado/solicitudes/1/aprobar").with(user("encargado").roles("ENCARGADO")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("SEG-04 [RF02] Restricción web por rol: productor no accede a funciones del encargado y viceversa")
    void restriccionWebPorRol() throws Exception {
        mvc.perform(get("/encargado/materiales").with(user("pquispe").roles("PRODUCTOR")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/encargado/existencias/1/ajustes").with(user("pquispe").roles("PRODUCTOR")).with(csrf())
                        .param("tipo", "INGRESO").param("cantidad", "100").param("observacion", "Intento"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/productor/inventario").with(user("encargado").roles("ENCARGADO")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/productor/inventario")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/productor/inventario").with(user("pquispe").roles("PRODUCTOR")))
                .andExpect(status().isOk()).andExpect(view().name("productor/inventario"));
    }

    @Test
    @DisplayName("SEG-05 [RF02] API: 401 sin credenciales o con clave errónea, 403 con rol incorrecto")
    void restriccionApi() throws Exception {
        mvc.perform(get("/api/materiales")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/materiales").with(httpBasic("pquispe", "mala"))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/encargado/solicitudes").with(httpBasic("pquispe", CLAVE_PRODUCTOR)))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/encargado/solicitudes/1/aprobar").with(httpBasic("pquispe", CLAVE_PRODUCTOR)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/productor/solicitudes").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/auth/me").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.roles[0]").value("ROLE_ENCARGADO"));
    }

    @Test
    @DisplayName("SEG-06 [RNF02] Un productor no puede consultar solicitudes de otro productor")
    void proteccionDatosDeProductor() throws Exception {
        // La solicitud 1 de los datos de demostración pertenece a pquispe
        mvc.perform(get("/api/productor/solicitudes/1").with(httpBasic("pquispe", CLAVE_PRODUCTOR)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/productor/solicitudes/1").with(httpBasic("lmamani", CLAVE_PRODUCTOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("SEG-07 [RF05/RF06] API: consulta de inventario y registro válido de solicitud (201)")
    void transaccionesCriticas() throws Exception {
        mvc.perform(get("/api/materiales").with(httpBasic("pquispe", CLAVE_PRODUCTOR)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].cantidadDisponible").exists());
        mvc.perform(get("/api/materiales/1/disponibilidad").param("cantidad", "1")
                        .with(httpBasic("pquispe", CLAVE_PRODUCTOR)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.disponible").value(true));
        String cuerpo = """
                {"materialId":1,"cantidad":1,"fechaDevolucionPrevista":"%s","motivo":"Prueba de API"}
                """.formatted(LocalDate.now(clock).plusDays(7));
        mvc.perform(post("/api/productor/solicitudes").with(httpBasic("pquispe", CLAVE_PRODUCTOR))
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("SEG-08 [RNF05] API: entradas inválidas -> 400; regla de negocio violada -> 409; recurso inexistente -> 404")
    void validacionDeEntradas() throws Exception {
        String fecha = LocalDate.now(clock).plusDays(7).toString();
        mvc.perform(post("/api/productor/solicitudes").with(httpBasic("pquispe", CLAVE_PRODUCTOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materialId\":1,\"cantidad\":-5,\"fechaDevolucionPrevista\":\"" + fecha + "\",\"motivo\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.cantidad").exists());
        mvc.perform(post("/api/productor/solicitudes").with(httpBasic("pquispe", CLAVE_PRODUCTOR))
                        .contentType(MediaType.APPLICATION_JSON).content("{no es json"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/productor/solicitudes").with(httpBasic("pquispe", CLAVE_PRODUCTOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materialId\":1,\"cantidad\":99999,\"fechaDevolucionPrevista\":\"" + fecha + "\",\"motivo\":\"Exceso de stock\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/productor/solicitudes").with(httpBasic("pquispe", CLAVE_PRODUCTOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materialId\":1,\"cantidad\":9000,\"fechaDevolucionPrevista\":\"" + fecha + "\",\"motivo\":\"Exceso de stock\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Disponibilidad insuficiente")));
        mvc.perform(get("/api/materiales/99999/disponibilidad").with(httpBasic("pquispe", CLAVE_PRODUCTOR)))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/encargado/prestamos/99999/devoluciones").with(httpBasic("encargado", CLAVE_ENCARGADO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadBuenEstado\":1,\"cantidadDanada\":0,\"cantidadMantenimiento\":0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("SEG-09 [RF07-RF10] API del encargado: aprobar, entregar, devolver, historial y reporte")
    void flujoApiEncargado() throws Exception {
        String fecha = LocalDate.now(clock).plusDays(7).toString();
        String respuesta = mvc.perform(post("/api/productor/solicitudes").with(httpBasic("ccutipa", CLAVE_PRODUCTOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materialId\":2,\"cantidad\":2,\"fechaDevolucionPrevista\":\"" + fecha + "\",\"motivo\":\"Cosecha de palta\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(respuesta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mvc.perform(post("/api/encargado/solicitudes/" + id + "/entrega").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/encargado/solicitudes/" + id + "/aprobar").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("APROBADA"));
        String prestamo = mvc.perform(post("/api/encargado/solicitudes/" + id + "/entrega")
                        .with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long prestamoId = Long.parseLong(prestamo.replaceAll("^\\{\"id\":(\\d+).*", "$1"));
        mvc.perform(post("/api/encargado/prestamos/" + prestamoId + "/devoluciones")
                        .with(httpBasic("encargado", CLAVE_ENCARGADO)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadBuenEstado\":2,\"cantidadDanada\":0,\"cantidadMantenimiento\":0}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.estadoPrestamo").value("CERRADO"));
        mvc.perform(post("/api/encargado/solicitudes/" + id + "/rechazar").with(httpBasic("encargado", CLAVE_ENCARGADO))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Ya fue entregada\"}"))
                .andExpect(status().isConflict());
        mvc.perform(get("/api/encargado/prestamos/" + prestamoId + "/devoluciones").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/encargado/historial").param("entidad", "Prestamo").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/encargado/reportes/consolidado").with(httpBasic("encargado", CLAVE_ENCARGADO)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.inventario.materialesRegistrados").exists());
        mvc.perform(get("/api/productor/prestamos").with(httpBasic("ccutipa", CLAVE_PRODUCTOR)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/productor/devoluciones").with(httpBasic("ccutipa", CLAVE_PRODUCTOR)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("SEG-10 [RNF05] Web: formulario de solicitud con datos inválidos vuelve a mostrarse con errores")
    void validacionWeb() throws Exception {
        mvc.perform(post("/productor/solicitudes").with(user("pquispe").roles("PRODUCTOR")).with(csrf())
                        .param("materialId", "1").param("cantidad", "0")
                        .param("fechaDevolucionPrevista", LocalDate.now(clock).plusDays(3).toString())
                        .param("motivo", "Motivo válido"))
                .andExpect(status().isOk())
                .andExpect(view().name("productor/solicitud-form"));
        mvc.perform(post("/productor/solicitudes").with(user("pquispe").roles("PRODUCTOR")).with(csrf())
                        .param("materialId", "1").param("cantidad", "1")
                        .param("fechaDevolucionPrevista", LocalDate.now(clock).plusDays(3).toString())
                        .param("motivo", "Motivo válido"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productor/solicitudes"));
    }
}
