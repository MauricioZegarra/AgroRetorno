# G04 — Paso 1.6 | Checklist para revisión de Cielo

**Fecha de preparación:** 23/11/2026  
**Estado inicial:** PENDIENTE DE REVISIÓN LOCAL

| ID | Control | Evidencia prevista | Estado |
|---|---|---|---|
| V01 | Abrir el Excel G04_06 (34 hojas) | Captura de Excel/LibreOffice | Pendiente |
| V02 | Contrastar base 131 UFP con NESMA | Vista de NESMA_Resumen | Pendiente con G01 |
| V03 | Contrastar base 111 CFP con COSMIC | Vista de COSMIC_Resumen | Pendiente con G01 |
| V04 | Verificar 3.491 KLOC y fuente Java | COCOMO_FuenteLOC y script | Pendiente |
| V05 | Recalcular ±20 % KLOC | SENSIBILIDAD_Detalle, filas 7-9 | Pendiente |
| V06 | Recalcular ±20 % tarifa | SENSIBILIDAD_Detalle, filas 10-12 | Pendiente |
| V07 | Recalcular ±20 % dedicación | SENSIBILIDAD_Detalle, filas 13-15 | Pendiente |
| V08 | Recalcular ±20 % coordinación | SENSIBILIDAD_Detalle, filas 16-18 | Pendiente |
| V09 | Comprobar títulos y unidades de todos los valores | COMPARACION_Metodos | Pendiente |
| V10 | Registrar horas reales del equipo | COCOMO_HorasReales | Pendiente |
| V11 | Guardar una captura genuina de revisión local | 05_Capturas | Pendiente |
| V12 | Actualizar bitácora y commit de la guía | Bitacora.md / GitHub | Pendiente |

## Precauciones

- Las imágenes incluidas en el ZIP son **vistas generadas del modelo**, no evidencia de haber abierto el archivo en tu computadora.
- **No marcar como aprobados** 131 UFP, 111 CFP o los costos hasta validar los supuestos con G01 y fuentes propias.
- Las horas reales se registran solo a partir de evidencias auténticas.
- No convertir directamente UFP a CFP ni tratar COCOMO como resultado observado.

<!-- G04-PASO19-AUDITORIA-REAL -->
## Paso 1.9 - Alcance de la auditoria real
- La auditoria de 11 controles NO prueba escenarios de sensibilidad +/-20 por ciento ni sus recalculos.
- NESMA=131 UFP y COSMIC=111 CFP son resultados preliminares en unidades distintas.
- Mantener V02/V03 pendientes de G01; V04 pendiente de reconteo HEAD; V05-V09 pendientes de inspeccion de formulas.
- No marcar V10 (horas reales) ni V11 (captura genuina) como completados.
