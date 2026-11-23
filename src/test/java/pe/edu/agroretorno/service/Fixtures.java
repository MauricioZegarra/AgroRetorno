package pe.edu.agroretorno.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import pe.edu.agroretorno.config.ReglasNegocioProperties;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoMaterial;
import pe.edu.agroretorno.entity.EstadoSolicitud;
import pe.edu.agroretorno.entity.Inventario;
import pe.edu.agroretorno.entity.Material;
import pe.edu.agroretorno.entity.Prestamo;
import pe.edu.agroretorno.entity.Productor;
import pe.edu.agroretorno.entity.Rol;
import pe.edu.agroretorno.entity.Solicitud;
import pe.edu.agroretorno.entity.Usuario;

/**
 * Datos de prueba reproducibles para las pruebas unitarias (G07).
 * Fecha fija: 17/11/2026 10:00 (America/Lima) para que el resultado no dependa del día de ejecución.
 */
final class Fixtures {

    static final ZoneId ZONA = ZoneId.of("America/Lima");
    static final Clock RELOJ = Clock.fixed(ZonedDateTime.of(2026, 11, 17, 10, 0, 0, 0, ZONA).toInstant(), ZONA);
    static final LocalDate HOY = LocalDate.now(RELOJ);
    static final LocalDateTime AHORA = LocalDateTime.now(RELOJ);

    private Fixtures() {
    }

    static ReglasNegocioProperties reglas() {
        ReglasNegocioProperties r = new ReglasNegocioProperties();
        r.setPlazoMinimoDias(1);
        r.setPlazoMaximoDias(90);
        r.setLongitudMinimaMotivo(5);
        return r;
    }

    static Usuario encargado() {
        Usuario u = new Usuario("encargado", "hash", "Rosa Huamaní", Rol.ENCARGADO, AHORA);
        u.setId(1L);
        return u;
    }

    static Productor productor(long id, String username) {
        Usuario u = new Usuario(username, "hash", "Productor " + id, Rol.PRODUCTOR, AHORA);
        u.setId(100 + id);
        Productor p = new Productor(u, String.format("4%07d", id), "Nombre" + id, "Apellido", "951234567",
                "Valle de Majes", AHORA);
        p.setId(id);
        return p;
    }

    /** Material ACTIVO con inventario cuadrado: total = disponible + prestada + dañada + mantenimiento. */
    static Material material(long id, int disponible, int prestada) {
        Material m = new Material("JAB-00" + id, "Jaba plástica", CategoriaMaterial.JABAS_COSECHA, "Jaba de cosecha",
                "jaba", AHORA);
        m.setId(id);
        Inventario inv = new Inventario(m, disponible + prestada, AHORA);
        inv.setId(id);
        inv.setCantidadDisponible(disponible);
        inv.setCantidadPrestada(prestada);
        m.setInventario(inv);
        return m;
    }

    static Material materialInactivo(long id, int disponible) {
        Material m = material(id, disponible, 0);
        m.setEstado(EstadoMaterial.INACTIVO);
        return m;
    }

    static Solicitud solicitud(long id, Productor p, Material m, int cantidad, EstadoSolicitud estado) {
        Solicitud s = new Solicitud(p, m, cantidad, AHORA, HOY.plusDays(10), "Cosecha de cebolla");
        s.setId(id);
        s.setEstado(estado);
        return s;
    }

    /** Préstamo ACTIVO con las unidades entregadas ya descontadas del inventario (disponible → prestada). */
    static Prestamo prestamo(long id, Solicitud s, int entregadas) {
        Prestamo p = new Prestamo(s, entregadas, AHORA, encargado());
        p.setId(id);
        s.setEstado(EstadoSolicitud.ENTREGADA);
        s.setPrestamo(p);
        return p;
    }
}
