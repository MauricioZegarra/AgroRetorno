# Protocolos de aceptación | G01

Los siguientes protocolos desarrollan los criterios de la [ERS](ERS.md). Cada ejecución registra revisión Git, fecha UTC, entorno, muestra, resultado observado y artefacto. La validación de alcance con representantes se registra en [Elicitación](Elicitacion.md).

## Flujos funcionales que requieren demostración

| Caso | Requisitos | Preparación y operación | Resultado esperado |
|---|---|---|---|
| AC-01 · Solicitar y decidir | RF02, RF06–RF10 | Preparar material activo con 12 unidades y dos productores. Solicitar 8 desde el primer productor. Consultar con el autorizado y probar la restricción por propiedad con el segundo. Aprobar una solicitud y rechazar otra con un motivo. | El dueño ve estado y motivo. El otro productor recibe 403 si abre el recurso ajeno. La aprobación conserva las 12 unidades hasta la entrega. |
| AC-02 · Stock entre aprobación y entrega | RF05, RF09, RF11, RNF06 | Aprobar 8 de 12 y retirar 5 por una operación válida. Intentar entregar las 8 unidades aprobadas y luego reponer saldo para repetir. | La primera entrega se rechaza con saldo 7 y sin préstamo. La entrega válida descuenta 8 y crea un solo préstamo; el segundo intento de entregar se rechaza. |
| AC-03 · Devolver por condición | RF12–RF15, RNF07 | Entregar 20 jabas. Registrar 5 buenas y 2 dañadas con observación. Completar después con las 13 restantes en buen estado. Consultar historial y reporte. | Tras la primera devolución, el préstamo sigue activo con 13 pendientes. Tras la segunda pasa a CERRADO con saldo cero, 18 buenas y 2 dañadas acumuladas. Historial y reporte conservan los dos registros. |
| AC-04 · Préstamo con fecha vencida | RF12 | Preparar un préstamo abierto con fecha prevista anterior al día actual en la zona de la aplicación. Abrir el listado del productor autorizado y el del encargado. | Se identifica el préstamo como vencido con unidades pendientes. Se registra por separado cómo se muestra en cada vista. |

AC-03 procede de la [consulta técnica](Elicitacion.md#consulta-técnica-del-equipo). PU-39 cubre la misma clasificación acumulada y el cierre con el saldo final repartido en dos devoluciones. AC-01 a AC-04 son escenarios de aceptación. La matriz contiene los casos automatizados ejecutados y sus límites de cobertura.

## Medición de requisitos no funcionales

| ID | Datos y procedimiento | Regla de decisión |
|---|---|---|
| RNF01 | En G08, fijar JMX, endpoints y volumen de datos. Ejecutar un escenario de 10 usuarios, 30 s de rampa y 5 min de duración. Registrar JTL y agregación. | p95 ≤ 2 s y errores ≤ 1 %. |
| RNF02 | Crear cuentas sintéticas e inspeccionar el formato de hash y el resultado de la comprobación BCrypt. | Todos los valores de la muestra usan BCrypt. Registrar solo conteos, sin contraseñas ni hashes individuales. |
| RNF03 | Probar acceso fuera del rol, fuera de la propiedad y formularios mutantes sin token. | 403 en los casos negativos contemplados y registro por caso. |
| RNF04 | Capturar conteos y huellas por tabla, reiniciar PostgreSQL y la aplicación, y volver a capturar. | Coinciden las 10 tablas de la muestra en ambos campos. |
| RNF05 | Enviar cantidades, fechas, motivos o identificadores inválidos y tomar evidencia de respuesta y estado. | Mensaje comprensible y filas afectadas sin cambios. |
| RNF06 | Comparar existencias con movimientos y listar préstamos por solicitud en un conjunto de datos conocido. | Cero saldos negativos y cero duplicados. Registrar inventario y restricciones de la base. |
| RNF07 | Seleccionar operaciones de los flujos de solicitud, decisión, entrega y devolución. | Cada operación de la muestra tiene actor, acción y fecha en el historial. |
| RNF08 | En G09, registrar versión de Chrome y ejecutar los diez casos de los tres flujos. | Los tres flujos pueden completarse y cada caso tiene resultado. |
| RNF09 | Ejecutar Maven y consultar JaCoCo para las líneas de la capa de servicios. | Cobertura de líneas ≥ 80 %. |
| RNF10 | Crear un volcado de PostgreSQL de la muestra, restaurarlo en otro destino y consultar las mismas tablas. | Conteos y huellas coinciden con el origen. Registrar versión, revisión y destino. |

RNF04 y RNF10 cuentan con una verificación de laboratorio sobre 10 tablas de PostgreSQL, con datos sintéticos. [Resultado reproducible](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g02-proceso-de-requerimientos/entregables/G02_Proceso_Requerimientos/verificacion/README.md#resultado-registrado).

[Consultar matriz de trazabilidad y evidencias](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g02-proceso-de-requerimientos/entregables/G02_Proceso_Requerimientos/trazabilidad/Matriz_Trazabilidad.md).
