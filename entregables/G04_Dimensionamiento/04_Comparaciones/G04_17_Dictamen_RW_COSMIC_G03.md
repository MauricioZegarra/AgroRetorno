# G04 - Paso 1.17 | Dictamen de 69 movimientos Read/Write COSMIC

**Proyecto:** AgroRetorno  
**Rama fuente:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit del código consultado:** `70bc993b38d474e07a53a4a346890771b5dacc47`  
**Línea base funcional:** RF01-RF15 documentada en G03 (`1fef1d211`), no ERS G01 aprobada.  
**Insumo:** matriz G04_14, 111 filas; SHA-256 del insumo local: `6a9621c9437f7e71849d8b784b66659be5761c04039a104e8a5459fe907c933f`  
**Naturaleza:** dictamen de clasificación para la línea base de trabajo. Sin aprobación académica.

## 1. Resultado del examen de R/W

| Elemento | Resultado |
|---|---:|
| Movimientos originales en la matriz | 111 |
| Movimientos R analizados individualmente | 41 |
| Movimientos W analizados individualmente | 28 |
| **Total de decisiones R/W con fundamento** | **69** |
| Movimientos R/W sin decisión documental en esta revisión | 0 |
| Movimientos E/X (siguiente bloque de revisión) | 42 |
| Resultado COSMIC del modelo anterior | 111 CFP preliminares |

La decisión de esta evaluación **es mantener las 69 R/W en la línea base G03**, con la interpretación de grupos de datos D01-D08 usada en el modelo. No corresponde decir que se verificaron transacciones de ejecución, ni afirmar revisión por G01 o por el docente. Si la frontera funcional cambia, se actualizan las decisiones y los 111 CFP. La referencia textual Java (Paso 1.12) es necesaria pero no suficiente: se añadió aquí la finalidad del Read/Write dentro del proceso de negocio.

## 2. Criterios de decisión utilizados

1. Un **R** requiere que el proceso recupere un grupo persistente de datos reconocido en la línea base para ejecutar una funcionalidad. Un método de repositorio no equivale automáticamente a un CFP.
2. Un **W** requiere actualización persistente del grupo de datos como resultado del proceso; múltiples SQL de una sola instancia del grupo no implican varios CFP.
3. Se conserva el tratamiento de D01 como *Cuentas y productores*, D02 Materiales, D03 Existencias, D04 Solicitudes, D05 Préstamos, D06 Devoluciones/detalles, D07 Movimientos y D08 Historial conforme al inventario provisional de G04.
4. **D07** tiene una consulta visible de movimientos de inventario en `EncargadoMaterialController` y un registro persistente en `InventarioService`. **D08** alimenta la pantalla visible de historial del encargado y está vinculado al RF14 de la línea base G03: no se clasifica automáticamente como logging descartable.
5. **M082** y **M107** se conservan como lecturas de grupos distintos de los grupos principales D05/D02. La distinción depende de que D04/D03 sean efectivamente grupos funcionales independientes; un `JOIN` o una tabla SQL no basta para decidirlo.
6. La identidad del productor en **M046** se usa para obtener el ámbito de solicitudes propias, no se cuenta simplemente por autenticar al usuario.

## 3. Matriz de las 69 decisiones y evidencia específica

| ID | Proceso | R/W | Grupo | Decisión | Evidencia en código actual |
|---|---|---|---|---|---|
| M002 | P03 | R | D01 | Mantener (base G03) | `service/ProductorService.java`: registrar: existsByUsername y existsByDni verifican unicidad de datos persistentes D01 |
| M003 | P03 | W | D01 | Mantener (base G03) | `service/ProductorService.java`: registrar: usuarioRepository.save y productorRepository.save mantienen un solo D01 agrupado |
| M004 | P03 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: guarda alta de productor; el historial es consultable por Encargado en RF14 |
| M007 | P04 | R | D01 | Mantener (base G03) | `service/ProductorService.java`: actualizar: productorRepository.findById obtiene el perfil D01 a cambiar |
| M008 | P04 | W | D01 | Mantener (base G03) | `service/ProductorService.java`: actualizar: productorRepository.save persiste el perfil D01 editado |
| M009 | P04 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: guarda modificacion de productor consultable en historial RF14 |
| M012 | P05 | R | D01 | Mantener (base G03) | `service/ProductorService.java`: listar: productorRepository.buscar consulta D01 por criterio de busqueda |
| M015 | P06 | R | D02 | Mantener (base G03) | `service/MaterialService.java`: registrar: materialRepository.existsByCodigo lee D02 para validar codigo unico |
| M016 | P06 | W | D02 | Mantener (base G03) | `service/MaterialService.java`: registrar: materialRepository.save crea el registro D02 |
| M017 | P06 | W | D03 | Mantener (base G03) | `service/InventarioService.java`: crearInventarioInicial: inventarioRepository.save crea D03 separado de Materiales |
| M018 | P06 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: registra alta de material en historial de RF14 |
| M021 | P07 | R | D02 | Mantener (base G03) | `service/MaterialService.java`: actualizar: materialRepository.findByIdConInventario obtiene D02 existente |
| M022 | P07 | W | D02 | Mantener (base G03) | `service/MaterialService.java`: actualizar: materialRepository.save actualiza D02 |
| M023 | P07 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: cambio de material incorporado al historial consultable |
| M026 | P08 | R | D02 | Mantener (base G03) | `service/InventarioService.java`: listarInventario: materialRepository.buscarConInventario obtiene catalogo D02 |
| M029 | P09 | R | D03 | Mantener (base G03) | `service/InventarioService.java`: ajustarExistencias: bloquear(materialId) obtiene inventario D03 para ajuste |
| M030 | P09 | W | D03 | Mantener (base G03) | `service/InventarioService.java`: ajustarExistencias: guardar(inv) persiste balances D03 |
| M031 | P09 | W | D07 | Mantener (base G03) | `service/InventarioService.java`: ajustarExistencias: registrarMovimiento persiste D07 visible en detalle de material |
| M032 | P09 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: guarda motivo de ajuste para consulta RF14 |
| M035 | P10 | R | D02 | Mantener (base G03) | `service/InventarioService.java`: listarInventario: materialRepository.buscarConInventario devuelve Material D02 |
| M036 | P10 | R | D03 | Mantener (base G03) | `service/InventarioService.java`: listarInventario: respuesta contiene existencias de D03, entidad distinta de Material |
| M039 | P11 | R | D01 | Mantener (base G03) | `service/SolicitudService.java`: registrar: productorRepository.findByUsername identifica productor D01 para la solicitud |
| M040 | P11 | R | D02 | Mantener (base G03) | `service/SolicitudService.java`: registrar: materialRepository.findByIdConInventario obtiene material D02 solicitado |
| M041 | P11 | R | D03 | Mantener (base G03) | `service/InventarioService.java`: validarDisponibilidad: inspecciona inventario D03 para autorizar cantidad |
| M042 | P11 | W | D04 | Mantener (base G03) | `service/SolicitudService.java`: registrar: solicitudRepository.save guarda solicitud D04 PENDIENTE |
| M043 | P11 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: conserva evento de solicitud para historial RF14 |
| M046 | P12 | R | D01 | Mantener (base G03) | `service/SolicitudService.java`: listarPorProductor: productorRepository.findByUsername resuelve D01 para el ambito propio |
| M047 | P12 | R | D04 | Mantener (base G03) | `service/SolicitudService.java`: listarPorProductor: solicitudRepository.findByProductorId obtiene D04 del usuario |
| M050 | P13 | R | D04 | Mantener (base G03) | `service/SolicitudService.java`: listar: solicitudRepository.findByEstado/findAllOrdenadas lee D04 |
| M051 | P13 | R | D01 | Mantener (base G03) | `dto/SolicitudResponse.java`: from(Solicitud): muestra nombre y DNI de productor D01 asociado a cada solicitud |
| M054 | P14 | R | D04 | Mantener (base G03) | `service/SolicitudService.java`: obtener: busca D04 por identificador; se muestra detalle del tramite |
| M055 | P14 | R | D01 | Mantener (base G03) | `controller/EncargadoSolicitudController.java`: vistaDetalle: productorService.obtener(s.productorId()) lee D01 por separado |
| M056 | P14 | R | D03 | Mantener (base G03) | `controller/EncargadoSolicitudController.java`: vistaDetalle: inventarioService.consultarDisponibilidad lee D03 actual |
| M057 | P14 | R | D05 | Mantener (base G03) | `controller/EncargadoSolicitudController.java`: vistaDetalle: prestamoService.buscarPorSolicitud consulta D05 vinculado |
| M060 | P15 | R | D04 | Mantener (base G03) | `service/SolicitudService.java`: aprobar: solicitudRepository.findByIdParaActualizar lee estado D04 |
| M061 | P15 | R | D03 | Mantener (base G03) | `service/SolicitudService.java`: aprobar: inventarioService.validarDisponibilidad evalua D03 sin descontarlo |
| M062 | P15 | W | D04 | Mantener (base G03) | `service/SolicitudService.java`: aprobar: s.setEstado(APROBADA) modifica D04 dentro de transaccion |
| M063 | P15 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: documenta aprobacion disponible en historial RF14 |
| M066 | P16 | R | D04 | Mantener (base G03) | `service/SolicitudService.java`: rechazar: solicitudRepository.findByIdParaActualizar lee D04 |
| M067 | P16 | W | D04 | Mantener (base G03) | `service/SolicitudService.java`: rechazar: guarda estado RECHAZADA y motivo en D04 |
| M068 | P16 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: documenta rechazo visible en historial RF14 |
| M071 | P17 | R | D04 | Mantener (base G03) | `service/PrestamoService.java`: registrarEntrega: findByIdParaActualizar comprueba solicitud APROBADA D04 |
| M072 | P17 | R | D05 | Mantener (base G03) | `service/PrestamoService.java`: registrarEntrega: prestamoRepository.existsBySolicitudId comprueba D05 para evitar doble entrega |
| M073 | P17 | R | D03 | Mantener (base G03) | `service/InventarioService.java`: descontarPorEntrega: bloquea inventario D03 y revalida saldo |
| M074 | P17 | W | D03 | Mantener (base G03) | `service/InventarioService.java`: descontarPorEntrega: guarda disponible/prestado en D03 |
| M075 | P17 | W | D05 | Mantener (base G03) | `service/PrestamoService.java`: registrarEntrega: prestamoRepository.save crea D05 ACTIVO |
| M076 | P17 | W | D04 | Mantener (base G03) | `service/PrestamoService.java`: registrarEntrega: s.setEstado(ENTREGADA) mantiene D04 |
| M077 | P17 | W | D07 | Mantener (base G03) | `service/InventarioService.java`: descontarPorEntrega: registra movimiento D07 para entrega fisica |
| M078 | P17 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: historiza entrega fisica consultable RF14 |
| M081 | P18 | R | D05 | Mantener (base G03) | `service/PrestamoService.java`: listar/listarPorProductor consultan PrestamoRepository D05 por estado o identidad |
| M082 | P18 | R | D04 | Mantener (base G03) | `dto/PrestamoResponse.java`: from(Prestamo): toma solicitudId y datos de productor/material desde Solicitud D04; no justificar solo por JOIN |
| M085 | P19 | R | D05 | Mantener (base G03) | `service/PrestamoService.java`: obtener: prestamoRepository.findById lee D05 para detalle y pendientes |
| M086 | P19 | R | D06 | Mantener (base G03) | `service/DevolucionService.java`: listarPorPrestamo: devolucionRepository.findByPrestamoId lee D06 |
| M089 | P20 | R | D05 | Mantener (base G03) | `service/DevolucionService.java`: registrar: prestamoRepository.findByIdParaActualizar obtiene D05 pendiente |
| M090 | P20 | R | D03 | Mantener (base G03) | `service/InventarioService.java`: registrarRetorno: consulta/bloquea D03 para clasificar cantidades devueltas |
| M091 | P20 | W | D03 | Mantener (base G03) | `service/InventarioService.java`: registrarRetorno: actualiza balances D03 por condicion de unidad |
| M092 | P20 | W | D05 | Mantener (base G03) | `service/DevolucionService.java`: registrar: prestamoRepository.save actualiza D05 y estado de cierre |
| M093 | P20 | W | D06 | Mantener (base G03) | `service/DevolucionService.java`: registrar: devolucionRepository.save persiste cabecera/detalles D06 |
| M094 | P20 | W | D07 | Mantener (base G03) | `service/InventarioService.java`: registrarRetorno: registrarMovimiento guarda D07 por tipos en una sola W del grupo |
| M095 | P20 | W | D08 | Mantener (base G03) | `service/HistorialService.java`: registrar: historiza devolucion para consulta RF14 |
| M098 | P21 | R | D04 | Mantener (base G03) | `service/SolicitudService.java`: listarPorProductor: obtiene solicitudes D04 del historial propio |
| M099 | P21 | R | D05 | Mantener (base G03) | `service/PrestamoService.java`: listarPorProductor: obtiene prestamos D05 del historial propio |
| M100 | P21 | R | D06 | Mantener (base G03) | `service/DevolucionService.java`: listarPorProductor: obtiene devoluciones D06 del historial propio |
| M103 | P22 | R | D08 | Mantener (base G03) | `service/HistorialService.java`: consultar: historialRepository.findAll con filtro entrega registros D08 |
| M106 | P23 | R | D02 | Mantener (base G03) | `service/ReporteService.java`: generar: materialRepository.buscarConInventario obtiene catalogo D02 |
| M107 | P23 | R | D03 | Mantener (base G03) | `service/ReporteService.java`: generar: respuesta MaterialResponse incluye saldos de inventario D03 |
| M108 | P23 | R | D04 | Mantener (base G03) | `service/ReporteService.java`: generar: consulta solicitudes D04 para estadisticas |
| M109 | P23 | R | D05 | Mantener (base G03) | `service/ReporteService.java`: generar: prestamoRepository.findByEstado obtiene D05 |
| M110 | P23 | R | D06 | Mantener (base G03) | `service/ReporteService.java`: generar: consulta devoluciones D06 para estadisticas |

## 4. Casos de lectura/escritura que exigen especial transparencia

- **Diez W de D08:** se conservan porque la línea base G03 requiere historial de operaciones consultable. El criterio de frontera es funcional; la sola llamada a `HistorialService.registrar` no certifica que un sistema distinto también deba contarlas.
- **M031, M077 y M094 de D07:** representan movimiento de inventario reconocido en la interfaz. M094 agrupa todas las escrituras del mismo grupo durante la devolución y no crea una W por cada cambio de estado.
- **M051/M055 y M082:** se sustentan por los atributos de respuesta y la distinción de los grupos de datos del usuario. El JOIN físico no se equipara a un movimiento independiente; se adopta la agrupación lógica declarada.
- **M107:** el reporte obtiene material e inventario; son dos grupos bajo la frontera de la medición, no dos CFP por usar una unión SQL.

## 5. Qué se puede marcar y qué no

**Con decisión sustentada en esta revisión:** las 41 R y 28 W tienen disposición documental de *mantener en la medición de línea base G03*. No queda ninguna R/W sin tratamiento dentro de ese alcance.

**No acreditado por este documento:** conformidad de la ERS G01, certificación COSMIC externa, prueba de ejecución de cada movimiento, aprobación de los 42 E/X y validación de las cifras monetarias como costos reales. En el libro histórico las 111 filas aún muestran `Por validar`; no deben cambiarse a `Validado` solo por guardar esta matriz. El cambio de estado deberá indicar expresamente **validación técnica de línea base G03** y aplicarse junto a la revisión de E/X, conservando siempre el historial de versiones.

## 6. Próximo cierre del modelo

1. Revisar los **21 E y 21 X** con la identificación del grupo de entrada o salida según el proceso; distinguir controles vacíos de transferencias de datos funcionales.
2. Conciliar las decisiones E/X con estas 69 R/W y registrar en la matriz el motivo por movimiento.
3. Actualizar en un solo paso el CSV de movimientos y el XLSX de 37 hojas en la computadora de la responsable (sobre la última copia real), recalcular y ejecutar G04_09.
4. Mantener como falta de fuente —no como trabajo aprobado— los datos que dependen de una ERS G01 no aportada o de horas reales inexistentes.

## 7. Fuentes estables

- Matriz de revisión: `G04_17_Decisiones_RW_69_COSMIC.csv` de este paquete, que conserva los 69 IDs y una referencia GitHub a la clase fuente.
- [Código AgroRetorno en el commit consultado](https://github.com/MauricioZegarra/AgroRetorno/tree/70bc993b38d474e07a53a4a346890771b5dacc47/src/main/java/pe/edu/agroretorno).
- [Catálogo RF01–RF15 G03](https://github.com/MauricioZegarra/AgroRetorno/blob/1fef1d211096d4ac3ba46f6adb16d3a5e5869ef2/entregables/G03_Analisis_Sistemas/03_Casos_de_Uso/G03_06_Matriz_RF_CU_Codigo_Interfaz.csv).

**Dictamen:** R/W documentalmente resueltos en línea base G03: **69/69**. El resultado numérico final queda sujeto a la revisión del conjunto completo E/X/R/W, y no se afirma aprobación externa.
