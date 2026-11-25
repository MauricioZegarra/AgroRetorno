# G04 — Paso 1.1. Frontera, alcance y línea base
**Responsable:** Cielo Cristal Meza Vizcarra  
**Proyecto:** AgroRetorno, Guía 04 — Dimensionamiento y estimación  
**Versión documental:** 0.1 (preanálisis, no validado)  
**Rama de GitHub:** `feature/cielo-g04-dimensionamiento-estimacion`

## 1. Propósito
Delimitar las funciones observables del sistema que serán medidas, para poder ejecutar **NESMA** y **COSMIC** sobre **el mismo alcance**, y después utilizar un tamaño de entrada debidamente justificado para **COCOMO**. En esta fase **todavía no se declara ningún total de puntos de función ni un costo o cronograma**.

## 2. Sistema y contexto
AgroRetorno es una aplicación web de gestión de materiales agrícolas reutilizables para una cooperativa simulada en Arequipa, con dos roles funcionales: `PRODUCTOR` y `ENCARGADO`. Su interfaz web está implementada en Thymeleaf y Spring MVC; posee servicios de negocio, entidades JPA, seguridad Spring Security y persistencia PostgreSQL, con esquema administrado por Flyway.

### 2.1. Dentro de la frontera lógica
- Mantener el catálogo de productores y de materiales.
- Mantener y consultar existencias.
- Solicitar materiales por parte del productor.
- Aprobar o rechazar solicitudes por parte del encargado.
- Registrar la entrega física y el préstamo.
- Registrar devoluciones parciales o completas y el cierre del préstamo.
- Consultar solicitudes, préstamos, historial y reportes disponibles.
- Autenticación/autorización y controles de seguridad: **su clasificación funcional concreta se analizará**, para evitar contar automáticamente cada pantalla, petición o regla.

### 2.2. Fuera de la frontera funcional
- Infraestructura externa de despliegue (p. ej., Render) y administración de GitHub.
- Herramientas de desarrollo, compilación y prueba (Maven, JUnit, Selenium, JMeter).
- Operación técnica de PostgreSQL y Flyway como sistemas externos: su funcionalidad de almacenamiento forma parte del producto; no se los trata automáticamente como aplicaciones proveedoras de datos externas.
- Actividades académicas de las otras guías que no ofrecen funciones al usuario.

### 2.3. Intercambios con actores
- **Productor → AgroRetorno:** credenciales, filtros, datos de solicitud.
- **AgroRetorno → Productor:** disponibilidad, estado de solicitudes, préstamos e historial propio.
- **Encargado → AgroRetorno:** catálogos, ajustes, aprobaciones, rechazos, entregas y devoluciones.
- **AgroRetorno → Encargado:** estado de operaciones, inventarios, reportes e historial.

## 3. Línea base para el conteo
Se toma **provisionalmente** el catálogo `RF01–RF15` y su trazabilidad **del SDD G03 de la rama de Cielo**. Este documento debe compararse con la ERS aprobada por la Persona 1 (G01) antes de cerrar la medición. **No modificar ni inventar requisitos para hacer coincidir el conteo.**

Tres macroprocesos que el docente exige: 
1. Solicitud de préstamo.
2. Aprobación/rechazo y entrega física.
3. Devolución, clasificación de unidades y cierre.

## 4. Inventario de datos
La migración `V1__esquema_inicial.sql` crea **10 tablas físicas**:
`usuarios`, `productores`, `materiales`, `inventario`, `solicitudes`, `prestamos`, `devoluciones`, `detalles_devolucion`, `movimientos_inventario` e `historial_operaciones`.

Como **hipótesis de agrupación** se han identificado ocho grupos lógicos candidatos:
D01 Cuentas y productores; D02 Materiales; D03 Existencias; D04 Solicitudes; D05 Préstamos; D06 Devoluciones con detalles; D07 Movimientos de inventario; D08 Historial de operaciones.

**Ocho candidatos NO significa ocho ILF validados.** Es necesario confirmar grupos reconocidos por el usuario, mantenimiento dentro de la frontera, posibles fusiones/divisiones de archivos lógicos y complejidad mediante DET/RET. No hay evidencia de archivos mantenidos por otra aplicación (EIF); se revisará si corresponde cero.

## 5. Inventario de procesos candidatos
La hoja `Procesos` contiene **23 operaciones candidatas** derivadas de controladores web y de las reglas de negocio. **Veintitrés NO es un conteo NESMA ni COSMIC validado**. Se deberán fusionar o separar operaciones por su objetivo de negocio, datos involucrados y lógica elemental. Por ejemplo, dos URLs distintas no implican necesariamente dos funciones distintas; y una consulta con salida derivada puede clasificarse EO en lugar de EQ.

## 6. Método y cálculo que seguirá
**NESMA:** para cada grupo lógico definir ILF/EIF, RET y DET; para cada transacción EI/EO/EQ, FTR y DET. Aplicar las matrices de complejidad y pesos del método citado. Registrar tipo de conteo (se propone detallado para aplicación existente), criterios, fórmulas y supuestos.

**COSMIC:** delimitar el mismo conjunto de procesos funcionales reconocibles y sus disparadores. Identificar grupos de datos y movimientos Entry (E), Exit (X), Read (R) y Write (W). El tamaño es la suma de movimientos válidos, en **CFP**, que no se equipara a los puntos de función NESMA.

**COCOMO:** declarar variante, entrada de tamaño, coeficientes y fuentes. Estimar esfuerzo en persona-mes, duración y costo con separación explícita de dedicación, coordinación y precio por hora. No dividir la duración entre tres sin justificación; sensibilidad ±20 % y comparación con horas realmente registradas.

## 7. Hallazgos de código para la sustentación
- `SolicitudService.aprobar()` pasa la solicitud a `APROBADA` y **no descuenta** existencias.
- `PrestamoService.registrarEntrega()` crea el préstamo y llama a `InventarioService.descontarPorEntrega()`; este descuenta existencias y registra movimiento.
- `DevolucionService.registrar()` procesa cantidades en buen estado, dañadas y mantenimiento y cierra el préstamo cuando las pendientes llegan a cero.
Son procesos funcionales diferentes desde el punto de vista del negocio, aunque compartan entidades.

## 8. Fuentes usadas
- Enunciado del ingeniero: `Trabajo_Final_Calidad_de_Software V1.pdf`, sección «Guía 4 Dimensionamiento y estimación».
- SDD G03 de Cielo: `G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx` (tabla de trazabilidad).
- Migración SQL: https://github.com/MauricioZegarra/AgroRetorno/blob/main/src/main/resources/db/migration/V1__esquema_inicial.sql
- Controladores: https://github.com/MauricioZegarra/AgroRetorno/tree/main/src/main/java/pe/edu/agroretorno/controller
- Servicios: https://github.com/MauricioZegarra/AgroRetorno/tree/main/src/main/java/pe/edu/agroretorno/service
- Entidades: https://github.com/MauricioZegarra/AgroRetorno/tree/main/src/main/java/pe/edu/agroretorno/entity

## 9. Estado y revisión
**Realizado:** delimitación del sistema, actores, fuentes, catálogo provisional RF, precatálogo de datos y procesos, libro inicial editable.

**Pendiente:** validar frontera y catálogo contra G01; decidir ILF/EIF; inventariar DET/RET/FTR; identificar movimientos COSMIC; calcular tamaño, esfuerzo, plazos, costos y sensibilidad.

**Conclusión provisional:** existe evidencia de código suficiente para iniciar una medición funcional, pero la clasificación y sus resultados **no deben presentarse como definitivos** hasta ejecutar los métodos.
