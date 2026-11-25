# G04 — Paso 1.5. Costos y cronogramas de AgroRetorno

**Responsable:** Cielo Cristal Meza Vizcarra  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Estado:** escenario académico reproducible, pendiente validación final.  
**Fecha de preparación:** 23 de noviembre de 2026.

## 1. Propósito y alcance

Comparar la planificación y el costo directo de desarrollo de **AgroRetorno** bajo dos escenarios: **una persona** y **tres personas**, usando el esfuerzo COCOMO 81 del paso 1.4 y supuestos explícitos de tarifa, dedicación y coordinación. No se intenta reconstruir el calendario histórico del equipo ni elaborar una cotización profesional. NESMA y COSMIC siguen siendo mediciones funcionales independientes y no se suman a KLOC.

## 2. Datos de partida y procedencia

| Magnitud | Valor inicial | Origen / advertencia |
|---|---:|---|
| KLOC Java productivo | 3.491 | Conteo aproximado documentado en G04.4 a partir de `AgroRetorno-v1.0.0.zip`; excluye frontend, SQL y pruebas. |
| E, esfuerzo base | 8.9188 persona-mes | `E = 2.4 × KLOC ^ 1.05`, COCOMO 81 básico, modo orgánico. |
| TDEV, duración nominal | 5.7419 meses | `TDEV = 2.5 × E ^ 0.38`. |
| Horas por persona-mes | 160 | Hipótesis académica editable. |
| Dedicación disponible | 80% | Hipótesis académica editable. |
| Tarifa por hora | S/ 20 | Hipótesis de ejercicio. **NO es tarifa de mercado verificada**. |
| Sobrecarga para equipo de 3 | 15% | Hipótesis de coordinación, aplicada como reducción de productividad. |
| Reserva de incertidumbre | 10% | Escenario adicional; **no pertenece a COCOMO**. |

**Fuente metodológica:** Boehm, B. W. (1981). *Software Engineering Economics*, para las ecuaciones básicas en modalidad orgánica. Los supuestos locales son construidos para este ejercicio, no obtenidos de cotizaciones ni de registros históricos.

## 3. Ecuaciones reproducibles

- Esfuerzo nominal: `E = 2.4 × 3.491^1.05 = 8.9188 PM`.
- Duración nominal: `TDEV = 2.5 × E^0.38 = 5.7419 meses`.
- Esfuerzo bruto de tres personas: `E3 = E/(1−0.15) = 10.4928 PM`.
- Capacidad mensual: `n × dedicación × 160` horas.
- Plazo por capacidad: `PM brutos/(n × dedicación)` meses.
- **Plazo adoptado**: `MAX(TDEV, plazo por capacidad)`. Así evitamos el error de dividir `TDEV` directamente entre 3.
- Horas productivas: `PM brutos × 160`.
- Costo directo por horas productivas: `horas productivas × S/20`.
- Costo por disponibilidad completa: `n × dedicación × 160 × plazo adoptado × S/20`.
- Reserva ilustrativa: `10% × costo directo`.

## 4. Resultados comparativos

| Métrica | 1 persona | 3 personas |
|---|---:|---:|
| Plazo adoptado | 11.15 meses | 5.74 meses |
| Esfuerzo de escenario | 8.92 PM | 10.49 PM |
| Horas productivas | 1,427.02 h | 1,678.84 h |
| Costo por horas productivas | S/ 28,540.30 | S/ 33,576.83 |
| Costo de disponibilidad | S/ 28,540.30 | S/ 44,097.96 |
| Uso de capacidad calendario | 100.0% | 76.1% |
| Directo + reserva 10% | S/ 31,394.33 | S/ 36,934.51 |

**Interpretación:** La opción de tres personas disminuye el plazo relativo, pero por la sobrecarga de coordinación aumenta el esfuerzo y el costo por horas productivas. Bajo un contrato de disponibilidad durante todo el periodo, hay capacidad no aprovechada en el escenario de tres personas; por eso su costo de contratación completa es superior al costo por horas productivas. La utilidad del modelo es contrastar escenarios, no decidir automáticamente cuántas personas contratar.

**Exclusiones:** Hosting, licencias, infraestructura, equipos, tributos, gestión y otros costos externos permanecen **sin presupuestar**. Las celdas correspondientes están vacías deliberadamente: *vacío no significa gasto cero*. Por ello el costo directo **no debe llamarse costo total de AgroRetorno**.

## 5. Cronograma relativo

La hoja `CRONOGRAMA_Fases` divide el trabajo en seis fases **secuenciales**, con pesos ilustrativos que suman 100%: requisitos 15%, diseño 18%, implementación 32%, integración 10%, pruebas 18% y cierre 7%. No se introducen fechas históricas ficticias. Cada fase muestra inicio, fin, duración y horas para 1 y 3 personas; al modificar los pesos, la hoja recalcula la distribución. Es un modelo académico simplificado: el equipo real puede trabajar en paralelo.

## 6. Guía de auditoría del Excel

1. Abrir `G04_05_NESMA_COSMIC_COCOMO_Costos_Cronogramas.xlsx`.
2. Revisar `COSTOS_Supuestos` para localizar entradas y procedencia. Las celdas amarillas son hipótesis editables.
3. Revisar `COSTOS_Escenarios`, comparando modalidad de pago por horas con pago por disponibilidad.
4. Abrir `CRONOGRAMA_Fases` y comprobar que **C13 = 100%** y que **H13 / K13** coinciden con los plazos globales.
5. Consultar `COSTOS_Resumen` y sus dos gráficos, disponibles para exposición.
6. Modificar temporalmente la reserva en `COSTOS_Supuestos!C15` y observar los costos con reserva; restaurar el 10%.
7. No llenar `COCOMO_HorasReales` sin evidencia real. Esa comparación corresponde a otro paso.
8. Guardar una captura de **tu Excel realmente abierto** en `05_Capturas` y documentar la revisión en la bitácora.

## 7. Limitaciones y pendientes

- Las 3.491 KLOC fueron calculadas con una fuente limitada al Java productivo, no con un conteo total multilenguaje.
- Los coeficientes COCOMO y la selección del modo son aproximaciones de carácter docente; el proyecto real utiliza Spring Boot moderno y no se ha calibrado el modelo.
- Tarifas, dedicación, coordinación, contingencia y reparto de fases son **supuestos**, nunca resultados observados.
- Pendiente obtener las horas reales del equipo y los datos reales de costos, si existen.
- Pendiente validar con el docente la selección del modo, los costos aceptables y la versión de código de referencia.
- La comparación NESMA/COSMIC/COCOMO se realizará en el siguiente paso con las tres unidades separadas.

## 8. Archivos que acompañan este paso

- `06_Memoria_Calculo/G04_05_NESMA_COSMIC_COCOMO_Costos_Cronogramas.xlsx`
- `04_Comparaciones/G04_05_Vista_Resumen_Costos.png` (**vista del Excel generado**, no una captura de revisión humana)
- `04_Comparaciones/G04_05_Escenarios_Resumen.csv`
- `04_Comparaciones/G04_05_Cronograma_Fases.csv`
- `04_Comparaciones/G04_05_Checklist_Costos_Cronogramas.md`
- `04_Comparaciones/G04_05_Memoria_Costos_Cronogramas.md` (este archivo)

No efectuar merge todavía; trabajar exclusivamente en la rama individual de G04.
