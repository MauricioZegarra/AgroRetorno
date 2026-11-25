**UNIVERSIDAD NACIONAL DE SAN AGUSTÍN DE AREQUIPA  
FACULTAD DE INGENIERÍA DE PRODUCCIÓN Y SERVICIOS  
ESCUELA PROFESIONAL DE INGENIERÍA DE SISTEMAS  **

**PROYECTO AGRORETORNO v1.0.0  
INFORME DE GESTIÓN DE RIESGOS  
(GUÍA 06 — METODOLOGÍA MAGERIT v3)  **
*Identificación de activos, escenarios activo–amenaza, valoración de riesgos, plan de salvaguardas,  
verificación real de controles implementados y matriz comparativa PILAR vs. RMAT*

| **Atributo del Documento** | **Detalle** |
|:---|:---|
| **Asignatura** | Calidad de Software — Semestre 8 (2026-B) |
| **Docente** | M. en C. Jesús Martín Silva Fernández |
| **Integrante responsable (G06 y G07)** | Marco Antonio Marquez Herrera (mmarquezhe@unsa.edu.pe) |
| **Sistema evaluado** | AgroRetorno v1.0.0 (commit base a28b7e4 · Spring Boot 3.5.16 / PostgreSQL) |
| **Línea base de requisitos** | 15 RF (RF01–RF15) y 10 RNF (RNF01–RNF10) alineados con G03/G04 |
| **Rama Git de entrega** | feature/marco-g06-gestion-de-riesgos |
| **Archivo editable principal** | 03_Matriz_MAGERIT_Editable/G06_Matriz_Riesgos_MAGERIT_AgroRetorno.xlsx (220 fórmulas, 0 errores) |
| **Fecha de elaboración** | 24 de noviembre de 2026 |

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th><strong>Resumen Ejecutivo de Resultados (G06)<br />
</strong>Se evaluaron 8 activos reales de AgroRetorno y 10 escenarios activo–amenaza codificados bajo MAGERIT v3. En estado inherente se detectaron 4 escenarios CRÍTICOS, 5 ALTOS y 1 MEDIO (riesgo promedio 13.55/25; pico máximo 20.00/25). Al aplicar las salvaguardas implementadas y planificadas, el riesgo promedio desciende a 1.54/25 (-88.6%), eliminando por completo los niveles Crítico y Alto (0 Críticos, 0 Altos, 0 Medios y 10 Bajos). Adicionalmente, se verificaron con pruebas reales ejecutadas mediante Maven/JUnit 5 los dos tratamientos seleccionados: Control T1 (RBAC, propiedad de datos y CSRF: 11/11 PASS) y Control T2 (validación de cantidades, disponibilidad e integridad del inventario: 60/60 PASS).</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

**1. Objetivo, Alcance y Línea Base del Sistema Evaluado**

Aplicar formalmente el proceso de gestión de riesgos de la metodología MAGERIT v3 (alineada a las normas ISO 31000 e ISO/IEC 27005) sobre el sistema real AgroRetorno v1.0.0, evaluando las herramientas PILAR, RMAT y alternativas abiertas, valorando activos y escenarios de amenaza, priorizando salvaguardas y comprobando mediante ejecución real de pruebas automatizadas dos controles críticos de seguridad e integridad.

El análisis toma como base el repositorio oficial del equipo MauricioZegarra/AgroRetorno (commit a28b7e4, copia v1.0.0 con pom.xml y Maven Wrapper 3.9.16 para ejecución reproducible de pruebas sin alterar la lógica de negocio). Para mantener plena coherencia con las entregas de G03 y G04, se adopta la línea base ampliada de 15 Requisitos Funcionales (RF01–RF15) y 10 Requisitos No Funcionales (RNF01–RNF10), documentando explícitamente su correspondencia con los identificadores originales del código fuente.

**2. Evaluación de PILAR, Alternativa Utilizada y Matriz RMAT vs. PILAR**

**2.1 Registro de evaluación e impedimento de PILAR (Paso 3.1)**

PILAR (Procedimiento Informático-Lógico de Análisis de Riesgos), desarrollado por el Centro Criptológico Nacional de España (CCN-CERT), es la herramienta de referencia para aplicar MAGERIT v3. Durante la verificación en el entorno de trabajo del integrante (Nobara Linux 44, kernel 7.2.11 x86_64, OpenJDK 25 / Eclipse Temurin JDK 17) se constataron tres impedimentos objetivos para su operación completa:

- **Licenciamiento propietario:** PILAR es software comercial de pago distribuido bajo licencia anual institucional/comercial (CCN-CERT / ARST). La versión de evaluación restringe el guardado, exportación completa de informes y portabilidad del archivo .car.

- **Disponibilidad en Linux:** No existe un paquete libre ni instalador oficial comunitario en repositorios Linux sin licencia registrada previa.

- **Reproducibilidad y auditoría abierta:** Para que todos los integrantes y el docente puedan inspeccionar, recalcular y auditar cada fórmula sin licencias comerciales, se construyó una matriz MAGERIT v3 completa en Excel/LibreOffice (.xlsx) con 220 fórmulas dinámicas verificadas con 0 errores, sujeta a validación docente conforme al Paso 3.1.

**2.2 Matriz comparativa de actividades: PILAR vs. RMAT vs. Alternativa (Paso 3.8 y LAB06)**

| **Actividad / Fase** | **PILAR (CCN-CERT)** | **RMAT** | **Alternativa Excel MAGERIT** | **Pirani (Ref.)** | **Limitaciones clave** |
|:---|:---|:---|:---|:---|:---|
| **Identificación y catálogo de activos** | Catálogo jerárquico MAGERIT incorporado (\[D\], \[S\], \[SW\], \[HW\], \[COM\], \[P\]). | Registro manual de activos/elementos en formato tabular simplificado. | Hoja Activos_y_Dependencias con codificación MAGERIT v3 completa y trazable. | Registro web por procesos y activos en plan gratuito limitado. | PILAR requiere licencia comercial/institucional (CCN-CERT) y licencia anual; versión demo restringe exportación/proyectos. |
| **Modelado de dependencias entre activos** | Grafo formal de dependencias con propagación automática de valor acumulado vs. repercutido. | Limitado: suele evaluar activos de forma plana sin propagación multinivel automática. | Documentado explícitamente en columna de dependencias y grafo visual PNG. | Asociación básica proceso–riesgo; sin árbol MAGERIT multinivel. | En RMAT y hojas simples la propagación en cascada debe auditarse manualmente. |
| **Catálogo de amenazas y dimensiones (C,I,D,A,T)** | Catálogo oficial MAGERIT Libro II (\[N\], \[I\], \[E\], \[A\]) con 5 dimensiones integradas. | Basado en probabilidad × impacto genérico (C/I/D simplificado o único valor). | 10 escenarios con códigos MAGERIT \[A.x\]/\[E.x\]/\[I.x\] y 5 dimensiones C/I/D/A/T. | Catálogo configurable orientado a ISO 31000 (riesgo operativo/seguridad). | RMAT no trae nativamente el catálogo MAGERIT ni sus 5 dimensiones diferenciadas. |
| **Análisis cualitativo y cuantitativo** | Soporta ambos modos (niveles y valoración monetaria con ROI de salvaguardas). | Predominantemente cualitativo/semicuantitativo (matrices 3x3 o 5x5). | Semicuantitativo transparente con fórmulas Excel verificables (0–25 y % reducción). | Mapas de calor 5x5 y evaluación cualitativa en la nube. | El análisis cuantitativo puro exige datos históricos de pérdidas económicas inexistentes en proyectos nuevos. |
| **Gestión de salvaguardas y riesgo residual** | Mapeo directo a perfiles ENS / ISO 27002, madurez L0–L5 y evolución temporal por fases. | Registro de controles y recálculo básico de probabilidad/impacto residual. | Fórmulas de eficacia sobre degradación y frecuencia, KPI, responsable y enlace a pruebas reales. | Planes de acción y alertas de seguimiento (con límites en plan Free). | RMAT carece de perfiles de cumplimiento normativo (ENS/ISO 27002) automatizados. |
| **Exportación, auditoría y reproducibilidad** | Exportación RTF/HTML/CSV/XML y proyecto .car (bloqueada o limitada sin licencia completa). | Exportación básica a CSV/Excel/PDF según variante. | Archivo .xlsx abierto 100% editable, sin vendor lock-in, versionable en Git y auditable en Excel/LibreOffice. | Dependiente de cuenta online; exportaciones completas solo en planes pagos. | PILAR no ofrece paquete nativo oficial libre para distribuciones Linux académicas sin licencia previa. |

**3. Metodología MAGERIT v3 y Escalas de Valoración (Paso 3.4)**

Siguiendo el Libro I (Método) y Libro II (Catálogo de Elementos) de MAGERIT v3, cada activo se valora en las cinco dimensiones de seguridad: Confidencialidad \[C\], Integridad \[I\], Disponibilidad \[D\], Autenticidad \[A\] y Trazabilidad \[T\] sobre una escala ordinal cuantificable de 0 a 5. El valor agregado del activo se toma como el máximo entre las dimensiones relevantes (V = MAX(C,I,D,A,T)). Para cada escenario activo–amenaza se calcula:

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th>Impacto Inherente (Imp) = Valor Activo (V) × Degradación (Deg %) [escala 0.00 – 5.00]<br />
Riesgo Inherente (R_inh) = Impacto Inherente × Frecuencia (1–5) [escala 0.00 – 25.00]<br />
Impacto Residual (Imp_r) = Imp × (1 - Eficacia_Degradación %)<br />
Frecuencia Residual (Fr) = MAX(1, ROUND(Frecuencia × (1 - Eficacia_Frec %), 2))<br />
Riesgo Residual (R_res) = Imp_r × Fr [Reducción % = (R_inh - R_res) / R_inh]<br />
Clasificación automática : CRÍTICO (&gt;=15) | ALTO (9–14.99) | MEDIO (4–8.99) | BAJO (&lt;4)</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

**4. Catálogo de Activos, Responsables y Dependencias (Paso 3.2)**

Se identificaron ocho activos específicos de AgroRetorno v1.0.0 (superando el mínimo de cinco exigido), trazados directamente a las tablas de V1\_\_esquema_inicial.sql, las clases de pe.edu.agroretorno y la infraestructura de despliegue en Render / Docker:

| **ID** | **Tipo** | **Nombre del Activo** | **Responsable** | **Depende de** | **C/I/D/A/T** | **V** |
|:--:|:--:|:---|:---|:---|:--:|:--:|
| **ACT-01** | \[D\] Datos / BD | Base de datos PostgreSQL | Administrador TI / Encargado | ACT-06 | 4/5/5/3/4 | 5 |
| **ACT-02** | \[D\] Credenciales | Credenciales y cuentas de usuario | Admin. Seguridad / Encargado | ACT-01, ACT-05 | 5/5/4/5/4 | 5 |
| **ACT-03** | \[D\] Datos operativos | Registros de préstamos, solicitudes e inventario | Encargado de Almacén | ACT-01, ACT-05 | 3/5/5/4/5 | 5 |
| **ACT-04** | \[D\] Datos personales | Datos de productores agrícolas | Encargado / Cooperativa | ACT-01, ACT-05 | 5/4/3/3/3 | 5 |
| **ACT-05** | \[SW\] Aplicación | Aplicación web MVC y API REST AgroRetorno | Equipo Desarrollo / Marco Marquez | ACT-06, ACT-01 | 3/5/5/4/4 | 5 |
| **ACT-06** | \[HW/S\] Servidor | Servidor de despliegue y runtime | Responsable Infraestructura | ACT-07 | 3/4/5/3/3 | 5 |
| **ACT-07** | \[COM\] Red / Canal | Canal de comunicaciones HTTP/HTTPS | Responsable Infraestructura | Ninguno (soporte base) | 5/4/4/4/3 | 5 |
| **ACT-08** | \[S\] Auditoría | Servicio de historial, movimientos y respaldos | Encargado / Auditor Calidad | ACT-01, ACT-05, ACT-06 | 3/5/4/4/5 | 5 |

<img src="media/image1.png" style="width:6.45in;height:3.07349in" />

*Figura 1. Grafo de dependencias entre activos esenciales, capa de aplicación/datos e infraestructura de AgroRetorno.*

**5. Escenarios Activo–Amenaza y Valoración Inherente (Pasos 3.3 y 3.4)**

Se modelaron 10 escenarios activo–amenaza concretos sobre AgroRetorno (superando el mínimo de 8), utilizando la codificación oficial de amenazas del Libro II de MAGERIT (\[A\] Ataques intencionados, \[E\] Errores y fallos no intencionados, \[I\] Origen industrial). La tabla presenta el cálculo automático extraído del libro Excel:

| **ID** | **Activo** | **Amenaza MAGERIT** | **Descripción del Escenario** | **Dim.** | **Deg.** | **Imp.** | **Fr.** | **R.Inh.** | **Nivel** | **Rk** |
|:--:|:--:|:---|:---|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
| **ESC-01** | ACT-05 | \[A.11\] Acceso no autorizado | Productor intenta acceder a rutas administrativas (/encargado/\*\*, /api/encargado/\*\*) para aprobar solicitudes o modificar inventario. | I, A, T | 80% | 4.00 | 4 | 16.00 | **CRÍTICO** | \#3 |
| **ESC-02** | ACT-03 | \[E.1 / A.15\] Modificación indebida / Error de datos | Ingreso de cantidades negativas, cero, superiores al stock o devoluciones que exceden lo pendiente, descuadrando el inventario. | I, D | 80% | 4.00 | 5 | 20.00 | **CRÍTICO** | \#1 |
| **ESC-03** | ACT-02 | \[A.5\] Suplantación de identidad | Explotación de credenciales demo por defecto (DEMO_DATA=true) o ataques de fuerza bruta sobre /login y HTTP Basic /api/\*\* sin bloqueo. | C, I, A | 100% | 5.00 | 4 | 20.00 | **CRÍTICO** | \#1 |
| **ESC-04** | ACT-04 | \[A.11\] Acceso no autorizado (IDOR) | Un productor autenticado manipula identificadores (/api/productor/solicitudes/{id}) para ver datos o solicitudes de otro productor. | C, A | 80% | 4.00 | 4 | 16.00 | **CRÍTICO** | \#3 |
| **ESC-05** | ACT-05 | \[A.22\] Falsificación de peticiones (CSRF) | Un atacante induce a un encargado autenticado a enviar formularios POST fraudulentos (aprobar solicitud o ajustar existencias). | I, A | 80% | 4.00 | 3 | 12.00 | **ALTO** | \#5 |
| **ESC-06** | ACT-03 | \[E.20 / E.21\] Condición de carrera en entregas | Dos operaciones concurrentes intentan entregar la misma solicitud o descontar las últimas unidades, dejando stock negativo o préstamo duplicado. | I, D | 80% | 4.00 | 3 | 12.00 | **ALTO** | \#5 |
| **ESC-07** | ACT-01 | \[I.5 / E.2\] Avería lógica o pérdida de BD | Fallo de disco/contenedor PostgreSQL o error de administración que corrompe o borra el historial y préstamos activos sin respaldo probado. | D, I, T | 100% | 5.00 | 2 | 10.00 | **ALTO** | \#8 |
| **ESC-08** | ACT-07 | \[A.14\] Interceptación de tráfico / credenciales | Captura de cabeceras Authorization (HTTP Basic en Base64) o cookies de sesión si el canal carece de HTTPS o se usa en red insegura. | C, A | 80% | 4.00 | 3 | 12.00 | **ALTO** | \#5 |
| **ESC-09** | ACT-06 | \[A.24 / I.6\] Indisponibilidad / Saturación de servidor | Caída o latencia extrema en Render (plan gratuito/hibernación) o agotamiento del pool HikariCP (10 conexiones) en picos de cosecha. | D | 50% | 2.50 | 4 | 10.00 | **ALTO** | \#8 |
| **ESC-10** | ACT-08 | \[A.13\] Repudio / Pérdida de trazabilidad | Imposibilidad de acreditar qué usuario aprobó una solicitud, entregó materiales o registró un ajuste manual en existencias. | T, I | 50% | 2.50 | 3 | 7.50 | **MEDIO** | \#10 |

<img src="media/image2.png" style="width:6.55in;height:2.90215in" />

*Figura 2. Mapa de calor comparativo de los 10 escenarios MAGERIT v3: estado inherente (izquierda) vs. residual (derecha).*

**6. Priorización, Salvaguardas y Riesgo Residual (Paso 3.5)**

Los escenarios se priorizaron según su puntuación inherente (ESC-02 y ESC-03 lideran con 20.00 — CRÍTICO; ESC-01 y ESC-04 siguen con 16.00 — CRÍTICO). Para cada escenario se asignó salvaguarda concreta, responsable, fecha objetivo, esfuerzo/costo estimado en soles, indicador verificable (KPI) y eficacia proyectada:

| **ID** | **Estado** | **Salvaguarda / Tratamiento** | **Responsable** | **Fecha** | **Esfuerzo/Costo** | **R. Res.** | **Nivel** | **Red.** |
|:--:|:--:|:---|:---|:--:|:--:|:--:|:--:|:--:|
| **ESC-01** | IMPLEMENTADO Y PROBADO (T1) | T1: Control de acceso RBAC en doble capa (SecurityConfig por URL + @PreAuthorize en controladores) y respuesta 403/401 controlada. | Marco Marquez (verif.) / Equipo Dev | 24/11/2026 (Cumplido) | 0 h adic. / S/ 0 (ya en v1.0.0) | 1.40 | **BAJO** | -91.2% |
| **ESC-02** | IMPLEMENTADO Y PROBADO (T2) | T2: Validación estricta Bean Validation (@Valid) + reglas en InventarioService/SolicitudService/DevolucionService + CHECK SQL (ck_inventario_cuadre, ck_prestamo_devuelto). | Marco Marquez (verif.) / Equipo Dev | 24/11/2026 (Cumplido) | 0 h adic. / S/ 0 (ya en v1.0.0) | 1.00 | **BAJO** | -95.0% |
| **ESC-03** | PARCIAL / PLANIFICADO | Hash BCrypt ya activo (SEG-01). Plan: forzar DEMO_DATA=false en producción, rotar credenciales por env y agregar rate-limiting/bloqueo tras 5 intentos fallidos. | Mauricio Zegarra / Admin. Seguridad | 02/12/2026 | 4 h / S/ 160 | 3.00 | **BAJO** | -85.0% |
| **ESC-04** | IMPLEMENTADO Y PROBADO (T1) | T1 (propiedad): Verificación explícita en SolicitudService.obtenerParaProductor y filtrado por usuario autenticado (Authentication.getName()). | Marco Marquez (verif.) / Equipo Dev | 24/11/2026 (Cumplido) | 0 h adic. / S/ 0 (ya en v1.0.0) | 1.20 | **BAJO** | -92.5% |
| **ESC-05** | IMPLEMENTADO Y PROBADO (T1) | Token CSRF habilitado por defecto en Spring Security para formularios web + cookie HttpOnly y SameSite=Lax + migrateSession(). | Marco Marquez (verif.) / Equipo Dev | 24/11/2026 (Cumplido) | 0 h adic. / S/ 0 (ya en v1.0.0) | 1.20 | **BAJO** | -90.0% |
| **ESC-06** | IMPLEMENTADO Y PROBADO (T2) | Bloqueo pesimista (@Lock PESSIMISTIC_WRITE / SELECT FOR UPDATE) en InventarioRepository y SolicitudRepository + UNIQUE(solicitud_id). | Marco Marquez (verif.) / Equipo Dev | 24/11/2026 (Cumplido) | 0 h adic. / S/ 0 (ya en v1.0.0) | 1.00 | **BAJO** | -91.7% |
| **ESC-07** | PLANIFICADO | Respaldo automatizado diario (pg_dump) con prueba mensual de restauración usando scripts en database/ y política de retención 30 días. | Mauricio Zegarra / Encargado TI | 02/12/2026 | 3 h / S/ 120 | 1.50 | **BAJO** | -85.0% |
| **ESC-08** | PARCIAL / PLANIFICADO | TLS obligatorio vía proxy HTTPS (server.forward-headers-strategy=framework) + activar cookie Secure y migrar API de HTTP Basic a JWT/OAuth2 en v2. | Mauricio Zegarra / Equipo Dev | 02/12/2026 | 6 h / S/ 240 | 1.47 | **BAJO** | -87.8% |
| **ESC-09** | PLANIFICADO (G08) | Pruebas de carga/estrés/estabilidad JMeter (G08), ajuste de índices SQL y pool HikariCP, más monitoreo /actuator/health. | Mauricio Zegarra (G08) | 02/12/2026 | 5 h / S/ 200 | 2.70 | **BAJO** | -73.0% |
| **ESC-10** | IMPLEMENTADO Y PROBADO | Auditoría automática vía HistorialService y MovimientoInventario en cada transacción crítica (registro, aprobación, rechazo, entrega, devolución, ajuste). | Marco Marquez (verif.) / Equipo Dev | 24/11/2026 (Cumplido) | 0 h adic. / S/ 0 (ya en v1.0.0) | 0.92 | **BAJO** | -87.8% |

<img src="media/image3.png" style="width:6.45in;height:3.45283in" />

*Figura 3. Reducción cuantitativa del riesgo inherente al riesgo residual por escenario tras aplicar salvaguardas.*

**7. Verificación Real de Dos Tratamientos Implementados (Pasos 3.6 y 3.7)**

En cumplimiento de los Pasos 3.6 y 3.7, se seleccionaron dos tratamientos ya implementados en AgroRetorno v1.0.0 correspondientes a los riesgos críticos de mayor prioridad y se comprobó su funcionamiento real ejecutando pruebas automatizadas de integración HTTP (MockMvc + Spring Security) y pruebas unitarias con Maven Surefire, conservando los logs completos y reportes XML/TXT originales en 04_Tratamientos_y_Pruebas_Controles/.

**7.1 Tratamiento T1 — Control de acceso por roles (RBAC), propiedad de datos y CSRF**

Mitiga los escenarios ESC-01 (acceso no autorizado a rutas administrativas), ESC-04 (IDOR sobre solicitudes de otros productores) y ESC-05 (falsificación CSRF). Está implementado en SecurityConfig.java (cadenas apiSecurity y webSecurity, @EnableMethodSecurity, BCryptPasswordEncoder y CSRF activo por defecto), anotaciones @PreAuthorize en controladores y validación de pertenencia en SolicitudService.obtenerParaProductor().

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th># Comando ejecutado (24/11/2026 — Eclipse Temurin JDK 17.0.17 / Maven 3.9.16):<br />
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test -Djava.version=17 \<br />
-Dtest="SeguridadIntegrationTest,SolicitudServiceTest\$Registro#restriccionesDePropiedad" -B<br />
<br />
# Resultado real capturado (04_Tratamientos_y_Pruebas_Controles/control_1_rbac/ejecucion_control_1.log):<br />
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 -- SeguridadIntegrationTest (SEG-01..SEG-10)<br />
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- SolicitudServiceTest$Registro (PU-21)<br />
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0 --&gt; BUILD SUCCESS</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

Evidencias confirmadas: SEG-01 valida hash BCrypt (\$2a\$); SEG-03 rechaza con HTTP 403 peticiones POST sin token CSRF; SEG-04 y SEG-05 bloquean con HTTP 401/403 accesos no autenticados o con rol cruzado tanto en Web MVC como en API REST; SEG-06 y PU-21 impiden que el productor 'lmamani' acceda a solicitudes de 'pquispe' (HTTP 403 / OperacionNoPermitidaException).

**7.2 Tratamiento T2 — Validación estricta de cantidades, disponibilidad e integridad de inventario**

Mitiga los escenarios ESC-02 (manipulación o error en cantidades de solicitudes/devoluciones) y ESC-06 (condición de carrera en entregas). Está implementado mediante Bean Validation (@Valid), reglas defensivas en InventarioService, SolicitudService, PrestamoService y DevolucionService, bloqueo pesimista @Lock(LockModeType.PESSIMISTIC_WRITE) en repositorios, invariante estaCuadrado() y restricciones CHECK en PostgreSQL (ck_inventario_cuadre, ck_prestamo_devuelto, UNIQUE(solicitud_id)).

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th># Comando ejecutado (24/11/2026 — Eclipse Temurin JDK 17.0.17 / Maven 3.9.16):<br />
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test -Djava.version=17 \<br />
-Dtest="InventarioServiceTest*,SolicitudServiceTest*,PrestamoServiceTest,DevolucionServiceTest,\<br />
ProcesosNegocioIntegrationTest#validacionNoModificaDatos,SeguridadIntegrationTest#validacionDeEntradas+validacionWeb" -B<br />
<br />
# Resultado real capturado (04_Tratamientos_y_Pruebas_Controles/control_2_validacion/ejecucion_control_2.log):<br />
[INFO] InventarioServiceTest (17) + SolicitudServiceTest (21) + PrestamoServiceTest (6)<br />
+ DevolucionServiceTest (13) + IT-02 (1) + SEG-08/SEG-10 (2)<br />
[INFO] Tests run: 60, Failures: 0, Errors: 0, Skipped: 0 --&gt; BUILD SUCCESS</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

Evidencias confirmadas: cantidades nulas, cero o negativas (PU-04, PU-17, PU-41, SEG-08, SEG-10) son rechazadas con HTTP 400 / ValidacionException sin alterar la base de datos (IT-02); cantidades mayores al stock disponible o devoluciones superiores a lo pendiente (PU-05, PU-08, PU-10, PU-19, PU-30, PU-38) son bloqueadas con HTTP 409 / ReglaNegocioException manteniendo la igualdad total = disponible + prestada + dañada + mantenimiento.

**8. Trazabilidad de Riesgos (G06) con Requisitos y Pruebas (G07)**

| **Escenario** | **Inherente** | **Residual** | **RF / RNF (15/10)** | **Pruebas G07 vinculadas** | **Estado** |
|:--:|:--:|:--:|:---|:---|:--:|
| **ESC-01** | **CRÍTICO** | **BAJO** | RF01, RF02, RNF02, RNF03 | SEG-02, SEG-04, SEG-05, PU-21 | **VERIFICADO EN VERDE** |
| **ESC-02** | **CRÍTICO** | **BAJO** | RF05, RF06, RF07, RF11, RF13, RNF05, RNF06 | PU-04, PU-05, PU-08, PU-10, PU-13, PU-15, PU-17, PU-19, PU-38, PU-41, IT-02, SEG-08, SEG-10 | **VERIFICADO EN VERDE** |
| **ESC-03** | **CRÍTICO** | **BAJO** | RF01, RNF02, RNF03 | SEG-01 (BCrypt verificado); fuerza bruta / DEMO_DATA=false -\> planificado | PARCIAL (BCrypt OK) |
| **ESC-04** | **CRÍTICO** | **BAJO** | RF02, RF08, RF12, RNF02, RNF03 | SEG-06, PU-21 | **VERIFICADO EN VERDE** |
| **ESC-05** | **ALTO** | **BAJO** | RF01, RF02, RNF02 | SEG-03 | **VERIFICADO EN VERDE** |
| **ESC-06** | **ALTO** | **BAJO** | RF09, RF11, RNF05, RNF06 | PU-28, PU-29, PU-30, PU-31, IT-01 | **VERIFICADO EN VERDE** |
| **ESC-07** | **ALTO** | **BAJO** | RNF04, RNF10 | Pendiente prueba de restauración en entorno PostgreSQL (G08 / Infra) | PLANIFICADO |
| **ESC-08** | **ALTO** | **BAJO** | RNF02, RNF03 | Verificación de configuración + plan JWT v2 | PARCIAL |
| **ESC-09** | **ALTO** | **BAJO** | RNF01, RNF08 | Cobertura en G08 (JMeter escenarios A–D) | DELEGADO A G08 |
| **ESC-10** | **MEDIO** | **BAJO** | RF14, RF15, RNF07 | PU-07, PU-09, PU-11, PU-16, PU-24, IT-03, SEG-09 | **VERIFICADO EN VERDE** |

**9. Análisis de Resultados, Limitaciones, Conclusiones y Participación**

**9.1 Análisis y limitaciones**

- **Reducción efectiva del riesgo:** Las salvaguardas ya codificadas en AgroRetorno v1.0.0 cubren 6 de los 10 escenarios con verificación automatizada en verde, reduciendo el riesgo promedio global en 88.6% (de 13.55 a 1.54).

- **Brechas de seguridad pendientes para despliegue productivo:** En DatosDemoInitializer.java, la propiedad agroretorno.datos-demo.habilitado mantiene valor por defecto 'true' con contraseñas fijas ('Encargado2026!', 'Productor2026!'). Asimismo, /api/\*\* usa HTTP Basic sin rate-limiting y application.properties incluye contraseña PostgreSQL de respaldo ('agroretorno'). Antes de pasar a producción real debe forzarse DEMO_DATA=false, rotar secretos por variables de entorno y añadir límite de intentos fallidos.

- **Limitación metodológica de herramientas:** Al no contar con licencia comercial de PILAR en Linux, la valoración se implementó en libro Excel abierto con fórmulas MAGERIT v3 verificables. Además, la valoración es semicuantitativa (0–25) y no monetaria pura, dado que AgroRetorno es un sistema nuevo sin histórico contable de incidentes de seguridad.

- **Alcance del entorno de pruebas de controles:** Las pruebas de los controles T1 y T2 se ejecutaron con Spring Boot + H2 en modo PostgreSQL (perfil test). Los controles de respaldo PostgreSQL (ESC-07) y carga/disponibilidad (ESC-09) se articulan con la guía G08.

**9.2 Conclusiones**

- **1.** MAGERIT v3 permitió estructurar de forma trazable los 8 activos críticos y 10 escenarios de amenaza de AgroRetorno, priorizando objetivamente integridad de inventario y control de acceso.

- **2.** La comprobación empírica mediante 71 ejecuciones reales de prueba (11 para T1 y 60 para T2) demostró que ambos controles críticos operan efectivamente tanto en la capa HTTP como en la lógica de negocio.

- **3.** El libro Excel editable (220 fórmulas, 0 errores) garantiza auditabilidad total y permite recalcular escenarios ante cambios futuros de configuración o nuevas amenazas.

**9.3 Registro de participación real y resumen para sustentación (Pasos 5.7 y 5.8)**

Participación de Marco Antonio Marquez Herrera (100% en G06): inspección de código fuente (SecurityConfig, servicios, repositorios, V1\_\_esquema_inicial.sql y application.properties), definición de activos y escenarios MAGERIT v3, construcción y verificación de fórmulas del libro Excel editable, ejecución real de pruebas Maven/JUnit para los controles T1 y T2, elaboración de gráficos y redacción del informe. Pendiente de integración final: validación docente de la alternativa a PILAR, consolidación con la matriz de trazabilidad general (Ower — G01) y verificación de escenarios ESC-07/ESC-09 junto con los reportes JMeter de Mauricio (G08).

**10. Cuestionario de la Guía LAB06 y Bibliografía**

a\) ¿Qué atributos de riesgo se deben conocer? Para caracterizar un riesgo de manera rigurosa bajo MAGERIT v3 e ISO 27005 se deben conocer: el activo afectado y su valor por dimensiones de seguridad (C, I, D, A, T), las dependencias entre activos, la amenaza (origen y código), la vulnerabilidad explotada, la degradación potencial (%), el impacto resultante (acumulado y repercutido), la frecuencia o probabilidad de ocurrencia, el riesgo inherente, las salvaguardas existentes/planificadas con su eficacia y madurez, el propietario del riesgo, el riesgo residual y el criterio de aceptación.

b\) ¿Cuál sería el protocolo para mitigar riesgos? Un protocolo estructurado sigue seis pasos cíclicos: (1) identificar y valorar activos y amenazas; (2) calcular y priorizar el riesgo inherente frente al umbral de aceptación; (3) seleccionar la opción de tratamiento (mitigar, evitar, transferir o aceptar); (4) diseñar e implementar salvaguardas preventivas, detectivas o correctivas asignando responsable, plazo, costo y KPI; (5) verificar empíricamente la eficacia del control mediante pruebas reales (como T1 y T2 en AgroRetorno); y (6) monitorear continuamente el riesgo residual y reevaluar ante cambios del sistema.

c\) ¿Para qué sirve la metodología MAGERIT y cuáles son sus limitaciones? MAGERIT sirve para proporcionar un método sistemático y estándar para analizar los riesgos derivados del uso de tecnologías de información, apoyar a los órganos de gobierno en la toma de decisiones sobre inversión en salvaguardas y demostrar cumplimiento normativo (ENS, ISO 27001/27005). Sus principales limitaciones son: alta exigencia de esfuerzo inicial para inventariar dependencias detalladas en sistemas medianos o cambiantes, subjetividad en estimaciones de frecuencia cuando no existen históricos de incidentes, curva de aprendizaje elevada de sus catálogos y dependencia de herramientas licenciadas (PILAR) para automatizar la propagación multinivel.

**Bibliografía verificada**

- **\[1\]** Ministerio de Hacienda y Administraciones Públicas (España), «MAGERIT — Versión 3.0. Metodología de Análisis y Gestión de Riesgos de los Sistemas de Información. Libro I: Método», Portal de Administración Electrónica (PAe) / CCN-CERT, 2012. Disponible en: https://www.ccn-cert.cni.es/documentos-publicos/1789-magerit-libro-i-metodo/file.html

- **\[2\]** Ministerio de Hacienda y Administraciones Públicas (España), «MAGERIT — Versión 3.0. Libro II: Catálogo de Elementos y Libro III: Guía de Técnicas», PAe, 2012.

- **\[3\]** International Organization for Standardization, «ISO 31000:2018 Risk management — Guidelines» e «ISO/IEC 27005:2022 Information security, cybersecurity and privacy protection — Guidance on managing information security risks», Ginebra, Suiza.

- **\[4\]** J. M. Silva Fernández, «Guía de Laboratorio 06: Gestión de Riesgos», Escuela Profesional de Ingeniería de Sistemas, Universidad Nacional de San Agustín (UNSA), Calidad de Software, 2026-B.
