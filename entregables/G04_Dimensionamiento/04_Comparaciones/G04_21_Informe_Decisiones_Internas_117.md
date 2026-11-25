# G04 — Paso 1.21 | Modelo interno de medición COSMIC G03 (117 movimientos)

**Generado:** 2026-11-24T23:37:29-05:00  
**Git HEAD:** `c2b707bc0e44dc71a34dc8c8409f6321895221b1`  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**SHA-256 CSV maestro (SIN MODIFICAR):** `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`  
**SHA-256 matriz interna:** `a65109e8fcf22d865f2d9b3fe3b53aa0a050804eb772e21be0d3229a8ee16d08`  
**Fuente:** G04_19, G04_20, referencia Java G04_12 y reglas R21 especificadas en G04_21.

## Resolución interna

Se adopta para la **estimación interna de la línea base RF01–RF15 de G03** la opción
que incluye las seis salidas adicionales de P14/P19/P21, trata una Entry funcional
por petición y atribuye la salida de negocio de POST→redirect→GET una vez al evento
que la originó. Se mantienen las R/W documentadas en G04_17.

| Tipo | Movimientos en este modelo interno |
|---|---:|
| E | 21 |
| X | 27 |
| R | 41 |
| W | 28 |
| **Total de decisión interna** | **117 CFP** |

**117 = 111 originales + 6 salidas nuevas.** Las 22 categorías especiales tienen
una regla interna explicita en la matriz; hay **0 filas sin decisión interna**.
Esto NO demuestra conformidad con una edición identificada del manual COSMIC y
NO supone aprobación por G01, docente ni auditor externo.

## Reglas aplicadas
- **R21-E1** (6 filas): Una peticion funcional de consulta dispara una Entry, aun con filtros opcionales; NO sumar una E por atributo.
- **R21-E2** (2 filas): Una peticion explicita de consulta propia dispara una Entry; Authentication define alcance y NO crea otra E.
- **R21-E3** (1 filas): P18 comparte objetivo de consulta para dos roles y conserva una Entry en este modelo; diferencias de acceso se anotan.
- **R21-X1** (10 filas): Se atribuye al evento del usuario una salida de resultado de negocio por el flujo POST->redirect->GET; no duplicar su flash.
- **R21-X2** (9 filas): En vistas con objetos de interes distintos se propone un X por grupo reconocible (incluye las seis adiciones).
- **R21-E0** (12 filas): La operacion de negocio recibe datos o identificador de entrada que disparan el proceso.
- **R21-X0** (8 filas): La consulta o reporte devuelve datos funcionales reconocibles como grupo de salida.
- **R21-RW** (69 filas): La lectura/escritura de grupo persistente se conserva segun G04_17, una vez por proceso, tipo y grupo.

## Totales por proceso (modelo adoptado de trabajo)

| Proceso | E | X | R | W | CFP |
|---|---:|---:|---:|---:|---:|
| P03 | 1 | 1 | 1 | 2 | 5 |
| P04 | 1 | 1 | 1 | 2 | 5 |
| P05 | 1 | 1 | 1 | 0 | 3 |
| P06 | 1 | 1 | 1 | 3 | 6 |
| P07 | 1 | 1 | 1 | 2 | 5 |
| P08 | 1 | 1 | 1 | 0 | 3 |
| P09 | 1 | 1 | 1 | 3 | 6 |
| P10 | 1 | 1 | 2 | 0 | 4 |
| P11 | 1 | 1 | 3 | 2 | 7 |
| P12 | 1 | 1 | 2 | 0 | 4 |
| P13 | 1 | 1 | 2 | 0 | 4 |
| P14 | 1 | 4 | 4 | 0 | 9 |
| P15 | 1 | 1 | 2 | 2 | 6 |
| P16 | 1 | 1 | 1 | 2 | 5 |
| P17 | 1 | 1 | 3 | 5 | 10 |
| P18 | 1 | 1 | 2 | 0 | 4 |
| P19 | 1 | 2 | 2 | 0 | 5 |
| P20 | 1 | 1 | 2 | 5 | 9 |
| P21 | 1 | 3 | 3 | 0 | 7 |
| P22 | 1 | 1 | 1 | 0 | 3 |
| P23 | 1 | 1 | 5 | 0 | 7 |

## Separación de responsabilidades / no alterar fuente

- **Decisión interna G04:** 117 movimientos incluidos por reglas definidas en la documentación (NO por buscar una cifra deseada).
- **Validación de trazabilidad:** 111 referencias Java del maestro localizadas por el paso 1.12; las seis nuevas heredan controlador y tienen evidencia de vistas.
- **No acreditado:** validación COSMIC frente a edición del manual, ERS G01 aceptada, pruebas en navegador y dictamen externo.
- **Archivos que NO cambia este script:** `02_COSMIC/G04_03_Movimientos_COSMIC.csv` y `06_Memoria_Calculo/G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`.
- **Siguiente integración:** conciliar el archivo Excel de 37 hojas original de la rama, agregando los nuevos movimientos con formulas y revisando validacion automatica. Mantener respaldo y no sustituir el informe de los 111/117 escenarios.

**Nota metodológica:** la elección de 117 es una decisión **de estimación académica interna** con criterios explicitados. Es distinto a afirmar que el manual COSMIC garantiza 117 CFP. En particular, una solicitud GET sin payload y los resultados en POST→redirect requieren confrontación normativa antes de denominar el total “COSMIC validado formalmente”.
