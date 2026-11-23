package pe.edu.agroretorno.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.agroretorno.dto.DevolucionRequest;
import pe.edu.agroretorno.dto.FiltroHistorial;
import pe.edu.agroretorno.dto.FiltroReporte;
import pe.edu.agroretorno.dto.MaterialRequest;
import pe.edu.agroretorno.dto.MaterialResponse;
import pe.edu.agroretorno.dto.PrestamoResponse;
import pe.edu.agroretorno.dto.ProductorRequest;
import pe.edu.agroretorno.dto.ProductorResponse;
import pe.edu.agroretorno.dto.ReporteConsolidado;
import pe.edu.agroretorno.dto.SolicitudRequest;
import pe.edu.agroretorno.dto.SolicitudResponse;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoMaterial;
import pe.edu.agroretorno.entity.EstadoPrestamo;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.exception.ReglaNegocioException;
import pe.edu.agroretorno.exception.ValidacionException;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.HistorialService;
import pe.edu.agroretorno.service.MaterialService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.ProductorService;
import pe.edu.agroretorno.service.ReporteService;
import pe.edu.agroretorno.service.SolicitudService;

/**
 * Pruebas de integración de los tres procesos de negocio con todos los componentes reales
 * (servicios, repositorios JPA y transacciones). Base H2 en memoria, perfil "test".
 * Cada prueba crea sus propios datos con códigos únicos (datos aislados).
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Integración - procesos de negocio")
class ProcesosNegocioIntegrationTest {

    static final String ENCARGADO = "encargado";

    @Autowired ProductorService productorService;
    @Autowired MaterialService materialService;
    @Autowired SolicitudService solicitudService;
    @Autowired PrestamoService prestamoService;
    @Autowired DevolucionService devolucionService;
    @Autowired ReporteService reporteService;
    @Autowired HistorialService historialService;
    @Autowired Clock clock;

    private ProductorResponse nuevoProductor() {
        String sufijo = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        ProductorRequest r = new ProductorRequest();
        r.setUsername("it_" + sufijo);
        r.setPassword("ClaveSegura1");
        r.setDni(String.valueOf(10_000_000 + (int) (Math.random() * 89_999_999)));
        r.setNombres("Prueba");
        r.setApellidos("Integración");
        r.setTelefono("987654321");
        r.setSector("Valle de Siguas");
        r.setActivo(true);
        return productorService.registrar(r, ENCARGADO);
    }

    private MaterialResponse nuevoMaterial(int cantidad) {
        MaterialRequest r = new MaterialRequest();
        r.setCodigo("IT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        r.setNombre("Jaba de prueba");
        r.setCategoria(CategoriaMaterial.JABAS_COSECHA);
        r.setUnidad("jaba");
        r.setCantidadInicial(cantidad);
        r.setEstado(EstadoMaterial.ACTIVO);
        return materialService.registrar(r, ENCARGADO);
    }

    @Test
    @DisplayName("IT-01 [Procesos 1-2-3] Solicitud -> aprobación -> entrega -> devoluciones parciales -> cierre")
    void procesoCompleto() {
        ProductorResponse productor = nuevoProductor();
        MaterialResponse material = nuevoMaterial(50);

        // Proceso 1: solicitud
        SolicitudResponse s = solicitudService.registrar(productor.username(),
                new SolicitudRequest(material.id(), 20, LocalDate.now(clock).plusDays(10), "Cosecha de cebolla"));
        assertThat(s.estado()).isEqualTo(EstadoSolicitud.PENDIENTE);

        // Proceso 2: aprobación (no descuenta) y entrega (descuenta)
        solicitudService.aprobar(s.id(), ENCARGADO);
        assertThat(materialService.obtener(material.id()).cantidadDisponible()).isEqualTo(50);
        PrestamoResponse p = prestamoService.registrarEntrega(s.id(), ENCARGADO);
        assertThat(solicitudService.obtener(s.id()).estado()).isEqualTo(EstadoSolicitud.ENTREGADA);
        MaterialResponse tras = materialService.obtener(material.id());
        assertThat(tras.cantidadDisponible()).isEqualTo(30);
        assertThat(tras.cantidadPrestada()).isEqualTo(20);
        assertThatThrownBy(() -> prestamoService.registrarEntrega(s.id(), ENCARGADO))
                .isInstanceOf(ReglaNegocioException.class);

        // Proceso 3: devoluciones parciales y cierre
        devolucionService.registrar(p.id(), new DevolucionRequest(5, 2, 0, "Dos jabas rotas"), ENCARGADO);
        devolucionService.registrar(p.id(), new DevolucionRequest(8, 0, 0, null), ENCARGADO);
        assertThat(prestamoService.obtener(p.id()).estado()).isEqualTo(EstadoPrestamo.ACTIVO);
        assertThatThrownBy(() -> devolucionService.registrar(p.id(), new DevolucionRequest(6, 0, 0, null), ENCARGADO))
                .isInstanceOf(ReglaNegocioException.class);
        devolucionService.registrar(p.id(), new DevolucionRequest(4, 0, 1, "Una requiere ajuste"), ENCARGADO);

        PrestamoResponse cerrado = prestamoService.obtener(p.id());
        assertThat(cerrado.estado()).isEqualTo(EstadoPrestamo.CERRADO);
        assertThat(cerrado.cantidadPendiente()).isZero();
        MaterialResponse fin = materialService.obtener(material.id());
        assertThat(fin.cantidadDisponible()).isEqualTo(47);
        assertThat(fin.cantidadDanada()).isEqualTo(2);
        assertThat(fin.cantidadMantenimiento()).isEqualTo(1);
        assertThat(fin.cantidadPrestada()).isZero();
        assertThat(fin.cantidadTotal()).isEqualTo(50);
        assertThat(devolucionService.listarPorPrestamo(p.id())).hasSize(3);
        assertThat(devolucionService.listarPorProductor(productor.username())).hasSize(3);
    }

    @Test
    @DisplayName("IT-02 [RNF05] Una validación fallida no modifica la base de datos")
    void validacionNoModificaDatos() {
        ProductorResponse productor = nuevoProductor();
        MaterialResponse material = nuevoMaterial(5);
        int antes = solicitudService.listarPorProductor(productor.username()).size();

        assertThatThrownBy(() -> solicitudService.registrar(productor.username(),
                new SolicitudRequest(material.id(), 6, LocalDate.now(clock).plusDays(3), "Excede stock")))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> solicitudService.registrar(productor.username(),
                new SolicitudRequest(material.id(), 1, LocalDate.now(clock).minusDays(1), "Fecha pasada")))
                .isInstanceOf(ValidacionException.class);

        assertThat(solicitudService.listarPorProductor(productor.username())).hasSize(antes);
        assertThat(materialService.obtener(material.id()).cantidadDisponible()).isEqualTo(5);
    }

    @Test
    @DisplayName("IT-03 [RF10] Reporte consolidado y historial con filtros por productor, material, estado y periodo")
    void reporteEHistorial() {
        ProductorResponse productor = nuevoProductor();
        MaterialResponse material = nuevoMaterial(10);
        SolicitudResponse s1 = solicitudService.registrar(productor.username(),
                new SolicitudRequest(material.id(), 3, LocalDate.now(clock).plusDays(5), "Pesaje de cosecha"));
        SolicitudResponse s2 = solicitudService.registrar(productor.username(),
                new SolicitudRequest(material.id(), 2, LocalDate.now(clock).plusDays(5), "Traslado de abono"));
        solicitudService.rechazar(s2.id(), "Sin justificación suficiente", ENCARGADO);
        solicitudService.aprobar(s1.id(), ENCARGADO);
        PrestamoResponse p = prestamoService.registrarEntrega(s1.id(), ENCARGADO);
        devolucionService.registrar(p.id(), new DevolucionRequest(1, 0, 0, null), ENCARGADO);

        FiltroReporte f = new FiltroReporte();
        f.setProductorId(productor.id());
        f.setMaterialId(material.id());
        f.setDesde(LocalDate.now(clock).minusDays(1));
        f.setHasta(LocalDate.now(clock));
        ReporteConsolidado r = reporteService.generar(f);

        assertThat(r.solicitudes()).hasSize(2);
        assertThat(r.solicitudesPorEstado().get(EstadoSolicitud.ENTREGADA)).isEqualTo(1);
        assertThat(r.solicitudesPorEstado().get(EstadoSolicitud.RECHAZADA)).isEqualTo(1);
        assertThat(r.prestamosActivos()).isEqualTo(1);
        assertThat(r.devolucionesRegistradas()).isEqualTo(1);
        assertThat(r.unidadesPendientesDevolucion()).isEqualTo(2);
        assertThat(r.inventario().materialesRegistrados()).isEqualTo(1);
        assertThat(r.inventario().cantidadPrestada()).isEqualTo(2);

        f.setEstado(EstadoSolicitud.RECHAZADA);
        assertThat(reporteService.generar(f).solicitudes()).singleElement()
                .satisfies(x -> assertThat(x.id()).isEqualTo(s2.id()));
        assertThat(reporteService.generar(null).inventario().materialesRegistrados()).isGreaterThan(1);

        FiltroHistorial h = new FiltroHistorial();
        h.setActor(productor.username());
        h.setEntidad("Solicitud");
        h.setAccion("REGISTRAR_SOLICITUD");
        h.setDesde(LocalDate.now(clock).minusDays(1));
        h.setHasta(LocalDate.now(clock));
        assertThat(historialService.consultar(h)).hasSize(2);
    }

    @Test
    @DisplayName("IT-04 [RF03] Registro y actualización de productor: usuario y DNI únicos, contraseña con hash")
    void gestionDeProductores() {
        ProductorResponse p = nuevoProductor();
        ProductorRequest duplicado = new ProductorRequest();
        duplicado.setUsername(p.username());
        duplicado.setPassword("OtraClave99");
        duplicado.setDni("99999999");
        duplicado.setNombres("Otro");
        duplicado.setApellidos("Productor");
        duplicado.setSector("Majes");
        assertThatThrownBy(() -> productorService.registrar(duplicado, ENCARGADO)).isInstanceOf(ReglaNegocioException.class);

        ProductorRequest cambio = new ProductorRequest();
        cambio.setDni(p.dni());
        cambio.setNombres("Nombre Actualizado");
        cambio.setApellidos("Apellido");
        cambio.setSector("Valle de Tambo");
        cambio.setTelefono("");
        cambio.setActivo(false);
        cambio.setPassword("NuevaClave2026");
        ProductorResponse actualizado = productorService.actualizar(p.id(), cambio, ENCARGADO);

        assertThat(actualizado.nombreCompleto()).isEqualTo("Nombre Actualizado Apellido");
        assertThat(actualizado.activo()).isFalse();
        assertThat(actualizado.telefono()).isNull();
        assertThat(productorService.listar(p.dni())).hasSize(1);
        assertThat(productorService.obtenerPorUsername(p.username()).sector()).isEqualTo("Valle de Tambo");
    }

    @Test
    @DisplayName("IT-05 [RF04] Registro y modificación de material: código único y estado INACTIVO bloquea solicitudes")
    void gestionDeMateriales() {
        MaterialResponse m = nuevoMaterial(7);
        MaterialRequest dup = new MaterialRequest();
        dup.setCodigo(m.codigo().toLowerCase());
        dup.setNombre("Duplicado");
        dup.setCategoria(CategoriaMaterial.BALANZAS);
        dup.setUnidad("equipo");
        assertThatThrownBy(() -> materialService.registrar(dup, ENCARGADO)).isInstanceOf(ReglaNegocioException.class);

        MaterialRequest cambio = new MaterialRequest();
        cambio.setCodigo(m.codigo());
        cambio.setNombre("Jaba renovada");
        cambio.setCategoria(CategoriaMaterial.JABAS_COSECHA);
        cambio.setUnidad("jaba");
        cambio.setDescripcion("  ");
        cambio.setEstado(EstadoMaterial.INACTIVO);
        MaterialResponse actualizado = materialService.actualizar(m.id(), cambio, ENCARGADO);

        assertThat(actualizado.nombre()).isEqualTo("Jaba renovada");
        assertThat(actualizado.descripcion()).isNull();
        assertThat(actualizado.cantidadTotal()).isEqualTo(7);
        ProductorResponse productor = nuevoProductor();
        assertThatThrownBy(() -> solicitudService.registrar(productor.username(),
                new SolicitudRequest(m.id(), 1, LocalDate.now(clock).plusDays(2), "Uso en campo")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("inactivo");
    }
}
