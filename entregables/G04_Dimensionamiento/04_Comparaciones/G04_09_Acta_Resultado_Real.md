# G04 - Paso 1.9: resultado de auditoria reproducible

**Proyecto:** AgroRetorno
**Responsable del registro:** Cielo Cristal Meza Vizcarra
**Fecha de este registro:** 2026-11-24 22:13:27 (hora local del equipo)
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`
**Commit base del registro:** `cb7e69dcdb85070d9acaac9fa1f0455499964212`
**Libro auditado:** `G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`
**SHA256 del libro:** `2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472`

## Resultado observado

| Control | Resultado | Alcance de la conclusion |
|---|---|---|
| Estructura del libro | OK: 37 hojas | Coincide con estructura esperada |
| NESMA archivos | OK: 8 ILF candidatos incluidos, 56 UFP | Conteo provisional, no validacion de DET/RET |
| NESMA transacciones | OK: 21 de 23, 10 EI / 1 EO / 10 EQ, 75 UFP | Clasificacion funcional pendiente de G01 |
| NESMA total | OK: 131 UFP | Total consistente, NO APROBADO funcionalmente |
| COSMIC movimientos | OK: 111 E/X/R/W (21/21/41/28) | Todos los movimientos son candidatos |
| COSMIC revision | OBSERVACION: 111 "Por validar" | Validar uno por uno, sin aprobacion automatica |
| COSMIC total | OK: 111 CFP | Consistente aritmeticamente, NO APROBADO |
| COCOMO LOC | OK: 3491 LOC Java = 3.491 KLOC | Referencia historica ZIP, no HEAD final |
| COCOMO modelo | OK: 8.918844313 PM; 5.741921757 meses | Formula nominal correcta, no prediccion observada |
| COCOMO formulas | OK: formulas base en celdas B9 y B12 | No se auditaron todos los escenarios comerciales |
| Horas reales | OK: 0 filas, PARCIAL | Sin desviacion final valida |

**Resumen:** 11 controles; 10 OK; 1 OBSERVACION; 0 REVISAR.

## Pendientes de validacion funcional
1. NESMA: DET/RET/FTR, D01/D07/D08, EIF y P01/P02.
2. COSMIC: revisar 111 movimientos y decisiones individuales, trazabilidad y descartes de logging/duplicados.
3. COCOMO: actualizar LOC frente a fuente final y declarar exclusiones.
4. Costos/sensibilidad: revisar supuestos, escenarios y formulas por separado.
5. Horas reales: solicitar registros reales fechados, con evidencia, y conservar PARCIAL hasta cobertura total.
6. G01: contrastar la ERS definitiva y solicitar revision externa.

## Trazabilidad
- Salida de ejecucion: `G04_09_Salida_Real.txt`.
- Reporte del verificador: `G04_09_Informe_Validacion_Automatica.md`.
- Script usado: `../06_Memoria_Calculo/G04_09_Validar_Libro.py`.
- Excel con las formulas: `../06_Memoria_Calculo/G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`.

**Dictamen:** coherencia aritmetica comprobada para los 11 controles; validacion funcional y aprobacion academica pendientes. No se inventan horas ni capturas personales.
