# G04 — Paso 1.18 | Examen de 42 entradas y salidas COSMIC (E/X)

**Proyecto:** AgroRetorno — G04 Dimensionamiento y estimación  
**Responsable de medición:** Cielo Cristal Meza Vizcarra  
**Rama fuente:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit de código consultado:** `1714d5e21c5f33c53b342101c94706f3086edb9c`  
**Línea base RF:** G03 `1fef1d211`, sin atribuir aprobación a G01.  
**Insumo documental:** matriz G04_14 publicada en G04; SHA-256 del CSV utilizado: `6a9621c9437f7e71849d8b784b66659be5761c04039a104e8a5459fe907c933f`.

## 1. Alcance y resultado

Esta auditoría documental examina **21 Entry (E) y 21 Exit (X)** y completa el tratamiento de los 42 movimientos E/X que el dictamen G04_17 de 69 R/W dejaba para el siguiente bloque. Sus **42 registros** se encuentran en `G04_18_Decisiones_EX_42_COSMIC.csv` con la referencia clase/método del modelo, ruta web y evidencia del controlador.

**Advertencia metodológica:** "encontrado en Java", "respaldado en la línea base" o "conservar en propuesta" no son sinónimos de **validación COSMIC definitiva**. Esta revisión documental no ejecutó pruebas funcionales ni verificó una versión específica del manual COSMIC. Por eso **no altera** `COSMIC_Movimientos!K`, `COSMIC_Movimientos!M`, el conteo de 111 CFP ni el CSV maestro.

| Tipo de situación | Filas | Interpretación |
|---|---:|---|
| E con formulario y atributos de negocio | 8 | Existe carga de negocio identificable que entra desde usuario |
| E con identificador de ruta / acción | 4 | Existe id de negocio; delimitar evento y grupo Entry |
| E con filtros opcionales | 6 | Cuando no se envía filtro, el solo GET podría ser un disparador sin Entry de datos reconocible |
| E con identidad de sesión, sin parámetros | 2 | **M045 y M097:** requieren criterio explícito de Entry; no excluir ni aprobar automáticamente |
| E con rutas de dos roles | 1 | **M080:** distinguir evento del productor y del encargado sin doble conteo arbitrario |
| X con `POST → redirect` y flash | 10 | **Atención:** el texto se guarda para otra respuesta; identificar la frontera temporal del Exit y evitar conteo duplicado |
| X de páginas de listado | 7 | Se observa entrega de datos de negocio en la vista GET |
| X de página con múltiples grupos | 3 | **M058, M087, M101:** revisar si se reúnen o separan grupos de datos |
| X de reporte derivado | 1 | **M111:** salida calculada con `ReporteService.generar` |
| **Total** | **42** | Todos con tratamiento documental; no 42 validaciones formales |

## 2. Movimientos que requieren resolución metodológica prioritaria

- **M045 y M097:** `ProductorController.misSolicitudes` e `historial` usan `Authentication`, sin payload de negocio del usuario. No debe asumirse que un clic por sí mismo aporta un grupo de datos Entry válido. Tampoco se puede inferir, por la firma HTTP sola, que el movimiento sea inválido. Definir con el manual COSMIC el evento disparador y el grupo de datos transferido.
- **M080:** productor (`misPrestamos`) y encargado (`listar`) consultan préstamos por vías diferentes. La ausencia de parámetro en una variante y el filtro `estado` en otra exigen una regla de conteo consistente para P18.
- **M005, M010, M019, M024, M033, M044, M064, M069, M079, M096:** los controladores emiten `addFlashAttribute` y redirigen. Aunque contienen nombre, código, cantidades, id o estado, deben comprobarse los límites del proceso y si el Exit se realiza en ese proceso o en el GET posterior. **No basta con ver datos en el texto para contar una X.**
- **M058, M087 y M101:** las vistas mezclan grupos persistentes o referencias asociadas. La agrupación de un solo X puede ser plausible, pero debe cotejarse con la definición de grupo de datos y objeto de interés; no deducir cantidad de X de la cantidad de atributos o de `model.addAttribute`.
- **M111:** el reporte es un conjunto calculado de datos funcionales reconocibles para ENCARGADO. No debe tratarse como un nuevo ILF/EIF.

## 3. Matriz de las 42 decisiones documentales

| ID | P | Tipo | Evidencia Java / URL | Dictamen de línea base |
|---|---|---|---|---|
| M001 | P03 | E | EncargadoProductorController.java:45-54 | **CONSERVAR EN PROPUESTA** — ProductorRequest (datos de cuenta y productor) |
| M005 | P03 | X | EncargadoProductorController.java:45-54 | **REVISAR SALIDA POST REDIRECT** — Flash de nombre completo del productor |
| M006 | P04 | E | EncargadoProductorController.java:75-84 | **CONSERVAR EN PROPUESTA** — ProductorRequest, id del productor |
| M010 | P04 | X | EncargadoProductorController.java:75-84 | **REVISAR SALIDA POST REDIRECT** — Flash de nombre actualizado |
| M011 | P05 | E | EncargadoProductorController.java:33-36 | **REVISAR ENTRY SIN FILTRO** — q opcional |
| M013 | P05 | X | EncargadoProductorController.java:33-36 | **CONSERVAR EN PROPUESTA** — model.addAttribute(productores, ProductorService.listar(q)) |
| M014 | P06 | E | EncargadoMaterialController.java:64-74 | **CONSERVAR EN PROPUESTA** — MaterialRequest y stock inicial |
| M019 | P06 | X | EncargadoMaterialController.java:64-74 | **REVISAR SALIDA POST REDIRECT** — Flash: codigo de material y cantidad total |
| M020 | P07 | E | EncargadoMaterialController.java:95-104 | **CONSERVAR EN PROPUESTA** — MaterialRequest actualizado |
| M024 | P07 | X | EncargadoMaterialController.java:95-104 | **REVISAR SALIDA POST REDIRECT** — Flash: codigo de material actualizado |
| M025 | P08 | E | EncargadoMaterialController.java:48-55 | **REVISAR ENTRY SIN FILTRO** — q y categoria opcionales |
| M027 | P08 | X | EncargadoMaterialController.java:48-55 | **CONSERVAR EN PROPUESTA** — model.addAttribute(materiales, inventarioService.listarInventario(...)) |
| M028 | P09 | E | EncargadoMaterialController.java:128-138 | **CONSERVAR EN PROPUESTA** — AjusteExistenciasRequest (tipo, cantidad y observacion) |
| M033 | P09 | X | EncargadoMaterialController.java:128-138 | **REVISAR SALIDA POST REDIRECT** — Flash: disponibles resultantes del ajuste |
| M034 | P10 | E | ProductorController.java:74-81 | **REVISAR ENTRY SIN FILTRO** — q y categoria opcionales |
| M037 | P10 | X | ProductorController.java:74-81 | **CONSERVAR EN PROPUESTA** — model.addAttribute(materiales, InventarioService.listarInventario(...)) |
| M038 | P11 | E | ProductorController.java:94-106 | **CONSERVAR EN PROPUESTA** — SolicitudRequest (material, cantidad, fecha y motivo) |
| M044 | P11 | X | ProductorController.java:94-106 | **REVISAR SALIDA POST REDIRECT** — Flash: ID creado y estado de solicitud |
| M045 | P12 | E | ProductorController.java:113-116 | **REVISAR ENTRY DE SESION** — Sin filtro HTTP: identidad de Authentication |
| M048 | P12 | X | ProductorController.java:113-116 | **CONSERVAR EN PROPUESTA** — model.addAttribute(solicitudes, listarPorProductor(auth.getName())) |
| M049 | P13 | E | EncargadoSolicitudController.java:46-51 | **REVISAR ENTRY SIN FILTRO** — estado con predeterminado PENDIENTE |
| M052 | P13 | X | EncargadoSolicitudController.java:46-51 | **CONSERVAR EN PROPUESTA** — model.addAttribute(solicitudes, SolicitudService.listar(filtro)) |
| M053 | P14 | E | EncargadoSolicitudController.java:55-57,107-115 | **CONSERVAR EN PROPUESTA** — Identificador id en URL |
| M058 | P14 | X | EncargadoSolicitudController.java:55-57,107-115 | **REVISAR AGRUPACION DE SALIDA** — vistaDetalle con solicitud, productor, disponibilidad y prestamo |
| M059 | P15 | E | EncargadoSolicitudController.java:60-68 | **CONSERVAR EN PROPUESTA** — Identificador id y accion aprobar |
| M064 | P15 | X | EncargadoSolicitudController.java:60-68 | **REVISAR SALIDA POST REDIRECT** — Flash: solicitud id y estado APROBADA |
| M065 | P16 | E | EncargadoSolicitudController.java:71-84 | **CONSERVAR EN PROPUESTA** — RechazoRequest.motivo + id |
| M069 | P16 | X | EncargadoSolicitudController.java:71-84 | **REVISAR SALIDA POST REDIRECT** — Flash: solicitud id y estado RECHAZADA |
| M070 | P17 | E | EncargadoSolicitudController.java:87-96 | **CONSERVAR EN PROPUESTA** — Identificador id y accion de entrega fisica |
| M079 | P17 | X | EncargadoSolicitudController.java:87-96 | **REVISAR SALIDA POST REDIRECT** — Flash: prestamo id, cantidad y unidad entregada |
| M080 | P18 | E | ProductorController.java:119-122; EncargadoPrestamoController.java:37-41 | **REVISAR EVENTO POR ROL** — Productor: identidad de sesion; encargado: estado de prestamo |
| M083 | P18 | X | ProductorController.java:119-122; EncargadoPrestamoController.java:37-41 | **CONSERVAR EN PROPUESTA** — model.addAttribute(prestamos) en ambos controladores |
| M084 | P19 | E | EncargadoPrestamoController.java:44-46,68-72 | **CONSERVAR EN PROPUESTA** — Identificador id en URL |
| M087 | P19 | X | EncargadoPrestamoController.java:44-46,68-72 | **REVISAR AGRUPACION DE SALIDA** — vista con prestamo y devoluciones relacionadas |
| M088 | P20 | E | EncargadoPrestamoController.java:49-61 | **CONSERVAR EN PROPUESTA** — DevolucionRequest con cantidades por condicion y observaciones |
| M096 | P20 | X | EncargadoPrestamoController.java:49-61 | **REVISAR SALIDA POST REDIRECT** — Flash: total devuelto, pendientes o cierre |
| M097 | P21 | E | ProductorController.java:125-130 | **REVISAR ENTRY DE SESION** — Sin filtro HTTP: identidad de Authentication |
| M101 | P21 | X | ProductorController.java:125-130 | **REVISAR AGRUPACION DE SALIDA** — model.addAttribute(solicitudes, prestamos, devoluciones) |
| M102 | P22 | E | EncargadoReporteController.java:45-48 | **REVISAR ENTRY SIN FILTRO** — FiltroHistorial @ModelAttribute |
| M104 | P22 | X | EncargadoReporteController.java:45-48 | **CONSERVAR EN PROPUESTA** — model.addAttribute(operaciones, HistorialService.consultar(filtro)) |
| M105 | P23 | E | EncargadoReporteController.java:36-42 | **REVISAR ENTRY SIN FILTRO** — FiltroReporte @ModelAttribute |
| M111 | P23 | X | EncargadoReporteController.java:36-42 | **CONSERVAR EN PROPUESTA** — model.addAttribute(reporte, ReporteService.generar(filtro)) |

## 4. Interpretación conjunta con el Paso 1.17

- Paso 1.17: 41 R y 28 W examinadas, con disposición de conservar en la línea base G03 bajo la agrupación D01–D08. Sus excepciones estructurales están documentadas allí.
- Paso 1.18: 21 E y 21 X examinadas, con soporte de controlador por fila y advertencias de clasificación. **Las 111 filas tienen ahora tratamiento documental**, pero algunas siguen condicionadas por criterios metodológicos de frontera/grupo de datos.
- El total **111 CFP** no se convierte en definitivo por sumar 69+42 decisiones documentales. No alterar las celdas de inclusión/validación hasta resolver los criterios y recalcular el modelo consistente.

## 5. Ruta de cierre sin inventar evidencias

1. Fijar la edición del manual COSMIC y revisar sus reglas de Entry disparadora y Exit en navegación `POST → redirect → GET`. Documentar una decisión homogénea para todos los movimientos afectados.
2. Resolver las agrupaciones de grupo/objeto de interés en M058/M087/M101 y la entrada de M045/M097/M080, contrastando con RF01–RF15 de G03.
3. Registrar cada decisión final **incluida, excluida o modificada**, con motivación y recontar los movimientos. No atribuirla a G01 ni a un revisor externo sin constancia.
4. Actualizar el CSV maestro y el XLSX de **37 hojas** a la vez, con respaldo; volver a ejecutar `G04_09_Validar_Libro.py`, y contrastar captura y valores cacheados.
5. Solo entonces aplicar un estado explícito y verdadero de **«Validado técnicamente para la línea base G03»** a las filas que satisfagan esas reglas. Eso no equivale a certificar COSMIC ni a aprobar la ERS definitiva.

## 6. Fuentes reproducibles

- Código del commit consultado: `https://github.com/MauricioZegarra/AgroRetorno/tree/1714d5e21c5f33c53b342101c94706f3086edb9c/src/main/java/pe/edu/agroretorno`.
- G04 — `04_Comparaciones/G04_17_Dictamen_RW_COSMIC_G03.md` y `G04_17_Decisiones_RW_69_COSMIC.csv`.
- G04 — `04_Comparaciones/G04_16_Revision_Decisiones_COSMIC_Entradas_Salidas.md`.
- G04 — `04_Comparaciones/G04_14_Matriz_Validacion_Tecnica_COSMIC_111.csv`.

**Dictamen del Paso 1.18:** 42 E/X con análisis y clasificación documental, pero sin una aprobación funcional definitiva automática del modelo. El siguiente paso ya no es comprobar que los nombres de métodos existan: es tomar decisiones metodológicas y reflejar sus consecuencias en el cálculo.
