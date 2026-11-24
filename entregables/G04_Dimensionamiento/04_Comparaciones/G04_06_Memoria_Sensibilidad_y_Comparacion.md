# G04 — Paso 1.6 | Análisis de sensibilidad y comparación metodológica

**Proyecto:** AgroRetorno  
**Responsable:** Cielo Cristal Meza Vizcarra  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Referencia verificada en GitHub:** `6f525460e47d647f30c8a8a7f51063f0675191a9` (último commit de G04 previo a este paso)  
**Estado:** análisis de escenarios preparado; **valores preliminares, pendientes de validación con la ERS de G01 y registros reales**.

## 1. Objetivo y alcance

Se estudia la sensibilidad del modelo académico COCOMO 81 básico (modo orgánico) y sus escenarios de planificación frente a cambios controlados de **±20 %** en cuatro parámetros: tamaño técnico Java (KLOC), tarifa por hora, dedicación por persona y coordinación del equipo de tres personas. Se estudia una variable cada vez, manteniendo las demás constantes.

Se comparan las salidas de **NESMA, COSMIC y COCOMO sin sumar sus unidades ni afirmar una conversión universal**.

## 2. Entradas y trazabilidad

| Origen | Variable base | Valor | Condición |
|---|---|---:|---|
| `COCOMO_Parametros!B11` | Java KLOC | 3.491 | Conteo aproximado de Java productivo del ZIP v1.0.0 |
| `COCOMO_Parametros!B7:B10` | Parámetros orgánicos a/b/c/d | 2.4 / 1.05 / 2.5 / 0.38 | Modelo académico de COCOMO 81 básico |
| `COCOMO_Parametros!B13` | Tarifa | S/ 20.00 / h | Supuesto, no cotización |
| `COCOMO_Parametros!B14` | Dedicación | 80 % | Supuesto de planificación |
| `COCOMO_Parametros!B15` | Coordinación, 3 personas | 15 % | Pérdida de productividad supuesta |
| `NESMA_Resumen!B16` | NESMA | 131 UFP | Conteo no aprobado |
| `COSMIC_Resumen!A7` | COSMIC | 111 CFP | Movimientos no validados |

**Limitación del conteo KLOC:** el archivo de referencia cuenta únicamente Java productivo, no HTML, CSS, JavaScript, SQL, configuración, pruebas o dependencias externas. Los gastos de infraestructura y licencias no están presupuestados.

## 3. Fórmulas empleadas

- **Esfuerzo COCOMO:** `E = 2.4 × KLOC^1.05`, en persona-meses.
- **Duración nominal:** `TDEV = 2.5 × E^0.38`, en meses.
- **Costo directo, 1 persona:** `E × 160 h/PM × tarifa`.
- **Esfuerzo bruto, 3 personas:** `E / (1 − coordinación)`.
- **Costo directo, 3 personas:** `E / (1 − coordinación) × 160 × tarifa`.
- **Plazo, 1 persona:** `max(TDEV, E / dedicación)`.
- **Plazo, 3 personas:** `max(TDEV, [E/(1 − coordinación)] / [3 × dedicación])`.

Las fórmulas son las mismas reglas de planificación adoptadas en el Paso 1.5. La tasa de coordinación del 15 % se modela como **pérdida de productividad** (divisor `1 − tasa`), y no como un recargo multiplicativo del 15 %.

La regla del máximo usa el TDEV nominal como cota inferior de planificación en este ejercicio; **no debe interpretarse como una ley física del desarrollo de software**. No hay calibración estadística específica de AgroRetorno.

## 4. Escenario base

| Indicador | Valor |
|---|---:|
| Esfuerzo | 8.92 persona-meses |
| Duración COCOMO nominal | 5.74 meses |
| Plazo aproximado con 1 persona | 11.15 meses |
| Plazo aproximado con 3 personas | 5.74 meses |
| Costo directo, 1 persona | S/ 28,540.30 |
| Costo directo, 3 personas | S/ 33,576.83 |

**Todos los montos son costos directos ilustrativos de mano de obra; no equivalen al costo completo ni real del proyecto.**

## 5. Sensibilidad ±20 %: resultados

### 5.1. Tamaño de código (KLOC)

| Escenario | KLOC | Esfuerzo PM | TDEV meses | Costo directo 3p |
|---|---:|---:|---:|---:|
| −20 % | 2.793 | 7.06 | 5.25 | S/ 26,563.43 |
| Base | 3.491 | 8.92 | 5.74 | S/ 33,576.83 |
| +20 % | 4.189 | 10.80 | 6.18 | S/ 40,661.18 |

Cambiar KLOC un ±20 % modifica esfuerzo aproximadamente **−20.89 % / +21.10 %**, y también cambia la duración, debido a los exponentes de COCOMO. No supone que NESMA ni COSMIC cambien en la misma proporción.

### 5.2. Tarifa por hora

La tarifa ±20 % (S/ 16 a S/ 24) produce un costo directo 3p de **S/ 26,861.46** a **S/ 40,292.19**, manteniendo los persona-meses y el TDEV del modelo.

### 5.3. Dedicación

Al variar la dedicación desde 80 % hasta 64 % y 96 % (±20 % relativo), el plazo de una persona cambia de **13.94** a **9.29 meses**. El costo por horas productivas del modelo no cambia porque no se han alterado las horas-persona; el costo por contratación de tiempo completo sí podría modificarse en otro presupuesto.

Para tres personas, el piso nominal TDEV puede limitar el plazo, de modo que no siempre habrá un cambio visible en el cronograma adoptado.

### 5.4. Coordinación de tres personas

La coordinación pasa del 15 % base a 12 % y 18 %. Los costos directos para el equipo de tres son **S/ 32,432.16** y **S/ 34,805.25**. La variación no es exactamente ±3 % de costo porque opera dentro de un denominador (`1 − coordinación`).

## 6. Comparación de métodos

| Método | Resultado provisional | Unidad | Alcance principal | Qué NO significa |
|---|---:|---|---|---|
| NESMA | 131 | UFP | Tamaño funcional basado en datos lógicos y transacciones | No equivale a 131 CFP |
| COSMIC | 111 | CFP | Movimientos funcionales de datos E/X/R/W | No equivale a 111 UFP |
| COCOMO tamaño de entrada | 3.491 | KLOC | Código Java productivo aproximado | No es un conteo de todo el sistema |
| COCOMO esfuerzo | 8.92 | persona-meses | Esfuerzo del modelo empírico | No es la duración de calendario |
| COCOMO duración nominal | 5.74 | meses | Duración teórica del modelo | No es un plazo contractual garantizado |

Las diferencias numéricas entre 131 UFP y 111 CFP no deben tratarse como un porcentaje de error ni como una diferencia de tamaño comparable. Las tres técnicas responden a preguntas distintas.

## 7. ¿Cómo mostrarlo en la sustentación?

1. Mostrar `COMPARACION_Metodos` y preguntar: **¿cuánto mide nuestro software y en qué unidad?** Aclarar que cada método observa un aspecto diferente.
2. Mostrar `SENSIBILIDAD_Resumen` y cambiar una variable en una copia del Excel: el costo cambia con la tarifa, pero el esfuerzo no.
3. Mostrar `SENSIBILIDAD_Detalle`: explicar que una variación de KLOC del 20 % se traduce en un efecto no estrictamente lineal.
4. Comparar equipo de 1 y 3 personas: una mayor plantilla no divide automáticamente el plazo por tres, pues intervienen dedicación y coordinación.
5. Terminar con las limitaciones y pasos de validación: los conteos de NESMA/COSMIC necesitan acuerdo de la ERS y el costo requiere datos reales.

## 8. Control de calidad y pendientes

- [ ] Comprobar que las 34 pestañas estén presentes en Excel o LibreOffice.
- [ ] Ejecutar una variación de ±20 % sobre una copia y observar las fórmulas.
- [ ] Revisar que el valor base en `SENSIBILIDAD_Detalle` coincida con COCOMO y costos anteriores.
- [ ] Validar conteos NESMA y COSMIC con la ERS de G01.
- [ ] Registrar evidencia real en `05_Capturas` (captura de tu Excel abierto).
- [ ] Recoger las **horas realmente trabajadas** por los integrantes, sin inventarlas.
- [ ] Actualizar bitácora y dejar pendiente el cierre formal hasta la revisión.

## 9. Fuentes de reproducción

- Código y documentación de AgroRetorno: https://github.com/MauricioZegarra/AgroRetorno/tree/feature/cielo-g04-dimensionamiento-estimacion
- Hojas vinculadas: `NESMA_Resumen`, `COSMIC_Resumen`, `COCOMO_Modelo`, `COCOMO_Parametros`, `COSTOS_Escenarios` y `COSTOS_Supuestos`.
- Referencia metodológica de COCOMO 81: Boehm, B. W. (1981). *Software Engineering Economics*. Prentice-Hall.
- Guía académica G04 del trabajo final: dimensionamiento funcional, COCOMO, sensibilidad ±20 % y comparación contra esfuerzo observado.
