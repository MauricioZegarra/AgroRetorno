# Instrumentos de elicitación

**Estado al corte** · Una consulta técnica escrita al equipo de ingeniería está registrada. La entrevista y el cuestionario a personas que representen a usuarios siguen pendientes. El primer avance declara un caso representado.

## Entrevista semiestructurada

Duración prevista de 20 minutos. Pedir a la persona que represente el rol de productor o encargado y describa una solicitud, entrega y devolución con datos ficticios. Registrar su conformidad con el uso académico y usar un código anónimo en la versión pública.

| ID | Rol | Pregunta | Requisitos |
|---|---|---|---|
| E01 | Productor | ¿Cómo buscaría una jaba disponible y qué datos necesita antes de solicitarla? | RF06, RF07 |
| E02 | Productor | ¿Qué debe ver si la solicitud se rechaza o su préstamo vence? | RF08, RF10, RF12 |
| E03 | Encargado | ¿En qué momento se descuenta el stock físico y qué haría si el saldo cambia? | RF05, RF09, RF11 |
| E04 | Encargado | ¿Cómo registra una devolución parcial, una unidad dañada y una en mantenimiento? | RF13, RNF06 |
| E05 | Encargado | ¿Qué información necesita para investigar un descuadre de inventario? | RF14, RF15, RNF07 |
| E06 | Ambos | ¿Qué datos o acciones deberían quedar fuera de su rol? | RF02, RNF03 |

## Cuestionario breve

Duración prevista de cinco minutos. Aplicar después de una demostración corta con el proceso principal. Compartir preguntas y opciones en el mismo orden.

| ID | Pregunta | Tipo u opciones | Vinculación |
|---|---|---|---|
| C01 | ¿Qué rol representa? | Productor / encargado | Contexto |
| C02 | ¿Qué tarea realizaría con mayor frecuencia? | Buscar material / solicitar / aprobar / entregar / devolver / consultar historial | RF06, RF07, RF09, RF11, RF13, RF14 |
| C03 | ¿Con cuánta facilidad identificó el stock disponible? | Escala 1–5, con 1 muy difícil y 5 muy fácil | RF06 |
| C04 | ¿Cuánto tiempo de respuesta acepta en una consulta habitual? | Hasta 1 s / 2 s / 3 s / más de 3 s | RNF01 |
| C05 | ¿Qué información requiere después de un rechazo? | Motivo / fecha / estado / otro, selección múltiple | RF08, RF10 |
| C06 | ¿Qué campo o mensaje le resultó confuso? | Respuesta abierta | RNF05 |

## Registro de aplicación

| Técnica | Fecha y canal | Participante y rol representado | Pregunta y respuesta | Inferencia o decisión | Requisito afectado |
|---|---|---|---|---|---|
| Entrevista | Pendiente | Pendiente | Pendiente | Pendiente | Según respuesta |
| Cuestionario | Pendiente | Pendiente | Pendiente | Pendiente | Según respuesta |

Tras la aplicación, devolver el resumen al participante y registrar fecha y conformidad. Los cambios a la ERS siguen el flujo de G02.

## Consulta técnica del equipo

**Fecha** · 25 de noviembre de 2026.\
**Fuente** · Consulta escrita con una integrante del equipo de ingeniería. Registro parafraseado. La consulta aporta el criterio del equipo sobre el alcance y el flujo de negocio.

| Punto discutido | Respuesta sintetizada | Decisión y trazabilidad |
|---|---|---|
| Devolución parcial | De 20 jabas entregadas, primero regresan cinco en buen estado y dos dañadas. Quedan 13 por devolver. | El préstamo permanece activo. Se distingue saldo pendiente de condición de las unidades. RF11–RF14 y AC-03. |
| Devolución final | Al recibir las 13 restantes en buen estado, el saldo llega a cero. El préstamo pasa a CERRADO. | Registrar 18 en buen estado y dos dañadas en total. El cierre y la clasificación quedan consultables. RF12–RF14 y AC-03. |
| Compensación por daños | AgroRetorno registra la condición y su relación con el productor y el préstamo. La compensación depende de las políticas de la organización. | Compensación y evaluación de responsabilidad quedan fuera del alcance de esta versión. Una ampliación requiere reglas aprobadas y una solicitud de cambio. RF13–RF14. |

[Protocolo AC-03](Verificacion.md#flujos-funcionales-que-requieren-demostración) · [Decisiones de la ERS](ERS.md#5-decisiones-y-revisiones).
