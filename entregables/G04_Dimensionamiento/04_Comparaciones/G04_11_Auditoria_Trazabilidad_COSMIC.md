# G04 - Paso 1.11 | Auditoria de trazabilidad COSMIC vs Java

**Fecha de ejecucion:** 2026-11-24T22:27:54-05:00  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit examinado:** `8021edd`  
**Archivo base:** `entregables/G04_Dimensionamiento/02_COSMIC/G04_03_Movimientos_COSMIC.csv`  
**SHA256 del CSV base:** `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`

## Resultado

| Control | Resultado |
|---|---:|
| Movimientos leidos | 111 |
| Procesos funcionales distintos | 21 |
| Punteros clase.metodo detectados en Java | 107 |
| Punteros no localizados | 4 |
| Filas D08 (historial) | 12 |
| Filas con misma terna proceso/tipo/grupo | 0 |
| Filas sin alertas estructurales | 84 |
| Entradas E incluidas | 21 |
| Salidas X incluidas | 21 |
| Lecturas R incluidas | 41 |
| Escrituras W incluidas | 28 |

## Foco: historial D08

D08 cuenta con consulta visible de historial (EncargadoReporteController.historial y HistorialService.consultar). 
Esto impide clasificar automaticamente todos sus movimientos como logging tecnico. 
A su vez, la existencia de una llamada historialService.registrar no valida por si sola un movimiento funcional COSMIC.

| ID | Proceso | Tipo | Detalle | Codigo declarado |
|---|---|---|---|---|
| M004 | P03 | W | Registrar auditoría de alta | EncargadoProductorController.registrar |
| M009 | P04 | W | Registrar auditoría de modificación | EncargadoProductorController.actualizar |
| M018 | P06 | W | Auditar registro de material | EncargadoMaterialController.registrar |
| M023 | P07 | W | Auditar actualización | EncargadoMaterialController.actualizar |
| M032 | P09 | W | Auditar ajuste si corresponde | EncargadoMaterialController.ajustar |
| M043 | P11 | W | Auditar registro de solicitud | ProductorController.registrarSolicitud |
| M063 | P15 | W | Auditar aprobación | EncargadoSolicitudController.aprobar |
| M068 | P16 | W | Auditar rechazo | EncargadoSolicitudController.rechazar |
| M078 | P17 | W | Auditar entrega física | EncargadoSolicitudController.entregar |
| M095 | P20 | W | Auditar devolución | EncargadoPrestamoController.devolver |
| M103 | P22 | R | Consultar historial de operaciones | EncargadoReporteController.historial |
| M104 | P22 | X | Mostrar registros filtrados | EncargadoReporteController.historial |

## Movimientos con referencia Java no localizada

| ID | Codigo declarado |
|---|---|
| M080 | `ProductorController.misPrestamos / EncargadoPrestamoController.listar` |
| M081 | `ProductorController.misPrestamos / EncargadoPrestamoController.listar` |
| M082 | `ProductorController.misPrestamos / EncargadoPrestamoController.listar` |
| M083 | `ProductorController.misPrestamos / EncargadoPrestamoController.listar` |

## Limites y decision

- El informe identifica referencias textuales, alertas de duplicacion potencial y focos de revision; **no aprueba** movimientos por si solo.
- El mismo proceso/tipo/grupo puede necesitar justificacion o eliminacion del duplicado; decidirlo con el codigo y los requisitos.
- Cada fila de `G04_11_Control_COSMIC_Filas.csv` conserva su estado original y exige decision funcional documentada.
- La ERS definitiva de G01 y las horas reales no se infieren ni se fabrican.
- No se modifica el Excel de 37 hojas ni los 111 estados originales.
