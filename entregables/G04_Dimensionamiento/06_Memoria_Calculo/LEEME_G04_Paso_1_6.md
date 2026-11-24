# AgroRetorno G04 · Paso 1.6 — Sensibilidad y comparación

Responsable: **Cielo Cristal Meza Vizcarra**.

Este paquete conserva las **30 hojas anteriores** y agrega 4 hojas:
`SENSIBILIDAD_Detalle`, `SENSIBILIDAD_Resumen`,
`COMPARACION_Metodos` y `REVISION_Paso16`.

## Distribución de los archivos

- `04_Comparaciones/G04_06_Memoria_Sensibilidad_y_Comparacion.md` — memoria técnica.
- `04_Comparaciones/G04_06_Checklist_Revision.md` — lista de validación.
- `04_Comparaciones/G04_06_Resultados_Sensibilidad.csv` — escenarios numéricos exportados.
- `04_Comparaciones/G04_06_Grafico_Costo_3p.png` — gráfico comparativo.
- `04_Comparaciones/G04_06_Grafico_Plazo_1p.png` — gráfico comparativo.
- `04_Comparaciones/G04_06_Vista_Tablero_Sensibilidad.png` — vista de hoja de cálculo.
- `06_Memoria_Calculo/G04_06_NESMA_COSMIC_COCOMO_Sensibilidad_Comparacion.xlsx` — libro Excel con 34 hojas.

## Nota de consistencia

Base del libro: G04.5, versión publicada en `feature/cielo-g04-dimensionamiento-estimacion`,
commit `6f525460e4`, antes de elaborar este nuevo paso.

Las fórmulas del nuevo paso enlazan **directamente** con el libro anterior y sus
resultados. Las cifras son estimaciones académicas; las clasificaciones
funcionales NESMA/COSMIC siguen pendientes de validación por la ERS.

**Nota:** los gráficos PNG son versiones visuales aparte;
el libro conserva tablas numéricas y fórmulas editables.
