# G04 Paso 1.8 — Exportaciones automáticas de las hojas del libro

**Proyecto:** AgroRetorno
**Responsable del trabajo:** Cielo Cristal Meza Vizcarra
**Naturaleza de las imágenes:** EXPORTACIONES AUTOMÁTICAS DEL LIBRO; NO CAPTURAS DE LA PANTALLA PERSONAL.
**Estado de la validación:** Preliminar. NESMA, COSMIC, parámetros COCOMO y ERS definitiva G01 están pendientes de revisión.

## Fuente exacta
- Archivo: `G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`
- Repositorio: https://github.com/MauricioZegarra/AgroRetorno
- Rama: `feature/cielo-g04-dimensionamiento-estimacion`
- Commit fuente de referencia: `2a652b885581`
- Git blob SHA1: `759a574e982e8c6cd0ef138372c9c650c52a0882`
- SHA256 del .xlsx: `748344cd9a60aa09d24afc0d553405c1a370bd68323ac4dbf6ea4caf2a8d1e0a`

Se contrastó el Git blob SHA1 del libro extraído con la versión en el repositorio: coincidencia exacta.
No se escribieron datos nuevos al archivo fuente.

## Procedimiento de exportación

Las imágenes 01 a 12 se renderizaron de forma automatizada a partir de las celdas,
valores y estilos del `.xlsx`, conservando las pestañas reales del libro.
La imagen 13 es una representación tabular legible de los valores de
`REALES_Resumen` porque la renderización automática de sus celdas combinadas
no era legible.

Estas imágenes sirven para DOCUMENTAR EL CONTENIDO del libro. No acreditan por sí solas
que Cielo haya abierto Excel en su laptop, comprobado fórmulas o validado conteos
manualmente. Para demostrar revisión personal, adjuntar además una captura auténtica
con Excel abierto en el equipo y el acta de comprobación propia.

## Archivos y origen

| Archivo | Hoja y región fuente |
|---|---|
| `G04_08_01_Resumen.png` | `Resumen!A1:H19` |
| `G04_08_02_NESMA_Datos.png` | `NESMA_Datos!A1:K17` |
| `G04_08_03_NESMA_Transacciones.png` | `NESMA_Transacciones!A1:J18` |
| `G04_08_04_NESMA_Resumen.png` | `NESMA_Resumen!A1:H22` |
| `G04_08_05_COSMIC_Movimientos.png` | `COSMIC_Movimientos!A1:K19` |
| `G04_08_06_COSMIC_Resumen.png` | `COSMIC_Resumen!A1:G23` |
| `G04_08_07_COCOMO_FuenteLOC.png` | `COCOMO_FuenteLOC!A1:H19` |
| `G04_08_08_COCOMO_Modelo.png` | `COCOMO_Modelo!A1:G22` |
| `G04_08_09_COSTOS_Resumen.png` | `COSTOS_Resumen!A1:H18` |
| `G04_08_10_SENSIBILIDAD_Resumen.png` | `SENSIBILIDAD_Resumen!A1:G19` |
| `G04_08_11_COMPARACION_Metodos.png` | `COMPARACION_Metodos!A1:G17` |
| `G04_08_12_REALES_Registro.png` | `REALES_Registro!A1:K15` |
| `G04_08_13_REALES_Resumen.png` | `REALES_Resumen (tabla fiel de valores de A5:B16; formato adaptado)` |

## Limitaciones y decisiones

- 131 UFP, 111 CFP, 3.491 KLOC y 8.9188 PM son valores preliminares del libro.
- `REALES_Registro` no contiene horas efectivas de integrantes en la versión fuente.
- `REALES_Resumen!B5` conserva `PARCIAL`.
- No se debe presentar ninguna imagen como una captura personal genuina, validación externa,
  aceptación de G01 o medición de horas reales inexistentes.
- No convertir arbitrariamente UFP a CFP ni interpretar estimaciones como resultados observados.
- No se necesita cargar este archivo a otro servicio para obtener estas exportaciones.
