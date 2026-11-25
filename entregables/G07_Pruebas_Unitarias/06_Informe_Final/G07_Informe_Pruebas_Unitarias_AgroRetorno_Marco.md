**UNIVERSIDAD NACIONAL DE SAN AGUSTÍN DE AREQUIPA  
FACULTAD DE INGENIERÍA DE PRODUCCIÓN Y SERVICIOS  
ESCUELA PROFESIONAL DE INGENIERÍA DE SISTEMAS  **

**PROYECTO AGRORETORNO v1.0.0  
INFORME DE PRUEBAS UNITARIAS Y COBERTURA  
(GUÍA 07 — JUNIT 5, MOCKITO Y JACOCO)  **
*Ejecución reproducible en Maven, catálogo de 43 pruebas unitarias (72 ejecuciones totales),  
verificación de cobertura JaCoCo \>= 80 %, experimento de defecto controlado y trazabilidad con G06*

| **Atributo del Documento** | **Detalle** |
|:---|:---|
| **Asignatura** | Calidad de Software — Semestre 8 (2026-B) |
| **Docente** | M. en C. Jesús Martín Silva Fernández |
| **Integrante responsable (G06 y G07)** | Marco Antonio Marquez Herrera (mmarquezhe@unsa.edu.pe) |
| **Sistema evaluado** | AgroRetorno v1.0.0 (commit base a28b7e4 · Spring Boot 3.5.16) |
| **Herramientas y versiones** | Maven Wrapper 3.9.16 · Eclipse Temurin JDK 17.0.17 · JUnit 5 (Jupiter) · Mockito · AssertJ 3.27.7 · Surefire 3.5.6 · JaCoCo 0.8.14 |
| **Línea base de requisitos** | 15 RF (RF01–RF15) y 10 RNF (RNF01–RNF10) alineados con G03/G04 |
| **Rama Git de entrega** | feature/marco-g07-pruebas-unitarias |
| **Catálogo editable principal** | 02_Catalogo_Pruebas/G07_Catalogo_Pruebas_Unitarias_AgroRetorno.xlsx (110 fórmulas, 0 errores) |
| **Fecha de ejecución verificada** | 24 de noviembre de 2026 |

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th><strong>Resumen Ejecutivo de Resultados (G07)<br />
</strong>Se ejecutó la suite automatizada completa mediante './mvnw clean verify' obteniendo 72/72 ejecuciones JUnit 5 aprobadas (0 fallos, 0 errores, 0 omitidas) en 24.645 s. Se catalogaron 43 casos unitarios (PU-01 a PU-43; 57 ejecuciones parametrizadas) sobre 4 servicios de negocio (InventarioService, SolicitudService, PrestamoService y DevolucionService) aislados con Mockito y reloj determinista, más 15 pruebas de integración y seguridad (IT-01..IT-05, SEG-01..SEG-10). JaCoCo 0.8.14 midió 97.6 % de cobertura de líneas (496/508) y 81.7 % de ramas en pe.edu.agroretorno.service, superando la meta obligatoria del 80 % y aprobando 'jacoco:check'. El experimento de defecto controlado en DevolucionService.java:75 confirmó la eficacia de la suite: 3 pruebas fallaron al introducir la mutación y las 13/13 volvieron a pasar inmediatamente tras restaurar la regla original.</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

**1. Objetivo, Entorno de Ejecución y Comando Reproducible (Paso 3.9)**

Evaluar, ejecutar y verificar la batería de pruebas unitarias de la lógica de negocio de AgroRetorno v1.0.0 con JUnit 5, Mockito y JaCoCo sobre Maven, garantizando reproducibilidad, cobertura mínima del 80 % de líneas en lógica de negocio, sensibilidad ante defectos controlados y trazabilidad directa hacia los requisitos (RF/RNF) y riesgos de seguridad e integridad identificados en la Guía 06.

Nota sobre la línea base del repositorio (Paso 5.1 y 5.6): el commit a28b7e4 de la rama main incluía la carpeta src/ completa pero omitía los archivos de compilación Maven. En esta rama se incorporaron únicamente pom.xml, mvnw, mvnw.cmd, .mvn/ y .gitignore exactos de AgroRetorno-v1.0.0.zip sin modificar una sola línea de lógica de negocio en src/. En el equipo Linux (Nobara 44, kernel 7.2.11) el compilador javac instalado corresponde a Eclipse Temurin JDK 17.0.17, por lo que la compilación y pruebas se ejecutaron indicando JAVA_HOME y -Djava.version=17 (plenamente compatible con Spring Boot 3.5.16 y JaCoCo 0.8.14):

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th># Entorno: Linux x86_64 | Maven Wrapper 3.9.16 | Eclipse Temurin JDK 17.0.17 | Fecha: 2026-11-24T19:28:36-05:00<br />
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw clean verify -Djava.version=17 -B<br />
<br />
# Salida resumida verificada (01_Ejecucion_Maven/01_mvn_clean_verify.log):<br />
[INFO] Tests run: 72, Failures: 0, Errors: 0, Skipped: 0<br />
[INFO] --- jacoco:0.8.14:check (verificar-cobertura) @ agroretorno ---<br />
[INFO] Analyzed bundle 'agroretorno' with 72 classes<br />
[INFO] All coverage checks have been met.<br />
[INFO] BUILD SUCCESS | Total time: 24.645 s</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

| **Clase de Prueba** | **Tipo** | **Ejec.** | **PASS** | **Fallos** | **Errores** | **Tiempo** |
|:---|:---|:--:|:--:|:--:|:--:|:--:|
| **ProcesosNegocioIntegrationTest** | Integración (SpringBoot + H2) | 5 | 5 | 0 | 0 | 2.608 s |
| **SeguridadIntegrationTest** | Integración (SpringBoot + H2) | 10 | 10 | 0 | 0 | 8.883 s |
| **DevolucionServiceTest** | Unitaria (Mockito) | 13 | 13 | 0 | 0 | 0.299 s |
| **InventarioServiceTest** | Unitaria (Mockito) | 17 | 17 | 0 | 0 | 0.821 s |
| **PrestamoServiceTest** | Unitaria (Mockito) | 6 | 6 | 0 | 0 | 0.021 s |
| **SolicitudServiceTest** | Unitaria (Mockito) | 21 | 21 | 0 | 0 | 0.285 s |
| **TOTAL SUITE AGRORETORNO** | **None** | **72** | **72** | **0** | **0** | **12.917 s** |

**2. Componentes de Negocio y Uso de Dobles de Prueba (Pasos 3.10 y 3.13)**

Se verificaron cuatro componentes críticos de lógica de negocio (superando el mínimo de tres exigido) en el paquete pe.edu.agroretorno.service, diseñados para ejecutarse de forma determinista y sin dependencia de PostgreSQL ni del orden de ejecución (maven-surefire-plugin configurado con \<runOrder\>random\</runOrder\>):

- **Componente 1 — InventarioService (InventarioServiceTest: PU-01 a PU-15, 17 ejecuciones):** Aislado mediante @ExtendWith(MockitoExtension.class) y @Mock para InventarioRepository, MaterialRepository, MovimientoInventarioRepository, UsuarioService e HistorialService. Emplea ArgumentCaptor\<MovimientoInventario\> y verify(..., never()) para asegurar que ningún error persista cambios indebidos.

- **Componente 2A — SolicitudService (SolicitudServiceTest: PU-16 a PU-27, 21 ejecuciones):** Usa dobles Mockito para 7 dependencias e inyecta un InventarioService real (respaldado por mocks) junto con Fixtures.RELOJ fijo (17/11/2026 10:00 America/Lima) para probar límites exactos de fechas (HOY+1 y HOY+90).

- **Componente 2B — PrestamoService (PrestamoServiceTest: PU-28 a PU-33, 6 ejecuciones):** Aísla 8 dependencias vía Mockito y verifica la revalidación de existencias en entregas, prohibición de entregas sin aprobación o duplicadas y cálculo de préstamos vencidos contra el reloj determinista.

- **Componente 3 — DevolucionService (DevolucionServiceTest: PU-34 a PU-43, 13 ejecuciones):** Aísla 8 dependencias con Mockito y valida devoluciones totales, parciales acumuladas, clasificación por estado (buen estado, dañadas, mantenimiento), cierre automático del préstamo y rechazo de excesos o préstamos cerrados.

- **Pruebas complementarias de integración y seguridad (IT-01..IT-05, SEG-01..SEG-10 — 15 ejecuciones):** Ejecutadas con @SpringBootTest y perfil 'test' (H2 en memoria en modo PostgreSQL), verificando transacciones completas de los 3 procesos y la capa real Spring Security / MockMvc.

**3. Catálogo de Pruebas Unitarias y Cobertura de Casos (Pasos 3.11 y 3.12)**

El catálogo completo abarca 43 identificadores unitarios (PU-01 a PU-43, equivalentes a 57 ejecuciones gracias a pruebas parametrizadas @ValueSource, @MethodSource y @CsvSource), más 15 pruebas de integración y seguridad (total 72 ejecuciones). Todos los casos cubren explícitamente las cuatro categorías exigidas: comportamientos válidos (17 casos), entradas inválidas (13 casos), valores límite (7 casos: stock exacto 50 vs 51, fechas HOY+1/HOY+90, devolución exacta 20 vs 21, cantidad 0) y excepciones controladas (RecursoNoEncontradoException, ValidacionException, ReglaNegocioException y OperacionNoPermitidaException). El catálogo editable completo figura en 02_Catalogo_Pruebas/G07_Catalogo_Pruebas_Unitarias_AgroRetorno.xlsx.

<img src="media/image1.png" style="width:6.35in;height:2.90462in" />

*Figura 1. Distribución de las 72 ejecuciones JUnit 5 entre pruebas unitarias con Mockito (57) e integración/seguridad (15).*

| **ID** | **Comp.** | **Req. (15/10)** | **Tipo** | **Entradas / Escenario** | **Resultado Esperado** | **Res.** | **G06** |
|:--:|:--:|:--:|:--:|:---|:---|:--:|:--:|
| **PU-01** | Inventario | RF06 | Válido | materialId=1, cantidad=10 | disponible=true, cantidadDisponible=50, cantidadSolicitada=10 | **PASS** | ESC-02 |
| **PU-02** | Inventario | RF06 | Excepción | materialId=99, cantidad=1 | Lanza RecursoNoEncontradoException con '99' | **PASS** | ESC-02 |
| **PU-03** | Inventario | RF06 | Inválido | materialId=2 (INACTIVO), cantidad=1 | disponible=false, mensaje contiene 'inactivo' | **PASS** | ESC-02 |
| **PU-04** | Inventario | RNF05 | Inválido / Límite | cantidad ∈ {0, -1, -20} (@ValueSource) | Lanza ValidacionException ('entero positivo') en los 3 casos | **PASS** | ESC-02 |
| **PU-05** | Inventario | RF07 / RNF05 | Valor límite | cantidad=50 (válido) y cantidad=51 (límite+1) | 50 pasa sin excepción; 51 lanza ReglaNegocioException | **PASS** | ESC-02 |
| **PU-06** | Inventario | RF06 | Válido | texto=null, soloActivos=true / false | soloActivos=true devuelve 1; soloActivos=false devuelve 2 | **PASS** | ESC-02 |
| **PU-07** | Inventario | RF11 / RF14 | Válido | materialId=1, cantidad=20, ref='SOLICITUD-1' | disp=30, prest=20, total=50, estaCuadrado()=true, movimiento ENTREGA guardado | **PASS** | ESC-02, ESC-10 |
| **PU-08** | Inventario | RF11 / RNF06 | Inválido / Excepción | materialId=1, cantidad=51 (\>50 disp) | Lanza ReglaNegocioException; inventario sin cambios y 0 saves | **PASS** | ESC-02, ESC-06 |
| **PU-09** | Inventario | RF13 / RF14 | Válido | buen=5, danadas=2, mant=3, ref='PRESTAMO-1' | disp=35, prest=10, dan=2, mant=3, total=50, 3 movimientos guardados | **PASS** | ESC-02, ESC-10 |
| **PU-10** | Inventario | RF13 / RNF06 | Inválido / Excepción | buen=1, dan=0, mant=0 con prestadas=0 | Lanza ReglaNegocioException y nunca persiste inventario | **PASS** | ESC-02 |
| **PU-11** | Inventario | RF05 / RF14 | Válido | tipo=INGRESO, cant=10, obs='Compra' | total=60, disp=60 y registro en HistorialService | **PASS** | ESC-10 |
| **PU-12** | Inventario | RF05 | Válido | ENVIO_MANTENIMIENTO(4) luego REPARACION(3) | disp=49, mant=1, total=50 (invariante conservado) | **PASS** | ESC-02 |
| **PU-13** | Inventario | RF05 / RNF05 | Inválido / Excepción | RETIRO(51), BAJA_DANADO(1 con 0 dañ.), ENTREGA(1) | ReglaNegocioException / ValidacionException; total permanece en 50 | **PASS** | ESC-02 |
| **PU-14** | Inventario | RF05 | Válido | RETIRO(5) + BAJA_DANADO(5) | total=40, disp=40, dan=0, estaCuadrado()=true | **PASS** | ESC-02 |
| **PU-15** | Inventario | RF04 / RNF05 | Válido + Inválido | cantidadInicial=12 (válido) y -1 (inválido) | 12 crea inventario + movimiento; -1 lanza ValidacionException | **PASS** | ESC-02 |
| **PU-16** | Solicitud | RF07 / RF14 | Válido | materialId=1, cant=20, fecha=HOY+15, motivo válido | Estado PENDIENTE, id=500, historial registrado, stock sin descontar (50) | **PASS** | ESC-10 |
| **PU-17** | Solicitud | RF07 / RNF05 | Inválido / Límite | 10 combinaciones inválidas (@MethodSource: null, 0, -3, fecha=HOY, pasada, \>90d, motivo corto/vacío) | Lanza ValidacionException y verify(solicitudRepository, never()).save() | **PASS** | ESC-02 |
| **PU-18** | Solicitud | RF07 | Valor límite | fecha = HOY+1 (límite inf.) y HOY+90 (límite sup.) | Ambas solicitudes se aceptan y se persisten (times(2)) | **PASS** | ESC-02 |
| **PU-19** | Solicitud | RF07 / RNF05 | Valor límite / Inválido | cantidad=51 con stock disponible=50 | Lanza ReglaNegocioException ('solo hay 50') sin persistir | **PASS** | ESC-02 |
| **PU-20** | Solicitud | RF07 | Excepción | materialId=2 (INACTIVO) y materialId=9 (inexistente) | ReglaNegocioException ('inactivo') y RecursoNoEncontradoException | **PASS** | ESC-02 |
| **PU-21** | Solicitud | RF02 / RF08 / RNF02 | Seguridad / Excepción | encargado intenta registrar; pquispe lee solicitud de lmamani | OperacionNoPermitidaException en ambos casos; lmamani sí accede a \#7 | **PASS** | ESC-01, ESC-04 |
| **PU-22** | Solicitud | RF09 | Válido | solicitudId=10 (PENDIENTE, cant=20), encargado | Estado APROBADA, respondidoPor=encargado, inventario NO se descuenta | **PASS** | ESC-06 |
| **PU-23** | Solicitud | RF09 | Inválido / Excepción | solicitudId=10 (cant=20) con disponible reducido a 10 | Lanza ReglaNegocioException y estado sigue PENDIENTE | **PASS** | ESC-02 |
| **PU-24** | Solicitud | RF10 / RF14 | Válido | solicitudId=10, motivo=' Excede lo asignado ' | Estado RECHAZADA, motivo recortado (trim), historial registrado | **PASS** | ESC-10 |
| **PU-25** | Solicitud | RF09 | Excepción | aprobar solicitud ya en estado RECHAZADA | Lanza ReglaNegocioException ('RECHAZADA') y estado no cambia | **PASS** | ESC-02 |
| **PU-26** | Solicitud | RF10 / RNF05 | Inválido / Excepción | rechazar con motivo='' y rechazar solicitud ENTREGADA | ValidacionException por motivo vacío; ReglaNegocioException por ENTREGADA | **PASS** | ESC-02 |
| **PU-27** | Solicitud | RF08 / RF09 | Válido + Excepción | listar(PENDIENTE/null), listarPorProductor, obtener(10 y 77) | Resultados esperados y RecursoNoEncontradoException para id=77 | **PASS** | ESC-04 |
| **PU-28** | Prestamo | RF11 | Válido | solicitudId=10 (APROBADA, cant=20), encargado | Préstamo ACTIVO id=900, solicitud ENTREGADA, disp=30, prest=20 | **PASS** | ESC-06 |
| **PU-29** | Prestamo | RF11 | Inválido / Excepción | solicitudId=10 en estado PENDIENTE | ReglaNegocioException ('sin aprobación'), 0 saves, disp sigue 50 | **PASS** | ESC-06 |
| **PU-30** | Prestamo | RF11 / RNF06 | Inválido / Concurrencia | solicitudId=10 (cant=20) pero stock bajó a 15 | ReglaNegocioException ('insuficientes'), solicitud sigue APROBADA | **PASS** | ESC-02, ESC-06 |
| **PU-31** | Prestamo | RF11 / RNF06 | Inválido / Excepción | entrega duplicada para solicitudId=10 | ReglaNegocioException ('ya fue entregada'), sin tocar inventario | **PASS** | ESC-06 |
| **PU-32** | Prestamo | RF11 | Excepción | material INACTIVO y solicitud inexistente id=99 | ReglaNegocioException y RecursoNoEncontradoException | **PASS** | ESC-02 |
| **PU-33** | Prestamo | RF12 | Válido + Excepción | listar activos/vencidos, por productor, id inexistente, rol no productor | Detecta vencido=true, valida acceso productor y lanza excepciones esperadas | **PASS** | ESC-04 |
| **PU-34** | Devolucion | RF13 | Válido / Límite | buen=20, danadas=0, mant=0 (devolución total exacta) | Préstamo CERRADO, pendiente=0, fechaCierre=AHORA, disp=50, prest=0 | **PASS** | ESC-02 |
| **PU-35** | Devolucion | RF13 | Válido | buen=8, danadas=0, mant=0 (devolución parcial) | Préstamo sigue ACTIVO, pendiente=12, disp=38, prest=12 | **PASS** | ESC-02 |
| **PU-36** | Devolucion | RF13 | Válido | buen=0, danadas=3, mant=0, obs='Base rota' | danada=3, disp sigue 30 (no disponible), cuadre=true | **PASS** | ESC-02 |
| **PU-37** | Devolucion | RF13 | Válido | buen=0, danadas=0, mant=4, obs='Requieren limpieza' | mantenimiento=4, disp sigue 30 (no disponible) | **PASS** | ESC-02 |
| **PU-38** | Devolucion | RF13 / RNF05 | Valor límite / Inválido | buen=15, danadas=6 (total=21 \> 20 pendientes) | ReglaNegocioException ('supera las unidades pendientes (20)'), 0 saves | **PASS** | ESC-02 |
| **PU-39** | Devolucion | RF13 | Válido / Límite | 3 parciales acumuladas: (5+2) + 8 + 5 = 20 | Al completar 20 -\> CERRADO, disp=48, dan=2, prest=0, total=50 | **PASS** | ESC-02 |
| **PU-40** | Devolucion | RF13 | Excepción | devolver(1,0,0) tras haber cerrado el préstamo | ReglaNegocioException ('ya está cerrado') | **PASS** | ESC-02 |
| **PU-41** | Devolucion | RNF05 | Inválido / Límite | (0,0,0), (-1,0,0), (0,-2,0), (3,0,-1) (@CsvSource) | ValidacionException y sin consultar préstamo | **PASS** | ESC-02 |
| **PU-42** | Devolucion | RF13 / RNF05 | Inválido / Excepción | dañada/mant sin observaciones + préstamo id=1 inexistente | ValidacionException y RecursoNoEncontradoException | **PASS** | ESC-02 |
| **PU-43** | Devolucion | RF12 / RF14 | Válido + Excepción | listarPorPrestamo(900), listarPorProductor('pquispe'/'nadie') | Retorna devoluciones esperadas y OperacionNoPermitidaException para 'nadie' | **PASS** | ESC-04, ESC-10 |

**4. Reporte de Cobertura JaCoCo y Exclusiones Documentadas (Paso 3.14)**

La ejecución './mvnw clean verify' generó los reportes originales JaCoCo 0.8.14 en formato HTML, XML y CSV (conservados en 03_Reportes_Originales/jacoco/). La regla automática jacoco:check en pom.xml exige un mínimo del 80.0 % de cobertura de líneas sobre el paquete de lógica de negocio pe.edu.agroretorno.service. El resultado real obtenido fue 97.6 % de líneas (496 de 508) y 81.7 % de ramas (201 de 246), superando en +17.6 puntos porcentuales el umbral exigido.

<img src="media/image2.png" style="width:6.45in;height:3.25608in" />

*Figura 2. Cobertura real de líneas y ramas medida por JaCoCo 0.8.14 para las 9 clases de pe.edu.agroretorno.service.*

**4.1 Desglose por clase en pe.edu.agroretorno.service (Meta \>= 80 %)**

| **Clase de Servicio** | **Líneas Cub.** | **% Líneas** | **Ramas Cub.** | **% Ramas** | **Métodos** | **Evaluación** |
|:---|:--:|:--:|:--:|:--:|:--:|:--:|
| **DevolucionService** | 54/54 | 100.0% | 27/28 | 96.4% | 7/7 | **CUMPLE** |
| **HistorialService** | 24/24 | 100.0% | 14/18 | 77.8% | 6/6 | **CUMPLE** |
| **InventarioService** | 121/126 | 96.0% | 52/68 | 76.5% | 18/20 | **CUMPLE** |
| **MaterialService** | 37/37 | 100.0% | 9/12 | 75.0% | 5/7 | **CUMPLE** |
| **PrestamoService** | 43/43 | 100.0% | 9/10 | 90.0% | 13/13 | **CUMPLE** |
| **ProductorService** | 51/57 | 89.5% | 17/32 | 53.1% | 7/11 | **CUMPLE** |
| **ReporteService** | 79/79 | 100.0% | 47/48 | 97.9% | 17/17 | **CUMPLE** |
| **SolicitudService** | 82/83 | 98.8% | 26/30 | 86.7% | 14/17 | **CUMPLE** |
| **UsuarioService** | 5/5 | 100.0% | 0/0 | N/A | 2/3 | **CUMPLE** |
| **TOTAL pe.edu.agroretorno.service** | **496/508** | **97.6%** | **201/246** | **81.7%** | **89/101** | **CUMPLE META \>=80%** |

**4.2 Cobertura por paquete y política de exclusiones**

| **Paquete Java** | **Líneas Cub.** | **% Líneas** | **Ramas Cub.** | **Alcance / Justificación** |
|:---|:--:|:--:|:--:|:---|
| **pe.edu.agroretorno** | 1/3 | 33.3% | 0/0 | Informativo / Soporte |
| **pe.edu.agroretorno.api** | 45/53 | 84.9% | 0/0 | Informativo / Soporte |
| **pe.edu.agroretorno.config** | 101/103 | 98.1% | 1/2 | Informativo / Soporte |
| **pe.edu.agroretorno.controller** | 61/265 | 23.0% | 2/44 | Cubierto por Selenium (G09) |
| **pe.edu.agroretorno.dto** | 152/165 | 92.1% | 12/20 | Informativo / Soporte |
| **pe.edu.agroretorno.entity** | 262/281 | 93.2% | 12/18 | Informativo / Soporte |
| **pe.edu.agroretorno.exception** | 10/10 | 100.0% | 0/0 | Informativo / Soporte |
| **pe.edu.agroretorno.security** | 56/56 | 100.0% | 2/4 | Informativo / Soporte |
| **pe.edu.agroretorno.service** | 496/508 | 97.6% | 201/246 | META OBLIGATORIA (\>=80%) |
| **TOTAL PROYECTO (TODOS LOS PAQUETES)** | **1184/1444** | **82.0%** | **230/334** | **72 clases analizadas** |

Exclusiones documentadas: el reporte HTML/CSV incluye los 9 paquetes (72 clases) sin ocultar código de negocio. La regla de umbral 80 % se aplica exclusivamente sobre pe.edu.agroretorno.service, excluyendo de la meta: (1) pe.edu.agroretorno.controller (23.0 % en JUnit), cuyas vistas Thymeleaf y flujos de formularios corresponden a pruebas funcionales automatizadas con Selenium en G09; y (2) AgroRetornoApplication.main() (33.3 %), punto de arranque de Spring Boot. Las 12 líneas no cubiertas en service corresponden a ramas defensivas de guardia (p. ej. IllegalStateException en InventarioService.guardar si falla el cuadre interno y validaciones secundarias de ProductorService).

**5. Experimento de Defecto Controlado y Restauración (Pasos 3.15 y 3.16)**

Para comprobar que las pruebas unitarias realmente detectan regresiones en las reglas críticas del negocio (y no aprueban por falsos positivos), se inyectó temporalmente un defecto de límite («off-by-one») en DevolucionService.java línea 75, cambiando '\>' por '\>=' para provocar el rechazo indebido de devoluciones exactas iguales al saldo pendiente:

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th>diff --git a/src/main/java/pe/edu/agroretorno/service/DevolucionService.java b/src/main/java/pe/edu/agroretorno/service/DevolucionService.java<br />
index f82754e..f243396 100644<br />
--- a/src/main/java/pe/edu/agroretorno/service/DevolucionService.java<br />
+++ b/src/main/java/pe/edu/agroretorno/service/DevolucionService.java<br />
@@ -72,7 +72,7 @@ public class DevolucionService {<br />
if (prestamo.getEstado() == EstadoPrestamo.CERRADO) {<br />
throw new ReglaNegocioException("El préstamo " + prestamoId + " ya está cerrado");<br />
}<br />
- if (total &gt; prestamo.getCantidadPendiente()) {<br />
+ if (total &gt;= prestamo.getCantidadPendiente()) {<br />
throw new ReglaNegocioException("La cantidad devuelta (" + total + ") supera las unidades pendientes ("<br />
+ prestamo.getCantidadPendiente() + ")");<br />
}</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 100%" />
</colgroup>
<thead>
<tr>
<th># Ejecución CON defecto inyectado (04_Defecto_Controlado/02_fallo/ejecucion_con_defecto.log):<br />
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test -Djava.version=17 -Dtest=DevolucionServiceTest -B<br />
<br />
[ERROR] DevolucionServiceTest.cierreTrasVariasParciales:150-&gt;devolver:82 » ReglaNegocio<br />
La cantidad devuelta (5) supera las unidades pendientes (5)<br />
[ERROR] DevolucionServiceTest.devolucionTotalBuenEstado:88-&gt;devolver:82 » ReglaNegocio<br />
La cantidad devuelta (20) supera las unidades pendientes (20)<br />
[ERROR] DevolucionServiceTest.prestamoCerrado:166-&gt;devolver:82 » ReglaNegocio<br />
La cantidad devuelta (20) supera las unidades pendientes (20)<br />
[ERROR] Tests run: 13, Failures: 0, Errors: 3, Skipped: 0 --&gt; BUILD FAILURE<br />
<br />
# Tras restaurar con 'git checkout -- src/main/java/pe/edu/agroretorno/service/DevolucionService.java' (0 líneas diff):<br />
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0 --&gt; BUILD SUCCESS</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

| **Fase** | **Regla en DevolucionService.java:75** | **Ejec.** | **Fallos** | **Pruebas afectadas** | **Estado** |
|:---|:---|:--:|:--:|:---|:--:|
| **1. Línea base (Antes)** | if (total \> prestamo.getCantidadPendiente()) | 13 | 0 | Ninguna (comportamiento correcto) | **VERDE (13/13)** |
| **2. Defecto inyectado (Paso 3.15)** | if (total \>= prestamo.getCantidadPendiente()) | 13 | 3 | PU-34 (devolucionTotalBuenEstado), PU-39 (cierreTrasVariasParciales), PU-40 (prestamoCerrado) | **FALLO DETECTADO (3/13)** |
| **3. Regla restaurada (Paso 3.16)** | if (total \> prestamo.getCantidadPendiente()) | 13 | 0 | Ninguna (regresión confirmada en verde) | **VERDE (13/13)** |

**6. Relación de las Pruebas Unitarias con los Riesgos de G06 (Paso 3.17)**

La suite G07 constituye la evidencia objetiva de verificación para los riesgos priorizados en G06, en particular los dos controles críticos seleccionados (T1 RBAC/propiedad/CSRF y T2 validación de cantidades e integridad de inventario):

| **Escenario G06** | **Inherente** | **Residual** | **RF / RNF** | **Pruebas G07 vinculadas** | **Estado** |
|:--:|:--:|:--:|:---|:---|:--:|
| **ESC-01** | CRÍTICO | BAJO | RF01, RF02, RNF02, RNF03 | SEG-02, SEG-04, SEG-05, PU-21 | **VERIFICADO EN VERDE** |
| **ESC-02** | CRÍTICO | BAJO | RF05, RF06, RF07, RF11, RF13, RNF05, RNF06 | PU-04, PU-05, PU-08, PU-10, PU-13, PU-15, PU-17, PU-19, PU-38, PU-41, IT-02, SEG-08, SEG-10 | **VERIFICADO EN VERDE** |
| **ESC-03** | CRÍTICO | BAJO | RF01, RNF02, RNF03 | SEG-01 (BCrypt verificado); fuerza bruta / DEMO_DATA=false -\> planificado | PARCIAL (BCrypt OK) |
| **ESC-04** | CRÍTICO | BAJO | RF02, RF08, RF12, RNF02, RNF03 | SEG-06, PU-21 | **VERIFICADO EN VERDE** |
| **ESC-05** | ALTO | BAJO | RF01, RF02, RNF02 | SEG-03 | **VERIFICADO EN VERDE** |
| **ESC-06** | ALTO | BAJO | RF09, RF11, RNF05, RNF06 | PU-28, PU-29, PU-30, PU-31, IT-01 | **VERIFICADO EN VERDE** |
| **ESC-07** | ALTO | BAJO | RNF04, RNF10 | Pendiente prueba de restauración en entorno PostgreSQL (G08 / Infra) | PLANIFICADO |
| **ESC-08** | ALTO | BAJO | RNF02, RNF03 | Verificación de configuración + plan JWT v2 | PARCIAL |
| **ESC-09** | ALTO | BAJO | RNF01, RNF08 | Cobertura en G08 (JMeter escenarios A–D) | DELEGADO A G08 |
| **ESC-10** | MEDIO | BAJO | RF14, RF15, RNF07 | PU-07, PU-09, PU-11, PU-16, PU-24, IT-03, SEG-09 | **VERIFICADO EN VERDE** |

**7. Análisis de Resultados, Defectos, Limitaciones y Conclusiones (Paso 3.18)**

**7.1 Análisis y observaciones**

- **Alta confiabilidad de la lógica de negocio:** Los tres procesos medulares (solicitud, aprobación/entrega y devolución/cierre) alcanzan 96.0 % a 100.0 % de cobertura de líneas en sus servicios y superan todas las aserciones con orden de ejecución aleatorio.

- **Eficacia verificada contra regresiones:** A diferencia del ejemplo histórico de LAB07 donde catch(AssertionError) silenciaba fallos en JUnit 4, las pruebas de AgroRetorno propagan excepciones reales a JUnit 5, provocando BUILD FAILURE inmediato ante el defecto inyectado en DevolucionService.

- **Observación menor sobre alineación de etiquetas y ramas:** Los textos @DisplayName de las pruebas referencian los IDs de la línea base original (RF01–RF10); en el catálogo Excel se documentó la equivalencia exacta hacia RF01–RF15. Además, ProductorService presenta 53.1 % de cobertura de ramas (89.5 % líneas), área recomendable para ampliar casos negativos en una futura versión.

- **Limitaciones:** Las pruebas unitarias validan lógica aislada con Mockito y las de integración emplean H2 en memoria (sin ejecutar el operador regex '~' de PostgreSQL de Flyway V1). Concurrencia real bajo carga, PostgreSQL físico y flujos visuales del navegador se cubren en G08 (JMeter) y G09 (Selenium). En este equipo Linux se compiló con Temurin JDK 17.0.17 (-Djava.version=17) debido a que solo el JRE 25 estaba instalado sin javac 21.

**7.2 Conclusiones y registro de participación (Pasos 5.7 y 5.8)**

- **1.** La suite automatizada de AgroRetorno v1.0.0 superó 72/72 ejecuciones sin errores en 24.645 s, acreditando el cumplimiento funcional y de reglas de negocio sobre los cuatro servicios evaluados.

- **2.** La cobertura de 97.6 % de líneas en pe.edu.agroretorno.service excede ampliamente el umbral del 80 % exigido por la guía y verificado automáticamente por JaCoCo 0.8.14.

- **3.** El ciclo completo de defecto controlado demostró objetivamente la capacidad de detección temprana de errores límite antes de llegar a producción.

Participación de Marco Antonio Marquez Herrera (100% en G07): incorporación de pom.xml/mvnw en rama de trabajo, ejecución completa de Maven/JUnit/JaCoCo, catalogación de los 43 casos unitarios y 15 de integración/seguridad en Excel con fórmulas verificadas, ejecución del experimento de defecto controlado y restauración, análisis de cobertura y elaboración del informe y resumen para sustentación.

**8. Cuestionario de la Guía LAB07 y Bibliografía**

a\) ¿Qué tipo de prueba se realiza con las pruebas unitarias y qué aspecto de calidad mide? Las pruebas unitarias son pruebas de caja blanca (estructurales) y funcionales de nivel de componente/unidad orientadas a verificar métodos y clases individuales en aislamiento (mediante dobles como Mockito). Miden principalmente la adecuación funcional (exactitud y completitud de reglas de negocio), la fiabilidad (manejo controlado de excepciones y valores límite) y la mantenibilidad (facilidad de prueba y detección temprana de regresiones mediante cobertura de líneas y ramas).

b\) ¿Las pruebas unitarias son obligatorias y por qué? En ingeniería de software profesional son una práctica obligatoria (y requisito de calidad en integración continua) porque reducen drásticamente el costo de corregir defectos al detectarlos en la fase más temprana posible, documentan el comportamiento esperado de cada regla de negocio, actúan como red de seguridad ante refactorizaciones y permiten medir objetivamente qué porción del código crítico ha sido efectivamente ejercitada.

c\) ¿Qué modelos de calidad utilizan los resultados de pruebas unitarias? Los resultados de pruebas unitarias y cobertura alimentan directamente los modelos ISO/IEC 25010 (características de Adecuación Funcional, Fiabilidad y Mantenibilidad/Analizabilidad/Capacidad de prueba), ISO/IEC/IEEE 29119 (estándar internacional de procesos y documentación de pruebas de software), CMMI (áreas de proceso VER — Verificación y VAL — Validación) y TMMi (Test Maturity Model integration, niveles 2 y 3 de pruebas gestionadas y definidas).

**Bibliografía verificada**

- **\[1\]** JUnit Team, «JUnit 5 User Guide», 2025. Disponible en: https://junit.org/junit5/docs/current/user-guide/

- **\[2\]** JaCoCo Team, «JaCoCo Java Code Coverage Library — Documentation v0.8.14», Mountainminds GmbH & Co. KG. Disponible en: https://www.jacoco.org/jacoco/trunk/doc/

- **\[3\]** International Organization for Standardization, «ISO/IEC 25010:2023 Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE)» e «ISO/IEC/IEEE 29119 Software testing».

- **\[4\]** J. M. Silva Fernández, «Guía de Laboratorio 07: Pruebas Unitarias», Escuela Profesional de Ingeniería de Sistemas, Universidad Nacional de San Agustín (UNSA), Calidad de Software, 2026-B.
