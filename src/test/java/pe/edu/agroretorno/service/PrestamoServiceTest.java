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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Solicitud;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.repository.InventarioRepository;
import pe.edu.agroretorno.repository.MaterialRepository;
import pe.edu.agroretorno.repository.MovimientoInventarioRepository;
import pe.edu.agroretorno.repository.PrestamoRepository;
import pe.edu.agroretorno.repository.ProductorRepository;
import pe.edu.agroretorno.repository.SolicitudRepository;

/**
 * Componente 2 (continuación): entrega física de solicitudes aprobadas (RF08).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Componente 2 - PrestamoService (entregas)")
class PrestamoServiceTest {

    @Mock PrestamoRepository prestamoRepository;
    @Mock SolicitudRepository solicitudRepository;
    @Mock ProductorRepository productorRepository;
    @Mock InventarioRepository inventarioRepository;
    @Mock MaterialRepository materialRepository;
    @Mock MovimientoInventarioRepository movimientoRepository;
    @Mock UsuarioService usuarioService;
    @Mock HistorialService historialService;

    PrestamoService servicio;
    Productor pedro;
    Material jabas;
    Solicitud aprobada;

    @BeforeEach
    void preparar() {
        InventarioService inventario = new InventarioService(inventarioRepository, materialRepository,
                movimientoRepository, usuarioService, historialService, Fixtures.RELOJ);
        servicio = new PrestamoService(prestamoRepository, solicitudRepository, productorRepository, inventario,
                usuarioService, historialService, Fixtures.RELOJ);
        pedro = Fixtures.productor(1L, "pquispe");
        jabas = Fixtures.material(1L, 50, 0);
        aprobada = Fixtures.solicitud(10L, pedro, jabas, 20, EstadoSolicitud.APROBADA);
        when(usuarioService.obtenerPorUsername("encargado")).thenReturn(Fixtures.encargado());
        when(solicitudRepository.findByIdParaActualizar(10L)).thenReturn(Optional.of(aprobada));
        when(inventarioRepository.findByMaterialIdParaActualizar(1L)).thenReturn(Optional.of(jabas.getInventario()));
        when(inventarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(i -> {
            Prestamo p = i.getArgument(0);
            p.setId(900L);
            return p;
        });
    }

    @Test
    @DisplayName("PU-28 [RF08] Entrega correcta: crea préstamo ACTIVO, solicitud ENTREGADA, inventario 50->30 / prestado 20")
    void entregaCorrecta() {
        PrestamoResponse p = servicio.registrarEntrega(10L, "encargado");

        assertThat(p.id()).isEqualTo(900L);
        assertThat(p.estado()).isEqualTo(EstadoPrestamo.ACTIVO);
        assertThat(p.cantidadEntregada()).isEqualTo(20);
        assertThat(p.cantidadPendiente()).isEqualTo(20);
        assertThat(p.entregadoPor()).isEqualTo("Rosa Huamaní");
        assertThat(aprobada.getEstado()).isEqualTo(EstadoSolicitud.ENTREGADA);
        assertThat(jabas.getInventario().getCantidadDisponible()).isEqualTo(30);
        assertThat(jabas.getInventario().getCantidadPrestada()).isEqualTo(20);
    }

    @Test
    @DisplayName("PU-29 [RF08] No se puede entregar material sin aprobación (solicitud PENDIENTE)")
    void entregaSinAprobacion() {
        aprobada.setEstado(EstadoSolicitud.PENDIENTE);

        assertThatThrownBy(() -> servicio.registrarEntrega(10L, "encargado"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("sin aprobación");
        verify(prestamoRepository, never()).save(any());
        assertThat(jabas.getInventario().getCantidadDisponible()).isEqualTo(50);
    }

    @Test
    @DisplayName("PU-30 [RF08] Revalidación de existencias al entregar: si el stock bajó, la entrega se bloquea")
    void revalidacionDeExistencias() {
        // Después de la aprobación otro préstamo consumió unidades: solo quedan 15 disponibles
        jabas.getInventario().setCantidadDisponible(15);
        jabas.getInventario().setCantidadPrestada(35);

        assertThatThrownBy(() -> servicio.registrarEntrega(10L, "encargado"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("insuficientes");
        assertThat(aprobada.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("PU-31 [RF08] Una solicitud genera como máximo un préstamo (entrega duplicada rechazada)")
    void entregaDuplicada() {
        when(prestamoRepository.existsBySolicitudId(10L)).thenReturn(true);

        assertThatThrownBy(() -> servicio.registrarEntrega(10L, "encargado"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya fue entregada");
        verify(inventarioRepository, never()).findByMaterialIdParaActualizar(any());
    }

    @Test
    @DisplayName("PU-32 [RF08] Material inactivado después de la aprobación: entrega bloqueada; solicitud inexistente: 404")
    void materialInactivoOSolicitudInexistente() {
        jabas.setEstado(pe.edu.agroretorno.entity.EstadoMaterial.INACTIVO);
        when(solicitudRepository.findByIdParaActualizar(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.registrarEntrega(10L, "encargado")).isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> servicio.registrarEntrega(99L, "encargado"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("PU-33 [RF10] Consultas de préstamos: activos, por productor, por solicitud y préstamo vencido")
    void consultas() {
        Prestamo p = Fixtures.prestamo(900L, aprobada, 20);
        Solicitud vieja = Fixtures.solicitud(11L, pedro, jabas, 2, EstadoSolicitud.APROBADA);
        Prestamo vencido = new Prestamo(vieja, 2, Fixtures.AHORA.minusDays(30), Fixtures.encargado()) {
            @Override
            public java.time.LocalDate getFechaDevolucionPrevista() {
                return Fixtures.HOY.minusDays(1);
            }
        };
        when(prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO)).thenReturn(List.of(p, vencido));
        when(prestamoRepository.findByProductorId(1L)).thenReturn(List.of(p));
        when(prestamoRepository.findBySolicitudId(10L)).thenReturn(Optional.of(p));
        when(prestamoRepository.findById(900L)).thenReturn(Optional.of(p));
        when(productorRepository.findByUsername("pquispe")).thenReturn(Optional.of(pedro));
        when(productorRepository.findByUsername("encargado")).thenReturn(Optional.empty());

        assertThat(servicio.listar(null)).extracting(PrestamoResponse::vencido).containsExactly(false, true);
        assertThat(servicio.listarPorProductor("pquispe")).hasSize(1);
        assertThat(servicio.buscarPorSolicitud(10L)).isPresent();
        assertThat(servicio.obtener(900L).cantidadEntregada()).isEqualTo(20);
        assertThatThrownBy(() -> servicio.obtener(1L)).isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> servicio.listarPorProductor("encargado"))
                .isInstanceOf(OperacionNoPermitidaException.class);
    }
}
