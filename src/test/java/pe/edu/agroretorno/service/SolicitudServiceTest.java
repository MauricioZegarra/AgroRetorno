package pe.edu.agroretorno.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pe.edu.agroretorno.dto.SolicitudRequest;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Solicitud;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.InventarioRepository;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.MovimientoInventarioRepository;
import pe.edu.agroretorno.repository.ProductorRepository;
import pe.edu.agroretorno.repository.SolicitudRepository;

/**
 * Componente 2: validación, registro, aprobación y rechazo de solicitudes (RF06, RF07).
 * Se usa un InventarioService real (con repositorios simulados) para ejercitar la regla de disponibilidad.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Componente 2 - SolicitudService")
class SolicitudServiceTest {

    @Mock SolicitudRepository solicitudRepository;
    @Mock ProductorRepository productorRepository;
    @Mock MaterialRepository materialRepository;
    @Mock InventarioRepository inventarioRepository;
    @Mock MovimientoInventarioRepository movimientoRepository;
    @Mock UsuarioService usuarioService;
    @Mock HistorialService historialService;

    SolicitudService servicio;
    Productor pedro;
    Material jabas;

    @BeforeEach
    void preparar() {
        InventarioService inventario = new InventarioService(inventarioRepository, materialRepository,
                movimientoRepository, usuarioService, historialService, Fixtures.RELOJ);
        servicio = new SolicitudService(solicitudRepository, productorRepository, materialRepository, inventario,
                usuarioService, historialService, Fixtures.reglas(), Fixtures.RELOJ);
        pedro = Fixtures.productor(1L, "pquispe");
        jabas = Fixtures.material(1L, 50, 0);
        when(productorRepository.findByUsername("pquispe")).thenReturn(Optional.of(pedro));
        when(materialRepository.findByIdConInventario(1L)).thenReturn(Optional.of(jabas));
        when(usuarioService.obtenerPorUsername("encargado")).thenReturn(Fixtures.encargado());
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(i -> {
            Solicitud s = i.getArgument(0);
            s.setId(500L);
            return s;
        });
    }

    private static SolicitudRequest valida() {
        return new SolicitudRequest(1L, 20, Fixtures.HOY.plusDays(15), "Cosecha de cebolla roja");
    }

    @Nested
    @DisplayName("Registro de solicitudes (RF06)")
    class Registro {

        @Test
        @DisplayName("PU-16 [RF06] Registro correcto: queda PENDIENTE y se registra en el historial")
        void registroCorrecto() {
            SolicitudResponse r = servicio.registrar("pquispe", valida());

            assertThat(r.id()).isEqualTo(500L);
            assertThat(r.estado()).isEqualTo(EstadoSolicitud.PENDIENTE);
            assertThat(r.cantidad()).isEqualTo(20);
            assertThat(r.productorId()).isEqualTo(1L);
            verify(historialService).registrar(eq("pquispe"), eq("REGISTRAR_SOLICITUD"), eq("Solicitud"), eq(500L), anyString());
            // Registrar una solicitud no modifica el inventario
            assertThat(jabas.getInventario().getCantidadDisponible()).isEqualTo(50);
        }

        static Stream<Arguments> datosInvalidos() {
            LocalDate hoy = Fixtures.HOY;
            return Stream.of(
                    Arguments.of("cantidad nula", new SolicitudRequest(1L, null, hoy.plusDays(5), "Cosecha de ajo")),
                    Arguments.of("cantidad cero", new SolicitudRequest(1L, 0, hoy.plusDays(5), "Cosecha de ajo")),
                    Arguments.of("cantidad negativa", new SolicitudRequest(1L, -3, hoy.plusDays(5), "Cosecha de ajo")),
                    Arguments.of("sin material", new SolicitudRequest(null, 1, hoy.plusDays(5), "Cosecha de ajo")),
                    Arguments.of("fecha nula", new SolicitudRequest(1L, 1, null, "Cosecha de ajo")),
                    Arguments.of("fecha de hoy (límite inferior - 1)", new SolicitudRequest(1L, 1, hoy, "Cosecha de ajo")),
                    Arguments.of("fecha pasada", new SolicitudRequest(1L, 1, hoy.minusDays(3), "Cosecha de ajo")),
                    Arguments.of("fecha > 90 días (límite superior + 1)", new SolicitudRequest(1L, 1, hoy.plusDays(91), "Cosecha de ajo")),
                    Arguments.of("motivo corto", new SolicitudRequest(1L, 1, hoy.plusDays(5), "abc")),
                    Arguments.of("motivo vacío", new SolicitudRequest(1L, 1, hoy.plusDays(5), "   ")));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("datosInvalidos")
        @DisplayName("PU-17 [RF06/RNF05] Datos inválidos -> ValidacionException y no se guarda nada")
        void datosInvalidos(String caso, SolicitudRequest req) {
            assertThatThrownBy(() -> servicio.registrar("pquispe", req)).isInstanceOf(ValidacionException.class);
            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("PU-18 [RF06] Valores límite de fecha: hoy+1 y hoy+90 son aceptados")
        void limitesDeFecha() {
            servicio.registrar("pquispe", new SolicitudRequest(1L, 1, Fixtures.HOY.plusDays(1), "Cosecha de ajo"));
            servicio.registrar("pquispe", new SolicitudRequest(1L, 1, Fixtures.HOY.plusDays(90), "Cosecha de ajo"));
            verify(solicitudRepository, org.mockito.Mockito.times(2)).save(any());
        }

        @Test
        @DisplayName("PU-19 [RF06] Cantidad superior a la disponibilidad -> ReglaNegocioException")
        void cantidadSuperiorADisponible() {
            SolicitudRequest req = new SolicitudRequest(1L, 51, Fixtures.HOY.plusDays(5), "Cosecha grande");

            assertThatThrownBy(() -> servicio.registrar("pquispe", req))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("solo hay 50");
            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("PU-20 [RF06] Material inactivo o inexistente no puede solicitarse")
        void materialInactivoOInexistente() {
            when(materialRepository.findByIdConInventario(2L)).thenReturn(Optional.of(Fixtures.materialInactivo(2L, 10)));
            when(materialRepository.findByIdConInventario(9L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicio.registrar("pquispe",
                    new SolicitudRequest(2L, 1, Fixtures.HOY.plusDays(5), "Uso en campo")))
                    .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("inactivo");
            assertThatThrownBy(() -> servicio.registrar("pquispe",
                    new SolicitudRequest(9L, 1, Fixtures.HOY.plusDays(5), "Uso en campo")))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        @DisplayName("PU-21 [RNF02] Un usuario que no es productor no puede registrar; un productor no ve solicitudes ajenas")
        void restriccionesDePropiedad() {
            when(productorRepository.findByUsername("encargado")).thenReturn(Optional.empty());
            Solicitud ajena = Fixtures.solicitud(7L, Fixtures.productor(2L, "lmamani"), jabas, 2, EstadoSolicitud.PENDIENTE);
            when(solicitudRepository.findById(7L)).thenReturn(Optional.of(ajena));

            assertThatThrownBy(() -> servicio.registrar("encargado", valida()))
                    .isInstanceOf(OperacionNoPermitidaException.class);
            assertThatThrownBy(() -> servicio.obtenerParaProductor("pquispe", 7L))
                    .isInstanceOf(OperacionNoPermitidaException.class);
            assertThat(servicio.obtenerParaProductor("lmamani", 7L).id()).isEqualTo(7L);
        }
    }

    @Nested
    @DisplayName("Aprobación y rechazo (RF07)")
    class AprobacionRechazo {

        Solicitud pendiente;

        @BeforeEach
        void solicitudPendiente() {
            pendiente = Fixtures.solicitud(10L, pedro, jabas, 20, EstadoSolicitud.PENDIENTE);
            when(solicitudRepository.findByIdParaActualizar(10L)).thenReturn(Optional.of(pendiente));
        }

        @Test
        @DisplayName("PU-22 [RF07] Aprobación correcta: APROBADA, con responsable y SIN descontar inventario")
        void aprobacionCorrecta() {
            SolicitudResponse r = servicio.aprobar(10L, "encargado");

            assertThat(r.estado()).isEqualTo(EstadoSolicitud.APROBADA);
            assertThat(pendiente.getRespondidoPor().getUsername()).isEqualTo("encargado");
            assertThat(pendiente.getFechaRespuesta()).isEqualTo(Fixtures.AHORA);
            assertThat(jabas.getInventario().getCantidadDisponible()).isEqualTo(50);
            verify(inventarioRepository, never()).findByMaterialIdParaActualizar(anyLong());
        }

        @Test
        @DisplayName("PU-23 [RF07] Aprobación bloqueada si ya no hay disponibilidad suficiente")
        void aprobacionSinDisponibilidad() {
            jabas.getInventario().setCantidadDisponible(10);
            jabas.getInventario().setCantidadPrestada(40);

            assertThatThrownBy(() -> servicio.aprobar(10L, "encargado")).isInstanceOf(ReglaNegocioException.class);
            assertThat(pendiente.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        }

        @Test
        @DisplayName("PU-24 [RF07] Rechazo con motivo: RECHAZADA y motivo registrado")
        void rechazoCorrecto() {
            SolicitudResponse r = servicio.rechazar(10L, "  Excede lo asignado  ", "encargado");

            assertThat(r.estado()).isEqualTo(EstadoSolicitud.RECHAZADA);
            assertThat(r.motivoRechazo()).isEqualTo("Excede lo asignado");
            verify(historialService).registrar(eq("encargado"), eq("RECHAZAR_SOLICITUD"), any(), eq(10L), any());
        }

        @Test
        @DisplayName("PU-25 [RF07] No se puede aprobar una solicitud ya rechazada")
        void noAprobarRechazada() {
            pendiente.setEstado(EstadoSolicitud.RECHAZADA);

            assertThatThrownBy(() -> servicio.aprobar(10L, "encargado"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("RECHAZADA");
            assertThat(pendiente.getEstado()).isEqualTo(EstadoSolicitud.RECHAZADA);
        }

        @Test
        @DisplayName("PU-26 [RF07] Rechazo sin motivo o de una solicitud entregada es rechazado")
        void rechazoInvalido() {
            assertThatThrownBy(() -> servicio.rechazar(10L, "", "encargado")).isInstanceOf(ValidacionException.class);
            pendiente.setEstado(EstadoSolicitud.ENTREGADA);
            assertThatThrownBy(() -> servicio.rechazar(10L, "Motivo válido", "encargado"))
                    .isInstanceOf(ReglaNegocioException.class);
        }

        @Test
        @DisplayName("PU-27 [RF07] Consultas: por estado, todas y por productor")
        void consultas() {
            when(solicitudRepository.findByEstado(EstadoSolicitud.PENDIENTE)).thenReturn(List.of(pendiente));
            when(solicitudRepository.findAllOrdenadas()).thenReturn(List.of(pendiente));
            when(solicitudRepository.findByProductorId(1L)).thenReturn(List.of(pendiente));
            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(pendiente));

            assertThat(servicio.listar(EstadoSolicitud.PENDIENTE)).hasSize(1);
            assertThat(servicio.listar(null)).hasSize(1);
            assertThat(servicio.listarPorProductor("pquispe")).extracting(SolicitudResponse::id).containsExactly(10L);
            assertThat(servicio.obtener(10L).materialNombre()).isEqualTo("Jaba plástica");
            assertThatThrownBy(() -> servicio.obtener(77L)).isInstanceOf(RecursoNoEncontradoException.class);
        }
    }
}
