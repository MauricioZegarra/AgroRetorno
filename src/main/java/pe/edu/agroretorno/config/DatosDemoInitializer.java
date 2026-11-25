package pe.edu.agroretorno.config;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.agroretorno.dto.DevolucionRequest;
import pe.edu.agroretorno.dto.MaterialRequest;
import pe.edu.agroretorno.dto.ProductorRequest;
import pe.edu.agroretorno.dto.SolicitudRequest;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoMaterial;
import pe.edu.agroretorno.entity.Rol;
import pe.edu.agroretorno.entity.Usuario;
import pe.edu.agroretorno.repository.UsuarioRepository;
import pe.edu.agroretorno.service.DevolucionService;
import pe.edu.agroretorno.service.MaterialService;
import pe.edu.agroretorno.service.PrestamoService;
import pe.edu.agroretorno.service.ProductorService;
import pe.edu.agroretorno.service.SolicitudService;

/**
 * Carga datos ficticios cuando se habilita el modo de demostración y la base está vacía.
 * Usa los servicios de negocio para mantener el inventario y registrar el historial.
 */
@Component
@ConditionalOnProperty(name = "agroretorno.datos-demo.habilitado", havingValue = "true")
public class DatosDemoInitializer implements ApplicationRunner {

    public static final String ENCARGADO = "encargado";
    public static final String CLAVE_ENCARGADO = "Encargado2026!";
    public static final String CLAVE_PRODUCTOR = "Productor2026!";

    private static final Logger log = LoggerFactory.getLogger(DatosDemoInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductorService productorService;
    private final MaterialService materialService;
    private final SolicitudService solicitudService;
    private final PrestamoService prestamoService;
    private final DevolucionService devolucionService;
    private final Clock clock;

    public DatosDemoInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                                ProductorService productorService, MaterialService materialService,
                                SolicitudService solicitudService, PrestamoService prestamoService,
                                DevolucionService devolucionService, Clock clock) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.productorService = productorService;
        this.materialService = materialService;
        this.solicitudService = solicitudService;
        this.prestamoService = prestamoService;
        this.devolucionService = devolucionService;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            log.info("Datos existentes detectados: no se cargan datos de demostración");
            return;
        }
        log.info("Base vacía: cargando datos ficticios de demostración");
        usuarioRepository.save(new Usuario(ENCARGADO, passwordEncoder.encode(CLAVE_ENCARGADO),
                "Rosa Huamaní Ccopa", Rol.ENCARGADO, LocalDateTime.now(clock)));

        productor("pquispe", "40112233", "Pedro", "Quispe Mamani", "951234567", "Valle de Majes - Sección B");
        productor("lmamani", "41223344", "Lucía", "Mamani Choque", "952345678", "Irrigación La Joya");
        productor("jcondori", "42334455", "Juan", "Condori Apaza", "953456789", "Valle de Vítor");
        productor("ccutipa", "43445566", "Carmen", "Cutipa Flores", "954567890", "Tiabaya - Anexo Alata");
        productor("rhuaman", "44556677", "Rubén", "Huamán Ticona", "955678901", "Valle de Tambo - Cocachacra");

        long jabaPlastica = material("JAB-001", "Jaba plástica de cosecha 20 kg", CategoriaMaterial.JABAS_COSECHA,
                "Jaba apilable para cosecha de cebolla, ajo y frutas", "jaba", 300);
        long jabaMadera = material("JAB-002", "Jaba de madera para frutas", CategoriaMaterial.JABAS_COSECHA,
                "Jaba de madera tratada para transporte de frutas", "jaba", 120);
        long carretilla = material("CAR-001", "Carretilla metálica 90 L", CategoriaMaterial.CARRETILLAS,
                "Carretilla de tolva metálica", "unidad", 15);
        material("CAR-002", "Carretilla de llanta neumática", CategoriaMaterial.CARRETILLAS,
                "Carretilla para terreno irregular", "unidad", 10);
        long balanzaPlataforma = material("BAL-001", "Balanza de plataforma 300 kg", CategoriaMaterial.BALANZAS,
                "Balanza para pesaje de cosecha en campo", "equipo", 4);
        long balanzaColgante = material("BAL-002", "Balanza colgante 50 kg", CategoriaMaterial.BALANZAS,
                "Balanza de gancho para sacos", "equipo", 8);
        long fumigadora = material("FUM-001", "Fumigadora de mochila 20 L", CategoriaMaterial.FUMIGADORAS,
                "Fumigadora manual de palanca", "equipo", 12);
        long fumigadoraMotor = material("FUM-002", "Fumigadora a motor 25 L", CategoriaMaterial.FUMIGADORAS,
                "Fumigadora motorizada a gasolina", "equipo", 5);
        long lampa = material("HER-001", "Lampa de acero", CategoriaMaterial.HERRAMIENTAS,
                "Lampa con mango de madera", "unidad", 40);
        material("HER-002", "Pico agrícola", CategoriaMaterial.HERRAMIENTAS, "Pico de doble punta", "unidad", 25);
        long tijera = material("HER-003", "Tijera de podar", CategoriaMaterial.HERRAMIENTAS,
                "Tijera para poda de frutales (retirada temporalmente)", "unidad", 10);
        inactivar(tijera);

        // Proceso completo con devolución parcial: 20 jabas, devuelve 5 buenas y 2 dañadas
        long s1 = solicitud("pquispe", jabaPlastica, 20, 15, "Cosecha de cebolla roja en parcela de 2 ha");
        entregar(s1);
        devolver(s1, 5, 2, 0, "Dos jabas con base rota");
        // Préstamo cerrado con devolución total en buen estado
        long s2 = solicitud("lmamani", carretilla, 2, 10, "Traslado de abono a la parcela");
        entregar(s2);
        devolver(s2, 2, 0, 0, null);
        // Préstamo activo
        long s3 = solicitud("jcondori", fumigadora, 1, 20, "Control fitosanitario de alfalfa");
        entregar(s3);
        // Préstamo cerrado con unidad que requiere mantenimiento
        long s4 = solicitud("jcondori", balanzaColgante, 1, 7, "Pesaje de sacos de papa");
        entregar(s4);
        devolver(s4, 0, 0, 1, "El gancho requiere calibración");
        // Solicitudes pendientes
        solicitud("ccutipa", balanzaPlataforma, 1, 5, "Pesaje de cosecha de zapallo");
        solicitud("lmamani", jabaMadera, 30, 12, "Cosecha de palta para venta en mercado");
        // Solicitud aprobada (sin entregar)
        long s7 = solicitud("pquispe", fumigadoraMotor, 2, 14, "Fumigación preventiva de vid");
        solicitudService.aprobar(s7, ENCARGADO);
        // Solicitud rechazada
        long s8 = solicitud("rhuaman", lampa, 10, 30, "Limpieza de canal de riego comunal");
        solicitudService.rechazar(s8, "La cantidad excede lo asignado por productor; solicite máximo 5", ENCARGADO);
        log.info("Datos de demostración cargados");
    }

    private void productor(String username, String dni, String nombres, String apellidos, String telefono,
                           String sector) {
        ProductorRequest r = new ProductorRequest();
        r.setUsername(username);
        r.setPassword(CLAVE_PRODUCTOR);
        r.setDni(dni);
        r.setNombres(nombres);
        r.setApellidos(apellidos);
        r.setTelefono(telefono);
        r.setSector(sector);
        r.setActivo(true);
        productorService.registrar(r, ENCARGADO);
    }

    private long material(String codigo, String nombre, CategoriaMaterial categoria, String descripcion,
                          String unidad, int cantidad) {
        MaterialRequest r = new MaterialRequest();
        r.setCodigo(codigo);
        r.setNombre(nombre);
        r.setCategoria(categoria);
        r.setDescripcion(descripcion);
        r.setUnidad(unidad);
        r.setCantidadInicial(cantidad);
        r.setEstado(EstadoMaterial.ACTIVO);
        return materialService.registrar(r, ENCARGADO).id();
    }

    private void inactivar(long materialId) {
        var m = materialService.obtener(materialId);
        MaterialRequest r = new MaterialRequest();
        r.setCodigo(m.codigo());
        r.setNombre(m.nombre());
        r.setCategoria(m.categoria());
        r.setDescripcion(m.descripcion());
        r.setUnidad(m.unidad());
        r.setEstado(EstadoMaterial.INACTIVO);
        materialService.actualizar(materialId, r, ENCARGADO);
    }

    private long solicitud(String productor, long materialId, int cantidad, int dias, String motivo) {
        return solicitudService.registrar(productor,
                new SolicitudRequest(materialId, cantidad, LocalDate.now(clock).plusDays(dias), motivo)).id();
    }

    private void entregar(long solicitudId) {
        solicitudService.aprobar(solicitudId, ENCARGADO);
        prestamoService.registrarEntrega(solicitudId, ENCARGADO);
    }

    private void devolver(long solicitudId, int buen, int danado, int mant, String obs) {
        // El préstamo se identifica por la solicitud (relación 1 a 1)
        var prestamo = prestamoService.buscarPorSolicitud(solicitudId).orElseThrow();
        devolucionService.registrar(prestamo.id(), new DevolucionRequest(buen, danado, mant, obs), ENCARGADO);
    }
}
