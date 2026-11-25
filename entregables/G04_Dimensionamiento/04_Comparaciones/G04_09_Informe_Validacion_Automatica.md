# G04 - Paso 1.9 | Auditoria automatica del libro

**Proyecto:** AgroRetorno
**Responsable:** Cielo Cristal Meza Vizcarra
**Origen:** `G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`
**SHA256 del Excel comprobado:** `2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472`
**Resultado:** 0 controles REVISAR y 1 observaciones de validacion.

| Estado | Comprobacion | Hallazgo aritmetico |
|---|---|---|
| OK | Estructura del libro | 37 hojas detectadas (modelo esperado: 37) |
| OK | NESMA archivos logicos | 8 candidatos; 8 ILF incluidos; 56 puntos preliminares de datos |
| OK | NESMA transacciones | 23 procesos candidatos; 21 incluidos; EI=10, EO=1, EQ=10; subtotal 75 UFP |
| OK | NESMA total | Libro=131.0 UFP; comprobacion independiente=131 UFP |
| OK | COSMIC movimientos | 111 candidatos, 111 incluidos, 21 procesos; E=21 X=21 R=41 W=28, 111 CFP |
| OBSERVACION | COSMIC revision funcional pendiente | Estados declarados en el libro: {'Por validar': 111}. No aprobar movimientos sin decisiones documentadas. |
| OK | COSMIC resumen | Resumen=111.0 CFP; suma de filas incluidas=111 CFP |
| OK | COCOMO base Java | Líneas de codigo en libro=3491.0; KLOC=3.491; fuente ZIP historica, no HEAD de GitHub |
| OK | COCOMO esfuerzo y duracion | E_libro=8.918844313068579 PM, E_recalculado=8.91884 PM; T_libro=5.741921756543081 meses, T_recalculado=5.74192 |
| OK | COCOMO formulas | B9=B7*B6^B8 ; B12=B10*B9^B11 |
| OK | Horas y cobertura | Estado del corte=PARCIAL; filas con datos=0; desviaciones finales sin publicar=True |

## Limitaciones y pendientes
- NESMA sigue siendo preliminar: validar DET, RET, FTR, D01/D07/D08, EIF y procesos P01/P02 con G01.
- COCOMO estima una base de Java productivo (80 archivos, 3491 lineas de codigo estimadas). Se excluyen HTML/CSS/JS, SQL, configuraciones y pruebas.
- No hay horas efectivas registradas en REALES_Registro. No deducir duracion de fechas de commits.
- Los 131 UFP y los 111 CFP son unidades diferentes y no se convierten ni suman entre si.
- Todas las cifras son de un modelo de trabajo. La aprobacion funcional requiere G01 y revision del equipo.
- Esta auditoria solo comprueba estructura, valores cacheados y coherencia aritmetica; no ejecuta macros ni valida requisitos de usuario.

## Dictamen

**Consistencia aritmetica:** Sin discrepancias en los controles aplicados.
**Validacion funcional definitiva:** PENDIENTE G01, decisiones NESMA/COSMIC y revision externa.
**Horas reales del equipo:** registrar solo tiempo realmente trabajado y documentado.

**Nota:** Esta salida automatica no es una certificacion del docente ni reemplaza la inspeccion manual de formulas.
