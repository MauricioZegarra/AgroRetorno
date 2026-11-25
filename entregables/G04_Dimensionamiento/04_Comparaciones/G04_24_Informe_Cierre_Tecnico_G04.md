# AgroRetorno — G04: Informe de cierre técnico de dimensionamiento y estimación

**Curso:** Calidad de Software, 2026-B
**Responsable:** Cielo Cristal Meza Vizcarra
**Proyecto:** AgroRetorno (cooperativa simulada en Arequipa, Perú)
**Línea base de código:** rama `feature/cielo-g04-dimensionamiento-estimacion`, commit `2a8e6a0e823479c6606dcac17b6f71690404a90c` (25/11/2026)
**Línea base funcional de trabajo:** RF01–RF15 de G03, commit `1fef1d211096d4ac3ba46f6adb16d3a5e5869ef2`
**Estado:** CIERRE TÉCNICO INTERNO AUDITADO; sin conformidad de G01 ni certificación metodológica externa.

## Resumen ejecutivo

La Guía G04 dimensiona funcionalmente AgroRetorno, estima esfuerzo, duración y costos y analiza la sensibilidad de sus hipótesis. Se obtuvieron **131 UFP con NESMA** (56 de funciones de datos y 75 de funciones transaccionales) y se adoptó un **modelo interno COSMIC de 117 CFP** (21 Entry, 27 Exit, 41 Read, 28 Write). Son unidades y técnicas distintas: no se suman ni se convierten directamente.

Para la estimación técnica se contabilizaron **80 archivos Java productivos**, **4 267 líneas físicas** y **3 491 líneas de código aproximadas** (3.491 KLOC). Con COCOMO 81 básico en modo orgánico se calculan **8.91884 persona-mes** y **5.74192 meses nominales**. Bajo supuestos académicos declarados, el escenario de una persona estima **S/ 28,540.30** de costo directo y **11.15 meses**; el de tres personas, **S/ 33,576.83** de costo directo y **5.74 meses**. Los montos no representan una cotización ni gastos observados.

La integración COSMIC 1.22 y la auditoría independiente 1.23 comprobaron la consistencia estructural del Excel de 37 hojas. El resultado de la última auditoría fue **32 controles OK, 0 REVISAR**. El estado de las 117 filas corresponde a **“Validado internamente G03”** como decisión de inclusión en el modelo de trabajo, no como validación COSMIC oficial. No se disponen de horas reales registradas: el corte sigue **PARCIAL**, por lo que no se calcula desviación observada.

## 1. Objetivo y alcance

El objetivo es dimensionar y estimar AgroRetorno de forma reproducible, separando tamaño funcional, tamaño de código, esfuerzo, plazo y costo. La aplicación web utiliza Spring MVC/Thymeleaf, servicios Java, JPA y PostgreSQL. Los usuarios funcionales son PRODUCTOR y ENCARGADO.

La frontera funcional considerada comprende mantenimiento de productores y materiales, gestión y consulta de existencias, registro y evaluación de solicitudes, entrega física, préstamo, devoluciones, historial y reportes. Excluye servicios de infraestructura y herramientas de desarrollo como GitHub, Maven y despliegue. La agrupación lógica de datos se plantea con D01–D08 sobre diez tablas físicas; no se equipara una tabla con un archivo lógico ni una URL con un proceso elemental.

Se utilizó el catálogo RF01–RF15 de G03 como línea base **interna identificada**. No consta conformidad de la ERS definitiva G01; por ello las clasificaciones funcionales son decisiones académicas de trabajo, aunque la aritmética y la trazabilidad técnica hayan sido auditadas.

## 2. Metodología

### 2.1. NESMA: funciones de datos y transaccionales

Se propone un conteo detallado de puntos de función sin ajustar (UFP). Se identificaron ocho archivos lógicos internos (ILF) candidatos, cero interfaces externas (EIF) y 21 procesos transaccionales incluidos de 23 candidatos. Para los datos se emplean DET y RET propuestos; para las transacciones, DET y FTR. La clasificación de trabajo contempla 10 entradas externas (EI), 1 salida externa (EO) y 10 consultas externas (EQ). P01 y P02 se mantienen excluidos del modelo de trabajo.

La hipótesis de los ocho grupos D01–D08 requiere comprobación funcional independiente; especialmente D01 (usuarios/productores), D02 versus D03 (material/existencias) y D07–D08 (movimientos e historial).

### 2.2. COSMIC: movimientos de datos

Se delimitaron 21 procesos funcionales candidatos y sus movimientos Entry (E), Exit (X), Read (R) y Write (W). La primera matriz contenía 111 movimientos. Los pasos G04_11 a G04_21 documentaron trazabilidad Java, revisiones de R/W, E/X, criterios de consultas, salidas tras POST→redirect→GET y grupos de salida.

El escenario **interno** adoptó seis salidas X adicionales: M112–M114 (detalle de solicitud P14), M115 (detalle de préstamo P19) y M116–M117 (historial del productor P21). Así, el modelo pasó de 111 a 117 CFP. Las reglas internas R21-E0 a R21-RW y sus decisiones por fila se guardan en G04_21; su adopción no prueba cumplimiento de una edición concreta del manual COSMIC. No se presupone que cada pestaña o atributo HTML dé lugar automáticamente a una salida independiente.

### 2.3. COCOMO 81 básico, modo orgánico

Se estimó el tamaño de entrada únicamente con Java de producción: 3.491 KLOC aproximadas. Se emplearon los coeficientes académicos a=2.4, b=1.05, c=2.5 y d=0.38.

**Esfuerzo:** `E = 2.4 × KLOC^1.05` (persona-mes).
**Duración nominal:** `TDEV = 2.5 × E^0.38` (meses calendario).

Para escenarios de dotación se asumieron 160 h/PM, 80 % de dedicación, S/ 20/h, y una pérdida de productividad del 15 % por coordinación en el equipo de tres. La regla de planificación adopta `máx(TDEV, esfuerzo ajustado / capacidad del equipo)`; esta regla adicional no forma parte de la ecuación original de COCOMO.

## 3. Resultados de tamaño y esfuerzo

| Método | Resultado | Unidad y condición |
|---|---:|---|
| NESMA, funciones de datos | 56 | UFP; 8 ILF candidatos, 0 EIF |
| NESMA, funciones transaccionales | 75 | UFP; 10 EI, 1 EO, 10 EQ |
| **NESMA, total** | **131** | **UFP del modelo preliminar** |
| COSMIC, Entry | 21 | Movimientos E |
| COSMIC, Exit | 27 | Movimientos X |
| COSMIC, Read | 41 | Movimientos R |
| COSMIC, Write | 28 | Movimientos W |
| **COSMIC, total** | **117** | **CFP del modelo interno G03** |
| Código de producción | 3.491 | KLOC Java aproximadas, 80 archivos |
| COCOMO, esfuerzo | 8.91884 | Persona-mes estimados |
| COCOMO, duración nominal | 5.74192 | Meses estimados |

El incremento de COSMIC respecto de los 111 CFP iniciales es **6 CFP (+6 salidas X)**, una **decisión de modelado interno**, no una revisión certificada. No se usa un factor de conversión NESMA↔COSMIC ni se infiere KLOC a partir de UFP/CFP.

## 4. Costos y cronograma comparados

| Indicador | 1 persona | 3 personas |
|---|---:|---:|
| Dedicación supuesta | 80 % | 80 % |
| Sobrecarga de coordinación | 0 % | 15 % |
| Esfuerzo del escenario | 8.92 PM | 10.49 PM |
| Plazo adoptado | 11.15 meses | 5.74 meses |
| Horas de mano de obra calculadas | 1,427.02 | 1,678.84 |
| Costo directo por horas | S/ 28,540.30 | S/ 33,576.83 |
| Costo por disponibilidad del período | S/ 28,540.30 | S/ 44,097.96 |
| Costo directo + reserva ilustrativa del 10 % | S/ 31,394.33 | S/ 36,934.51 |

La alternativa de tres personas acorta el plazo teórico en el escenario, pero incrementa el esfuerzo agregado por coordinación. Su costo de disponibilidad completa es mayor que el costo de horas productivas, pues el período del proyecto contiene capacidad no utilizada bajo los supuestos establecidos. No se incorporan hosting, licencias, equipos, tributos ni costos indirectos: **no es un presupuesto total**.

El cronograma de referencia distribuye el trabajo en seis fases secuenciales: requisitos 15 %, diseño 18 %, implementación 32 %, integración 10 %, pruebas 18 % y cierre 7 % (total 100 %). Los pesos no equivalen a fechas ni a horas efectivamente observadas.

## 5. Análisis de sensibilidad

Se reprodujeron **12 escenarios** (tres casos por variable) variando de forma aislada el tamaño KLOC, tarifa, dedicación y coordinación con ±20 % de referencia. Los principales resultados son:

| Variable | Escenario inferior | Base | Escenario superior |
|---|---:|---:|---:|
| Tamaño técnico | 2.793 KLOC | 3.491 KLOC | 4.189 KLOC |
| Esfuerzo al variar KLOC | 7.06 PM | 8.92 PM | 10.80 PM |
| Costo directo 3 personas al variar KLOC | S/ 26,563.43 | S/ 33,576.83 | S/ 40,661.18 |
| Tarifa por hora | S/ 16 | S/ 20 | S/ 24 |
| Costo directo 3 personas al variar tarifa | S/ 26,861.46 | S/ 33,576.83 | S/ 40,292.19 |
| Dedicación | 64 % | 80 % | 96 % |
| Plazo con 1 persona al variar dedicación | 13.94 meses | 11.15 meses | 9.29 meses |
| Coordinación equipo de 3 | 12 % | 15 % | 18 % |
| Costo directo 3 personas al variar coordinación | S/ 32,432.16 | S/ 33,576.83 | S/ 34,805.25 |

El cambio de KLOC afecta de manera no estrictamente lineal el esfuerzo porque COCOMO utiliza un exponente 1.05. Variar solo la tarifa modifica el costo monetario, no el esfuerzo. La sensibilidad es una simulación académica, no una predicción calibrada con datos reales del proyecto.

## 6. Control de calidad, reproducibilidad y trazabilidad

La integración G04_22 añadió seis movimientos en una **copia del libro** y preservó el original G04_07. El script amplió las referencias de las 92 fórmulas SUMIFS hasta la fila 122 y recalculó `COSMIC_Procesos`, `COSMIC_Resumen` y la comparación de métodos. El paso G04_23 ejecutó un auditor independiente de solo lectura con **32 controles OK y 0 REVISAR**, que contrastó las 117 filas del CSV con el Excel, los hashes, las 37 hojas, los subtotales, la preservación de NESMA/COCOMO y la ausencia de horas ficticias.

La celda `COCOMO_Comparacion!B7` cambió legítimamente de 111 a 117 por la fórmula `=COSMIC_Resumen!A7`, **sin alterar la fórmula ni el esfuerzo COCOMO**. La auditoría distingue ese recálculo esperado de una edición no prevista.

| Evidencia principal | Resultado y ubicación relativa a G04 |
|---|---|
| Libro auditado G04_22 | `06_Memoria_Calculo/G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx` |
| Matriz COSMIC G03 de 117 filas | `02_COSMIC/G04_22_Movimientos_117_ModeloInterno.csv` |
| Reglas y decisión individual | `04_Comparaciones/G04_21_Dictamen_de_Adopcion_Interna_G03.md` y `G04_21_Decisiones_Internas_117_COSMIC.csv` |
| Informe de auditoría independiente | `04_Comparaciones/G04_23_Informe_Auditoria_Independiente_117.md` |
| Verificador de solo lectura | `06_Memoria_Calculo/G04_23_Auditar_Integracion_117.py` |
| Validación LOC, costos y sensibilidad | `04_Comparaciones/G04_10_Informe_Validacion_Tecnica.md` |

**Integridad de la última copia:** SHA-256 del XLSX G04_22 `4e17024f115d7ec107c40567354f41db27d18ea04dbc63bbe4fcb4c9624de621`; SHA-256 del CSV G04_22 `38e9b225f888ac0ba594709273b0cd1591979ade79169aca5016071907e423a8`. Los resultados son reproducibles sobre esas versiones; cambios posteriores requieren nueva auditoría.

## 7. Horas reales y límites de interpretación

La hoja `REALES_Registro` tiene **0 filas** de trabajo efectivo validado y `REALES_Resumen` conserva el corte **PARCIAL**. Por lo tanto, no se dispone de esfuerzo observado ni se calcula una diferencia entre esfuerzo real y COCOMO. Las fechas o la cantidad de commits **no** permiten inferir horas trabajadas.

**Estado de cierre por dimensión:**

- **Cerrado técnicamente:** integridad del libro y CSV, recuento de código, aritmética NESMA/COSMIC, escenarios de costos y sensibilidad, trazabilidad interna y auditoría de 32 controles.
- **Modelo interno documentado, no certificación:** 131 UFP NESMA y 117 CFP COSMIC dependen de las agrupaciones y reglas académicas adoptadas. Una futura revisión de la ERS G01 o del manual COSMIC puede modificar estos resultados.
- **No disponible:** registros completos de horas reales y de costos observados. Su ausencia se declara como limitación, sin forzar una variación cero ni completar datos ficticios.
- **No acreditado:** firma de conformidad G01, revisión externa del docente o aprobación formal del conteo. No se atribuye ninguno de estos actos.

La conservación de estos límites **no es un error aritmético ni una observación de integridad**, sino una condición de interpretación del alcance de la evidencia disponible.

## 8. Conclusiones y recomendaciones

1. La medición funcional produce dos magnitudes independientes: **131 UFP NESMA**, basados en ocho grupos lógicos y 21 transacciones incluidos, y **117 CFP COSMIC** en el modelo académico interno G03. Los resultados no se suman ni equiparan.
2. El esfuerzo técnico de referencia estimado con COCOMO 81 básico es **8.92 persona-mes** y **5.74 meses nominales**, utilizando **3.491 KLOC Java** aproximadas; esta aproximación no mide todo el software ni representa esfuerzo observado.
3. El contraste de una y tres personas exhibe el intercambio entre plazo y costo: el equipo de tres acorta el calendario teórico, aunque puede elevar el costo total de capacidad si se contrata durante todo el período.
4. La auditoría **32 OK / 0 REVISAR** acredita la consistencia estructural y numérica del Excel de 37 hojas, y preserva las fuentes anteriores mediante hashes y versiones diferenciadas.
5. El cierre de la Guía G04 es **técnico e interno**; para una aceptación formal hacen falta la ERS G01 acordada, el criterio metodológico respaldado y, si se exige comparación de esfuerzo real, registros completos de horas verificables.

## 9. Referencias y evidencias

- Boehm, B. W. (1981). *Software engineering economics*. Prentice-Hall. (Referencia metodológica del modelo COCOMO 81 usada en los materiales G04.)
- Meza Vizcarra, C. C. (2026). *G04_02_Memoria_Conteo_NESMA* [Memoria técnica interna]. Proyecto AgroRetorno, rama G04.
- Meza Vizcarra, C. C. (2026). *G04_21_Dictamen_de_Adopcion_Interna_G03* [Dictamen de modelo de trabajo]. Proyecto AgroRetorno, rama G04.
- Meza Vizcarra, C. C. (2026). *G04_23_Informe_Auditoria_Independiente_117* [Informe de verificación reproducible]. Proyecto AgroRetorno, rama G04.
- Repositorio: `https://github.com/MauricioZegarra/AgroRetorno/tree/feature/cielo-g04-dimensionamiento-estimacion` (commit de cierre técnico de referencia: `2a8e6a0e823479c6606dcac17b6f71690404a90c`).

**Dictamen final de este documento:** CIERRE TÉCNICO INTERNO AUDITADO, con 32 verificaciones satisfactorias y alcance metodológico explícito. No constituye certificación COSMIC, firma de G01 ni validación de horas observadas.
