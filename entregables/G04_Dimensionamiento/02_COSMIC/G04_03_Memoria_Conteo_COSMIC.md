# G04 — Paso 1.3: medición funcional COSMIC (versión de revisión)

**Responsable:** Cielo Cristal Meza Vizcarra  
**Proyecto:** AgroRetorno · Calidad de Software · 2026-B  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Versión del documento:** 0.3, 23/11/2026  
**Estado:** **PRELIMINAR — NO APROBADO**

## 1. Objetivo y alcance

Aplicar COSMIC al mismo alcance funcional de la Guía 04 Paso 1.1 y del conteo NESMA del Paso 1.2, midiendo **movimientos de grupos de datos** de los procesos reconocibles por el usuario, no clases Java, endpoints HTTP, sentencias SQL, pantallas o campos.

La frontera comprende la aplicación AgroRetorno: mantenimiento de productores y materiales, inventario, solicitudes, aprobación/rechazo, entregas, devoluciones, consultas, auditoría y reportes. Los usuarios funcionales externos son **Productor** y **Encargado**. PostgreSQL forma parte de la persistencia propia de la aplicación; la plataforma de despliegue y las herramientas de desarrollo quedan fuera.

La base de conteo es **23 procesos candidatos**, de los cuales **21 se incluyen provisionalmente** y P01/P02 (iniciar/cerrar sesión) quedan fuera hasta revisar su papel en la ERS y la frontera funcional. Esta decisión coincide con el alcance provisional documentado para NESMA, pero no debe darse por aprobada.

## 2. Regla de cálculo

Se registra **1 COSMIC Function Point (CFP)** por movimiento de datos funcional aceptado:

| Tipo | Definición operativa |
|---|---|
| E · Entry | Grupo de datos que cruza la frontera desde un usuario funcional hacia un proceso. |
| X · Exit | Grupo de datos que cruza desde el proceso hacia un usuario funcional. |
| R · Read | Grupo de datos que el proceso recupera del almacenamiento persistente. |
| W · Write | Grupo de datos que el proceso escribe en el almacenamiento persistente. |

**CFP del proceso = cantidad de E + X + R + W aceptados.** Una lectura SQL no es automáticamente un R, ni cada dato/atributo cuenta 1 CFP. Los movimientos de un mismo grupo deben comprobarse según las reglas del método. Un acuse de control sin datos de negocio no debe contarse automáticamente como X.

El Excel registra los cuatro tipos en movimientos individuales; la columna `Contar` (Sí/No) permite excluirlos y el total cambia mediante fórmulas. Un proceso también puede excluirse desde `COSMIC_Procesos`.

## 3. Grupos de datos candidatos

Los mismos grupos preliminares de la línea base se emplean para identificar los datos:

| ID | Grupo candidato | Tablas físicas principales |
|---|---|---|
| D01 | Cuentas y productores | `usuarios`, `productores` |
| D02 | Materiales | `materiales` |
| D03 | Existencias | `inventario` |
| D04 | Solicitudes | `solicitudes` |
| D05 | Préstamos | `prestamos` |
| D06 | Devoluciones y detalles | `devoluciones`, `detalles_devolucion` |
| D07 | Movimientos | `movimientos_inventario` |
| D08 | Historial | `historial_operaciones` |

Estos grupos **no constituyen por sí solos ocho CFP ni ocho procesos**. El total COSMIC procede de los movimientos aceptados. La separación o agrupación final debe confirmarse contra la definición funcional y los objetivos de datos.

## 4. Resultados preliminares del libro

| Tipo | Movimientos candidatos contados | Unidad |
|---|---:|---|
| Entry (E) | 21 | CFP |
| Exit (X) | 21 | CFP |
| Read (R) | 41 | CFP |
| Write (W) | 28 | CFP |
| **TOTAL** | **111** | **CFP PRELIMINARES** |

**El valor de 111 CFP NO está aprobado**: es una primera asignación de movimientos derivada del código y de las funcionalidades identificadas. Los campos de decisión y validación están abiertos para correcciones.

### Distribución por proceso

| ID | Proceso candidato | E | X | R | W | CFP provisional |
|---|---|---:|---:|---:|---:|---:|
| P01 | Iniciar sesión | 0 | 0 | 0 | 0 | Excluido provisionalmente |
| P02 | Cerrar sesión | 0 | 0 | 0 | 0 | Excluido provisionalmente |
| P03 | Registrar productor | 1 | 1 | 1 | 2 | 5 |
| P04 | Actualizar productor | 1 | 1 | 1 | 2 | 5 |
| P05 | Buscar productores | 1 | 1 | 1 | 0 | 3 |
| P06 | Registrar material | 1 | 1 | 1 | 3 | 6 |
| P07 | Actualizar material | 1 | 1 | 1 | 2 | 5 |
| P08 | Consultar materiales | 1 | 1 | 1 | 0 | 3 |
| P09 | Ajustar existencias | 1 | 1 | 1 | 3 | 6 |
| P10 | Consultar inventario disponible | 1 | 1 | 2 | 0 | 4 |
| P11 | Registrar solicitud | 1 | 1 | 3 | 2 | 7 |
| P12 | Consultar solicitudes propias | 1 | 1 | 2 | 0 | 4 |
| P13 | Consultar solicitudes del almacén | 1 | 1 | 2 | 0 | 4 |
| P14 | Consultar detalle de solicitud | 1 | 1 | 4 | 0 | 6 |
| P15 | Aprobar solicitud | 1 | 1 | 2 | 2 | 6 |
| P16 | Rechazar solicitud | 1 | 1 | 1 | 2 | 5 |
| P17 | Registrar entrega física | 1 | 1 | 3 | 5 | 10 |
| P18 | Consultar préstamos | 1 | 1 | 2 | 0 | 4 |
| P19 | Consultar detalle de préstamo | 1 | 1 | 2 | 0 | 4 |
| P20 | Registrar devolución | 1 | 1 | 2 | 5 | 9 |
| P21 | Consultar historial del productor | 1 | 1 | 3 | 0 | 5 |
| P22 | Consultar historial de auditoría | 1 | 1 | 1 | 0 | 3 |
| P23 | Generar reporte consolidado | 1 | 1 | 5 | 0 | 7 |

## 5. Recorrido de código para los tres procesos clave

**P11 — Registrar solicitud.** `ProductorController.registrarSolicitud()` delega en `SolicitudService.registrar()`. Se recibe material, cantidad, fecha y motivo; se consultan productor, material e inventario; se escribe la solicitud y se registra el historial. El resultado visible incluye el identificador de la solicitud. **No se descuenta el stock** en esta etapa.

**P15 — Aprobar solicitud.** `EncargadoSolicitudController.aprobar()` delega en `SolicitudService.aprobar()`. Lee solicitud y disponibilidad y cambia el estado a `APROBADA`. La salida observada debe comprobarse para no contar únicamente un mensaje de confirmación. **La aprobación no modifica el saldo del inventario.**

**P17 — Registrar entrega física.** `EncargadoSolicitudController.entregar()` llama a `PrestamoService.registrarEntrega()`. Comprueba la aprobación, consulta si existe préstamo, solicita a `InventarioService.descontarPorEntrega()` el cambio de existencias con bloqueo, registra préstamo, movimiento, cambio de estado y auditoría. Tiene una cantidad de grupos de datos **distinta** de P15, lo que justifica modelar ambos como procesos funcionales diferentes.

**P20 — Registrar devolución.** `EncargadoPrestamoController.devolver()` llama a `DevolucionService.registrar()`: registra clasificación por condición, actualiza existencias, préstamo, devoluciones, movimientos e historial. El préstamo cierra cuando no quedan cantidades pendientes.

**P23 — Reporte consolidado.** `ReporteService.generar()` consulta materiales, solicitudes, préstamos y devoluciones y calcula indicadores agregados. Revisar si todas las lecturas se corresponden con grupos distintos bajo la frontera adoptada.

## 6. Hipótesis y riesgos de sobreconteo

1. **X de confirmaciones:** comprobar si devuelve identificador/estado/datos funcionales. Si es solo un mensaje de control o redirección, marcar `Contar = No` y justificarlo.
2. **D08 auditoría:** un historial accesible por el Encargado puede ser funcional, pero no convertir automáticamente toda operación de logging en un W funcional. Revisar requisito y finalidad.
3. **Consultas con joins:** varias tablas o múltiples consultas SQL no equivalen necesariamente a varios R COSMIC.
4. **P08 frente a P10:** comparar objetivos, actores y grupos; no contar dos procesos si funcionalmente representan el mismo objetivo sin distinción relevante.
5. **P18:** verificar variantes de consulta para Productor y Encargado.
6. **P01 y P02:** no se incluyen solo por consistencia con el conteo NESMA preliminar; confirmar inclusión o exclusión después de revisar el requisito RF01.
7. **RF:** los ID actuales proceden de la línea base documentada en G03 y deben contrastarse con la ERS de G01.
8. **Unidades distintas:** NESMA ofrece provisionalmente **131 UFP**, mientras COSMIC propone **111 CFP**. No es una discrepancia aritmética ni existe una conversión automática; son métodos y magnitudes distintas.

## 7. Instrucciones de recálculo para el ingeniero

1. Abrir `G04_03_Conteo_NESMA_COSMIC.xlsx`, hoja `COSMIC_Movimientos`.
2. Filtrar por ID de proceso y revisar grupo, tipo y evidencia Java.
3. Cambiar `Contar` de **Sí** a **No** si el movimiento no supera la revisión; escribir el motivo en la memoria/acta.
4. Revisar la hoja `COSMIC_Procesos`: contiene sumas E/X/R/W por proceso y permite excluir un proceso completo.
5. Verificar `COSMIC_Resumen`: recalcula automáticamente CFP totales y por tipo.
6. Contrastar con las hojas NESMA sin sumar ni convertir UFP y CFP.
7. Documentar los supuestos aprobados, la versión medida y la revisión del equipo.

## 8. Fuentes

- Consigna del curso: `Trabajo_Final_Calidad_de_Software V1.pdf`, «Guía 4 Dimensionamiento y estimación».
- Línea base: `entregables/G04_Dimensionamiento/06_Memoria_Calculo/G04_01_Frontera_y_Linea_Base.md`.
- Código: https://github.com/MauricioZegarra/AgroRetorno/tree/main/src/main/java/pe/edu/agroretorno
- Modelo de datos: https://github.com/MauricioZegarra/AgroRetorno/blob/main/src/main/resources/db/migration/V1__esquema_inicial.sql
- Manual de Medición COSMIC: se debe registrar la edición oficial efectivamente utilizada al cerrar el entregable. **No se ha verificado en esta fase su edición vigente.**

## 9. Dictamen de revisión de este paso

**El modelo es recalculable y está listo para revisión, pero todavía NO puede presentarse como medición COSMIC definitiva.** Falta validar agrupaciones de datos, salidas de control, procesos duplicados, ERS y métodos específicos aplicados a cada movimiento. Las capturas deben realizarse sobre el libro abierto por la responsable y reflejar los resultados realmente observados.
