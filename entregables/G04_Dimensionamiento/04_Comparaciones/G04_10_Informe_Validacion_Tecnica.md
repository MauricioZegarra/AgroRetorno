# G04 - Paso 1.10 | Auditoria independiente de fuente, costos y sensibilidad

**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit base:** `f0b89fa07ee9d8cf36a03c3f917c08d99b86011c`  
**Fecha local de ejecucion:** `2026-11-24T22:19:51-05:00`  
**Alcance:** Codigo Java productivo de `src/main/java` y CSV de escenarios de la rama G04.  
**Herramienta:** Python estandar; no se modifican los datos del XLSX.

## Resultado verificable

| Comprobacion | Estado | Evidencia / conclusion |
|---|---|---|
| Java productivo / archivos | OK | 80 archivos Java actuales (referencia historica 80) |
| Java productivo / KLOC | OK | Conteo actual 3491 LOC aproximadas = 3.491 KLOC; modelo historico 3491 LOC = 3.491 KLOC |
| Sensibilidad / estructura | OK | 12 escenarios; cada variable tiene 3 puntos |
| Sensibilidad / 12 recalculos | OK | Ecuaciones y deltas reproducidos sin diferencias |
| Costos / escenarios 1p y 3p | OK | Costos, horas, utilizacion y reserva al 10% verificados |
| Cronograma / fases y pesos | OK | 6 fases; pesos=100%; plazos y continuidad correctos |

## Recuento actual de codigo Java

| Paquete | Archivos | Lineas fisicas | LOC aproximadas |
|---|---:|---:|---:|
| agroretorno | 1 | 16 | 9 |
| api | 5 | 302 | 252 |
| config | 3 | 235 | 188 |
| controller | 9 | 758 | 659 |
| dto | 19 | 603 | 488 |
| entity | 17 | 774 | 591 |
| exception | 5 | 45 | 30 |
| repository | 9 | 214 | 166 |
| security | 3 | 153 | 124 |
| service | 9 | 1167 | 984 |
| **TOTAL** | **80** | **4267** | **3491** |

**DECISION DE ALCANCE:** El recuento actual coincide con las 3.491 LOC aproximadas del modelo historico. No se demuestra identidad de cada archivo por este solo resultado.

## Escenario nominal (hipotesis documentadas)

- Tarifa: S/ 20 por hora; 160 h por persona-mes; dedicacion del 80%; coordinacion del 15% para tres personas.
- COCOMO 81 basico, modo organico: esfuerzo `E=2.4*KLOC^1.05`; duracion `TDEV=2.5*E^0.38`.
- El plazo para tres personas usa `max(TDEV, E/(3*dedicacion*(1-coordinacion)))`; **TDEV no se divide entre tres**.
- Costo directo 1p = `E*160*tarifa`; 3p = `E*160*tarifa/(1-coordinacion)`.
- Resultado nominal 1p: S/ 28540.30 y 11.1486 meses; 3p: S/ 33576.83 y 5.7419 meses.

## Limites del dictamen

- Las cifras de costo son **escenarios academicos**, no gastos observados ni cotizaciones.
- La prueba de codigo mide Java productivo, no SQL, HTML, CSS, JavaScript, configuraciones ni tests.
- Esta auditoria no valida los 8 ILF de NESMA ni los 111 movimientos funcionales de COSMIC; dichas decisiones requieren evidencia funcional.
- No aporta horas reales ni firma de un revisor externo. Se mantiene el corte de observaciones real en PARCIAL.
- La ERS definitiva G01 no esta publicada en el repositorio al generar esta version; la medicion funcional es de la linea base provisional identificada.

## Dictamen

Resultado de controles de este script: **6 OK, 0 REVISAR, 0 ERROR**.
Se consideran cerradas solo las comprobaciones aritmeticas cuyos estados son OK.
No se declara aprobacion academica ni integracion a `main`.
