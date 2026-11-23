package pe.edu.agroretorno.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pe.edu.agroretorno.dto.AjusteExistenciasRequest;
import pe.edu.agroretorno.dto.DisponibilidadResponse;
import pe.edu.agroretorno.entity.Inventario;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.MovimientoInventario;
import pe.edu.agroretorno.entity.TipoMovimiento;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.InventarioRepository;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.MovimientoInventarioRepository;

/**
 * Componente 1: disponibilidad y control de inventario (RF04, RF05, RF08, RF09).
 * Unidad bajo prueba: InventarioService. Dobles: repositorios, UsuarioService e HistorialService (Mockito).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Componente 1 - InventarioService")
class InventarioServiceTest {

    @Mock InventarioRepository inventarioRepository;
    @Mock MaterialRepository materialRepository;
    @Mock MovimientoInventarioRepository movimientoRepository;
    @Mock UsuarioService usuarioService;
    @Mock HistorialService historialService;

    InventarioService servicio;
    Material jabas;

    @BeforeEach
    void preparar() {
        servicio = new InventarioService(inventarioRepository, materialRepository, movimientoRepository,
                usuarioService, historialService, Fixtures.RELOJ);
        jabas = Fixtures.material(1L, 50, 0);
        when(materialRepository.findByIdConInventario(1L)).thenReturn(Optional.of(jabas));
        when(inventarioRepository.findByMaterialIdParaActualizar(1L)).thenReturn(Optional.of(jabas.getInventario()));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));
        when(usuarioService.obtenerPorUsername("encargado")).thenReturn(Fixtures.encargado());
    }

    @Nested
    @DisplayName("Consulta de disponibilidad (RF05)")
    class Consulta {

        @Test
        @DisplayName("PU-01 [RF05] Consulta correcta: 10 de 50 disponibles -> disponible=true")
        void consultaDisponibilidadCorrecta() {
            DisponibilidadResponse r = servicio.consultarDisponibilidad(1L, 10);

            assertThat(r.disponible()).isTrue();
            assertThat(r.cantidadDisponible()).isEqualTo(50);
            assertThat(r.cantidadSolicitada()).isEqualTo(10);
        }

        @Test
        @DisplayName("PU-02 [RF05] Material inexistente -> RecursoNoEncontradoException")
        void materialInexistente() {
            when(materialRepository.findByIdConInventario(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicio.consultarDisponibilidad(99L, 1))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("PU-03 [RF05] Material inactivo -> no disponible aunque tenga stock")
        void materialInactivo() {
            when(materialRepository.findByIdConInventario(2L)).thenReturn(Optional.of(Fixtures.materialInactivo(2L, 30)));

            DisponibilidadResponse r = servicio.consultarDisponibilidad(2L, 1);

            assertThat(r.disponible()).isFalse();
            assertThat(r.mensaje()).contains("inactivo");
        }

        @ParameterizedTest(name = "cantidad = {0}")
        @ValueSource(ints = {0, -1, -20})
        @DisplayName("PU-04 [RNF05] Cantidad no positiva -> ValidacionException")
        void cantidadNoPositiva(int cantidad) {
            assertThatThrownBy(() -> servicio.consultarDisponibilidad(1L, cantidad))
                    .isInstanceOf(ValidacionException.class)
                    .hasMessageContaining("entero positivo");
        }

        @Test
        @DisplayName("PU-05 [RF06] Valor límite: solicitar exactamente lo disponible es válido; una unidad más no")
        void valorLimiteDisponibilidad() {
            servicio.validarDisponibilidad(jabas, 50); // no lanza excepción

            assertThatThrownBy(() -> servicio.validarDisponibilidad(jabas, 51))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("Disponibilidad insuficiente");
        }

        @Test
        @DisplayName("PU-06 [RF05] Listado para productor excluye materiales inactivos")
        void listadoSoloActivos() {
            when(materialRepository.buscarConInventario("")).thenReturn(List.of(jabas, Fixtures.materialInactivo(2L, 5)));

            assertThat(servicio.listarInventario(null, true)).hasSize(1);
            assertThat(servicio.listarInventario(null, false)).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Entregas (RF08)")
    class Entregas {

        @Test
        @DisplayName("PU-07 [RF08] Entrega con existencias suficientes: disponible 50->30, prestada 0->20")
        void entregaConExistenciasSuficientes() {
            Inventario inv = servicio.descontarPorEntrega(1L, 20, Fixtures.encargado(), "SOLICITUD-1");

            assertThat(inv.getCantidadDisponible()).isEqualTo(30);
            assertThat(inv.getCantidadPrestada()).isEqualTo(20);
            assertThat(inv.getCantidadTotal()).isEqualTo(50);
            assertThat(inv.estaCuadrado()).isTrue();
            ArgumentCaptor<MovimientoInventario> mov = ArgumentCaptor.forClass(MovimientoInventario.class);
            verify(movimientoRepository).save(mov.capture());
            assertThat(mov.getValue().getTipo()).isEqualTo(TipoMovimiento.ENTREGA);
            assertThat(mov.getValue().getCantidad()).isEqualTo(20);
        }

        @Test
        @DisplayName("PU-08 [RF08] Entrega con existencias insuficientes: se bloquea y el inventario no cambia")
        void entregaConExistenciasInsuficientes() {
            assertThatThrownBy(() -> servicio.descontarPorEntrega(1L, 51, Fixtures.encargado(), "SOLICITUD-1"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("insuficientes");

            assertThat(jabas.getInventario().getCantidadDisponible()).isEqualTo(50);
            verify(inventarioRepository, never()).save(any());
            verify(movimientoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Retorno de unidades (RF09)")
    class Retornos {

        @Test
        @DisplayName("PU-09 [RF09] Retorno clasificado: buen estado vuelve a disponible; dañadas y mantenimiento no")
        void retornoClasificado() {
            Inventario inv = jabas.getInventario();
            inv.setCantidadDisponible(30);
            inv.setCantidadPrestada(20);

            servicio.registrarRetorno(1L, 5, 2, 3, Fixtures.encargado(), "PRESTAMO-1");

            assertThat(inv.getCantidadDisponible()).isEqualTo(35);
            assertThat(inv.getCantidadPrestada()).isEqualTo(10);
            assertThat(inv.getCantidadDanada()).isEqualTo(2);
            assertThat(inv.getCantidadMantenimiento()).isEqualTo(3);
            assertThat(inv.getCantidadTotal()).isEqualTo(50);
            assertThat(inv.estaCuadrado()).isTrue();
            verify(movimientoRepository, times(3)).save(any());
        }

        @Test
        @DisplayName("PU-10 [RF09] Retorno mayor que lo prestado del material -> ReglaNegocioException")
        void retornoMayorQuePrestado() {
            assertThatThrownBy(() -> servicio.registrarRetorno(1L, 1, 0, 0, Fixtures.encargado(), "PRESTAMO-1"))
                    .isInstanceOf(ReglaNegocioException.class);
            verify(inventarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Ajustes de existencias (RF04)")
    class Ajustes {

        @Test
        @DisplayName("PU-11 [RF04] Ingreso de 10 unidades incrementa total y disponible")
        void ingreso() {
            servicio.ajustarExistencias(1L, new AjusteExistenciasRequest(TipoMovimiento.INGRESO, 10, "Compra"), "encargado");

            assertThat(jabas.getInventario().getCantidadTotal()).isEqualTo(60);
            assertThat(jabas.getInventario().getCantidadDisponible()).isEqualTo(60);
            verify(historialService).registrar(any(), any(), any(), anyLong(), any());
        }

        @Test
        @DisplayName("PU-12 [RF04] Mantenimiento y reparación mueven unidades sin alterar el total")
        void mantenimientoYReparacion() {
            servicio.ajustarExistencias(1L, new AjusteExistenciasRequest(TipoMovimiento.ENVIO_MANTENIMIENTO, 4, "Revisión"), "encargado");
            servicio.ajustarExistencias(1L, new AjusteExistenciasRequest(TipoMovimiento.REPARACION, 3, "Reparadas"), "encargado");

            Inventario inv = jabas.getInventario();
            assertThat(inv.getCantidadDisponible()).isEqualTo(49);
            assertThat(inv.getCantidadMantenimiento()).isEqualTo(1);
            assertThat(inv.getCantidadTotal()).isEqualTo(50);
        }

        @Test
        @DisplayName("PU-13 [RF04] Retiro mayor que lo disponible y baja sin dañadas son rechazados")
        void ajustesInvalidos() {
            assertThatThrownBy(() -> servicio.ajustarExistencias(1L,
                    new AjusteExistenciasRequest(TipoMovimiento.RETIRO, 51, "Retiro"), "encargado"))
                    .isInstanceOf(ReglaNegocioException.class);
            assertThatThrownBy(() -> servicio.ajustarExistencias(1L,
                    new AjusteExistenciasRequest(TipoMovimiento.BAJA_DANADO, 1, "Baja"), "encargado"))
                    .isInstanceOf(ReglaNegocioException.class);
            assertThatThrownBy(() -> servicio.ajustarExistencias(1L,
                    new AjusteExistenciasRequest(TipoMovimiento.ENTREGA, 1, "No permitido"), "encargado"))
                    .isInstanceOf(ValidacionException.class);
            assertThat(jabas.getInventario().getCantidadTotal()).isEqualTo(50);
        }

        @Test
        @DisplayName("PU-14 [RF04] Retiro y baja de dañadas reducen el total manteniendo el cuadre")
        void retiroYBaja() {
            jabas.getInventario().setCantidadDisponible(45);
            jabas.getInventario().setCantidadDanada(5);

            servicio.ajustarExistencias(1L, new AjusteExistenciasRequest(TipoMovimiento.RETIRO, 5, "Traslado"), "encargado");
            servicio.ajustarExistencias(1L, new AjusteExistenciasRequest(TipoMovimiento.BAJA_DANADO, 5, "Rotas"), "encargado");

            Inventario inv = jabas.getInventario();
            assertThat(inv.getCantidadTotal()).isEqualTo(40);
            assertThat(inv.getCantidadDisponible()).isEqualTo(40);
            assertThat(inv.getCantidadDanada()).isZero();
            assertThat(inv.estaCuadrado()).isTrue();
        }

        @Test
        @DisplayName("PU-15 [RF04] Alta inicial crea inventario y movimiento; cantidad negativa es rechazada")
        void altaInicial() {
            Material nuevo = Fixtures.material(3L, 0, 0);
            nuevo.setInventario(null);

            Inventario inv = servicio.crearInventarioInicial(nuevo, 12, Fixtures.encargado());

            assertThat(inv.getCantidadTotal()).isEqualTo(12);
            assertThat(inv.getCantidadDisponible()).isEqualTo(12);
            verify(movimientoRepository).save(any());
            assertThatThrownBy(() -> servicio.crearInventarioInicial(nuevo, -1, Fixtures.encargado()))
                    .isInstanceOf(ValidacionException.class);
        }
    }
}
