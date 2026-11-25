# G06 — Verificación Real de Dos Tratamientos Implementados (Pasos 3.6 y 3.7)

Para demostrar el funcionamiento real de las salvaguardas (y no únicamente capturas de código), se ejecutaron las pruebas automatizadas correspondientes sobre AgroRetorno v1.0.0 (`commit a28b7e4`) el **24/11/2026** con Eclipse Temurin JDK `17.0.17` y Apache Maven Wrapper `3.9.16`.

## Tratamiento T1 — Control de acceso por roles (RBAC), propiedad de datos, BCrypt y CSRF
- **Escenarios mitigados:** `ESC-01`, `ESC-04`, `ESC-05` (y parcialmente `ESC-03` vía hash BCrypt).
- **Mecanismos en código:** `SecurityConfig.java` (cadenas `apiSecurity` y `webSecurity`, `@EnableMethodSecurity`, `BCryptPasswordEncoder`, CSRF web habilitado por defecto), `@PreAuthorize("hasRole(...)")` en controladores API/MVC y validación de propiedad en `SolicitudService.obtenerParaProductor()`.
- **Comando exacto ejecutado:**
  ```bash
  JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test -Djava.version=17 \
    -Dtest="SeguridadIntegrationTest,SolicitudServiceTest\$Registro#restriccionesDePropiedad" -B
  ```
- **Resultado real obtenido (`control_1_rbac/ejecucion_control_1.log`):**
  - `SeguridadIntegrationTest`: **10/10 PASS** (`SEG-01` hash BCrypt `$2a$`; `SEG-02` login/logout; `SEG-03` HTTP 403 sin token CSRF; `SEG-04` 403 en acceso web cruzado por rol; `SEG-05` 401/403 en API REST; `SEG-06` HTTP 403 cuando `lmamani` intenta ver solicitud de `pquispe`).
  - `SolicitudServiceTest$Registro#restriccionesDePropiedad` (`PU-21`): **1/1 PASS** (`OperacionNoPermitidaException` para usuario no productor o solicitud ajena).
  - **Total Control T1:** `Tests run: 11, Failures: 0, Errors: 0, Skipped: 0` → **`BUILD SUCCESS`**.
- **Reportes originales conservados:** `control_1_rbac/ejecucion_control_1.log` y archivos Surefire XML/TXT.

## Tratamiento T2 — Validación estricta de cantidades, disponibilidad e integridad del inventario
- **Escenarios mitigados:** `ESC-02`, `ESC-06`.
- **Mecanismos en código:** Bean Validation (`@Valid`), reglas en `InventarioService`, `SolicitudService`, `PrestamoService` y `DevolucionService`, bloqueo pesimista `@Lock(LockModeType.PESSIMISTIC_WRITE)` (`SELECT ... FOR UPDATE`), invariante `Inventario.estaCuadrado()` y restricciones SQL `CHECK` (`ck_inventario_cuadre`, `ck_prestamo_devuelto`, `UNIQUE(solicitud_id)`).
- **Comando exacto ejecutado:**
  ```bash
  JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test -Djava.version=17 \
    -Dtest="InventarioServiceTest*,SolicitudServiceTest*,PrestamoServiceTest,DevolucionServiceTest,ProcesosNegocioIntegrationTest#validacionNoModificaDatos,SeguridadIntegrationTest#validacionDeEntradas+validacionWeb" -B
  ```
- **Resultado real obtenido (`control_2_validacion/ejecucion_control_2.log`):**
  - `InventarioServiceTest`: **17/17 PASS** (`PU-01`–`PU-15`, incluyendo límites 50 vs. 51 y cantidades `{0, -1, -20}`).
  - `SolicitudServiceTest`: **21/21 PASS** (`PU-16`–`PU-27`, incluyendo 10 combinaciones inválidas parametrizadas).
  - `PrestamoServiceTest`: **6/6 PASS** (`PU-28`–`PU-33`, revalidación de stock y bloqueo de entrega duplicada).
  - `DevolucionServiceTest`: **13/13 PASS** (`PU-34`–`PU-43`, devoluciones excedentes y cantidades negativas/cero rechazadas).
  - `ProcesosNegocioIntegrationTest#validacionNoModificaDatos` (`IT-02`): **1/1 PASS** (0 modificaciones en BD tras rechazo).
  - `SeguridadIntegrationTest#validacionDeEntradas+validacionWeb` (`SEG-08`, `SEG-10`): **2/2 PASS** (HTTP 400 / 409 / 404 y validación de formulario web).
  - **Total Control T2:** `Tests run: 60, Failures: 0, Errors: 0, Skipped: 0` → **`BUILD SUCCESS`**.
- **Reportes originales conservados:** `control_2_validacion/ejecucion_control_2.log` y archivos Surefire XML/TXT.
