package pe.edu.agroretorno.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pe.edu.agroretorno.dto.DevolucionRequest;
import pe.edu.agroretorno.dto.DevolucionResponse;
import pe.edu.agroretorno.entity.Devolucion;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Inventario;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.repository.DevolucionRepository;
import pe.edu.agroretorno.repository.InventarioRepository;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.MovimientoInventarioRepository;
import pe.edu.agroretorno.repository.PrestamoRepository;
import pe.edu.agroretorno.repository.ProductorRepository;

/**
 * Componente 3: registro de devoluciones totales y parciales (Proceso 3, RF09).
 * Estado inicial común: 50 jabas en total; préstamo de 20 entregado (30 disponibles, 20 prestadas).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Componente 3 - DevolucionService")
class DevolucionServiceTest {

    @Mock DevolucionRepository devolucionRepository;
    @Mock PrestamoRepository prestamoRepository;
    @Mock ProductorRepository productorRepository;
    @Mock InventarioRepository inventarioRepository;
    @Mock MaterialRepository materialRepository;
    @Mock MovimientoInventarioRepository movimientoRepository;
    @Mock UsuarioService usuarioService;
    @Mock HistorialService historialService;

    DevolucionService servicio;
    Material jabas;
    Inventario inventario;
    Prestamo prestamo;

    @BeforeEach
    void preparar() {
        InventarioService inventarioService = new InventarioService(inventarioRepository, materialRepository,
                movimientoRepository, usuarioService, historialService, Fixtures.RELOJ);
        servicio = new DevolucionService(devolucionRepository, prestamoRepository, productorRepository,
                inventarioService, usuarioService, historialService, Fixtures.RELOJ);
        jabas = Fixtures.material(1L, 30, 20);
        inventario = jabas.getInventario();
        var solicitud = Fixtures.solicitud(10L, Fixtures.productor(1L, "pquispe"), jabas, 20, EstadoSolicitud.APROBADA);
        prestamo = Fixtures.prestamo(900L, solicitud, 20);
        when(usuarioService.obtenerPorUsername("encargado")).thenReturn(Fixtures.encargado());
        when(prestamoRepository.findByIdParaActualizar(900L)).thenReturn(Optional.of(prestamo));
        when(inventarioRepository.findByMaterialIdParaActualizar(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(devolucionRepository.save(any(Devolucion.class))).thenAnswer(i -> i.getArgument(0));
    }

    private DevolucionResponse devolver(int buen, int danadas, int mant, String obs) {
        return servicio.registrar(900L, new DevolucionRequest(buen, danadas, mant, obs), "encargado");
    }

    @Test
    @DisplayName("PU-34 [RF09] Devolución total en buen estado: préstamo CERRADO y 20 unidades vuelven a disponible")
    void devolucionTotalBuenEstado() {
        DevolucionResponse r = devolver(20, 0, 0, null);

        assertThat(r.estadoPrestamo()).isEqualTo(EstadoPrestamo.CERRADO);
        assertThat(r.pendienteDespues()).isZero();
        assertThat(prestamo.getFechaCierre()).isEqualTo(Fixtures.AHORA);
        assertThat(inventario.getCantidadDisponible()).isEqualTo(50);
        assertThat(inventario.getCantidadPrestada()).isZero();
        assertThat(inventario.estaCuadrado()).isTrue();
    }

    @Test
    @DisplayName("PU-35 [RF09] Devolución parcial: 8 de 20, el préstamo sigue ACTIVO con 12 pendientes")
    void devolucionParcial() {
        DevolucionResponse r = devolver(8, 0, 0, null);

        assertThat(r.estadoPrestamo()).isEqualTo(EstadoPrestamo.ACTIVO);
        assertThat(prestamo.getCantidadPendiente()).isEqualTo(12);
        assertThat(prestamo.getFechaCierre()).isNull();
        assertThat(inventario.getCantidadDisponible()).isEqualTo(38);
        assertThat(inventario.getCantidadPrestada()).isEqualTo(12);
    }

    @Test
    @DisplayName("PU-36 [RF09] Unidades dañadas quedan registradas como NO disponibles")
    void devolucionDanadas() {
        devolver(0, 3, 0, "Base rota");

        assertThat(inventario.getCantidadDanada()).isEqualTo(3);
        assertThat(inventario.getCantidadDisponible()).isEqualTo(30);
        assertThat(prestamo.getCantidadDanada()).isEqualTo(3);
        assertThat(inventario.estaCuadrado()).isTrue();
    }

    @Test
    @DisplayName("PU-37 [RF09] Unidades que requieren mantenimiento permanecen NO disponibles")
    void devolucionMantenimiento() {
        devolver(0, 0, 4, "Requieren limpieza");

        assertThat(inventario.getCantidadMantenimiento()).isEqualTo(4);
        assertThat(inventario.getCantidadDisponible()).isEqualTo(30);
        assertThat(prestamo.getCantidadMantenimiento()).isEqualTo(4);
    }

    @Test
    @DisplayName("PU-38 [RF09] Devolución superior a las unidades pendientes es rechazada sin modificar datos")
    void devolucionSuperiorAPendientes() {
        assertThatThrownBy(() -> devolver(15, 6, 0, "Exceso"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("supera las unidades pendientes (20)");

        assertThat(prestamo.getCantidadDevuelta()).isZero();
        assertThat(inventario.getCantidadPrestada()).isEqualTo(20);
        verify(devolucionRepository, never()).save(any());
    }

    @Test
    @DisplayName("PU-39 [RF09] Cierre correcto tras devoluciones parciales: 20 = (5 buenas + 2 dañadas) + 8 + 5")
    void cierreTrasVariasParciales() {
        assertThat(devolver(5, 2, 0, "Dos jabas rotas").estadoPrestamo()).isEqualTo(EstadoPrestamo.ACTIVO);
        assertThat(prestamo.getCantidadPendiente()).isEqualTo(13);
        assertThat(devolver(8, 0, 0, null).estadoPrestamo()).isEqualTo(EstadoPrestamo.ACTIVO);
        assertThat(prestamo.getCantidadPendiente()).isEqualTo(5);
        DevolucionResponse ultima = devolver(5, 0, 0, null);

        assertThat(ultima.estadoPrestamo()).isEqualTo(EstadoPrestamo.CERRADO);
        assertThat(prestamo.getCantidadEntregada()).isEqualTo(20);
        assertThat(prestamo.getCantidadDevuelta()).isEqualTo(20);
        assertThat(prestamo.getCantidadBuenEstado()).isEqualTo(18);
        assertThat(prestamo.getCantidadDanada()).isEqualTo(2);
        assertThat(inventario.getCantidadDisponible()).isEqualTo(48);
        assertThat(inventario.getCantidadDanada()).isEqualTo(2);
        assertThat(inventario.getCantidadPrestada()).isZero();
        assertThat(inventario.getCantidadTotal()).isEqualTo(50);
    }

    @Test
    @DisplayName("PU-40 [RF09] Un préstamo CERRADO no admite más devoluciones")
    void prestamoCerrado() {
        devolver(20, 0, 0, null);

        assertThatThrownBy(() -> devolver(1, 0, 0, null))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya está cerrado");
    }

    @ParameterizedTest(name = "buen={0}, dañadas={1}, mant={2}")
    @CsvSource({"0,0,0", "-1,0,0", "0,-2,0", "3,0,-1"})
    @DisplayName("PU-41 [RNF05] Cantidades totales nulas o negativas -> ValidacionException")
    void cantidadesInvalidas(int buen, int danadas, int mant) {
        assertThatThrownBy(() -> devolver(buen, danadas, mant, "Observación"))
                .isInstanceOf(ValidacionException.class);
        verify(prestamoRepository, never()).findByIdParaActualizar(any());
    }

    @Test
    @DisplayName("PU-42 [RF09] Observaciones obligatorias cuando hay dañadas o mantenimiento; préstamo inexistente -> 404")
    void observacionesObligatorias() {
        assertThatThrownBy(() -> devolver(0, 1, 0, "  ")).isInstanceOf(ValidacionException.class);
        assertThatThrownBy(() -> devolver(0, 0, 1, null)).isInstanceOf(ValidacionException.class);
        when(prestamoRepository.findByIdParaActualizar(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.registrar(1L, new DevolucionRequest(1, 0, 0, null), "encargado"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("PU-43 [RF10] Consultas de devoluciones por préstamo y por productor")
    void consultas() {
        Devolucion d = new Devolucion(prestamo, Fixtures.AHORA, Fixtures.encargado(), null);
        d.agregarDetalle(pe.edu.agroretorno.entity.CondicionDevolucion.BUEN_ESTADO, 4);
        when(devolucionRepository.findByPrestamoId(900L)).thenReturn(List.of(d));
        when(devolucionRepository.findByProductorId(1L)).thenReturn(List.of(d));
        when(productorRepository.findByUsername("pquispe")).thenReturn(Optional.of(prestamo.getSolicitud().getProductor()));
        when(productorRepository.findByUsername("nadie")).thenReturn(Optional.empty());

        assertThat(servicio.listarPorPrestamo(900L)).singleElement()
                .satisfies(x -> assertThat(x.cantidadBuenEstado()).isEqualTo(4));
        assertThat(servicio.listarPorProductor("pquispe")).hasSize(1);
        assertThatThrownBy(() -> servicio.listarPorProductor("nadie")).isInstanceOf(OperacionNoPermitidaException.class);
    }
}
