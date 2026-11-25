# G04 - Dimensionamiento y Estimacion

Responsable: Cielo Cristal Meza Vizcarra
Proyecto: AgroRetorno
Curso: Calidad de Software - 2026-B

## Objetivo
Medir el tamano funcional y estimar el esfuerzo, la duracion y el costo de AgroRetorno mediante NESMA, COSMIC y COCOMO, documentando supuestos, comparaciones y evidencia verificable.

## Estado vigente - cierre tecnico interno G04 (25/11/2026)

**Estado: CIERRE TECNICO INTERNO AUDITADO.** No equivale a conformidad formal G01, certificacion COSMIC ni revision externa.

- NESMA: **131 UFP del modelo de trabajo** (8 ILF / 56 UFP de datos; 21 transacciones / 75 UFP).
- COSMIC: **117 CFP del modelo interno G03** (E=21, X=27, R=41, W=28). El conteo historico inicial fue 111 CFP; las seis X adicionales se documentan en los pasos 1.20 y 1.21.
- COCOMO 81 basico, modo organico: **3.491 KLOC Java aproximadas**, **8.91884 persona-mes**, **5.74192 meses nominales**.
- Costo directo ilustrativo: **S/ 28,540.30** con 1 persona y **S/ 33,576.83** con 3 personas; hipotesis de tarifa, dedicacion y coordinacion explicitadas.
- Analisis de sensibilidad: **12 escenarios** reproducidos y 6 fases de cronograma con pesos que suman 100 %.
- Integridad del Excel de **37 hojas**: **32 OK, 0 REVISAR**, segun auditoria independiente G04_23.
- Horas reales: **0 filas registradas**, corte **PARCIAL**. No se informa desviacion observada.
- No hubo merge a main. La rama es `feature/cielo-g04-dimensionamiento-estimacion`.

## Entregables principales

- `01_NESMA/` - memoria, checklist y conteo funcional NESMA.
- `02_COSMIC/G04_22_Movimientos_117_ModeloInterno.csv` - movimientos y estados internos.
- `03_COCOMO/` - conteo Java, modelo y sus supuestos.
- `04_Comparaciones/` - costos, cronogramas, sensibilidad y auditorias de los pasos 1.9-1.24.
- `04_Comparaciones/G04_24_Informe_Cierre_Tecnico_G04.md` - fuente del informe de cierre.
- `04_Comparaciones/G04_24_Informe_Cierre_Tecnico_G04.docx` - informe editable.
- `04_Comparaciones/G04_24_Informe_Cierre_Tecnico_G04.pdf` - informe para lectura/impresion.
- `06_Memoria_Calculo/G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx` - libro actualizado de 37 hojas.
- `06_Memoria_Calculo/G04_23_Auditar_Integracion_117.py` - auditor independiente de solo lectura.

## Alcance del cierre

La consistencia tecnica y aritmetica quedo verificada. Las decisiones de inclusion COSMIC son internas para el catalogo RF01-RF15 de G03; no se atribuye aprobacion a una ERS G01 no publicada. La clasificacion NESMA sigue basada en hipotesis explicitas de DET, RET, FTR y grupos logicos. El resultado 117 CFP no es una certificacion del metodo COSMIC. Las horas reales y costos observados no se fabrican.

## Registro historico (paso 1.9)

La auditoria original del libro G04_07 registro 11 controles, 10 OK y 1 OBSERVACION, 0 REVISAR, cuando el modelo COSMIC tenia 111 CFP preliminares. Ese estado historico fue reemplazado para efectos de **integridad del modelo interno** por las verificaciones G04_22 y G04_23; sus archivos y hashes se conservan sin sobreescritura.

## Evidencias y bitacora

Consultar `Bitacora.md`, los dictamenes G04_21, el informe de integracion G04_22, el informe de auditoria G04_23 y el informe de cierre G04_24. Las exportaciones automaticas de `05_Capturas/Exportaciones_Automaticas` no son capturas personales tomadas en el equipo.
