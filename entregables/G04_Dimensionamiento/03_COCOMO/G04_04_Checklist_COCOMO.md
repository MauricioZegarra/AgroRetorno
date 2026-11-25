# G04 — Lista de revisión COCOMO

- [x] Identificar variante del modelo y coeficientes de estimación.
- [x] Separar tamaño KLOC, esfuerzo PM, TDEV y costo S/.
- [x] Registrar tamaño del código con evidencia de origen y script reproducible.
- [x] Proporcionar escenarios de 1 y 3 personas con dedicación y coordinación.
- [x] Preparar sensibilidad de KLOC ±20 %.
- [x] Mantener fórmulas y supuestos monetarios editables.
- [ ] Volver a contar LOC en la rama final y documentar cualquier diferencia con el ZIP.
- [ ] Corroborar si la base de Java cubre todo el alcance estimado o extenderlo.
- [ ] Justificar/validar tarifa, dedicación, horas por persona-mes y sobrecarga.
- [ ] Obtener horas reales del equipo con fechas y evidencia (no inventar).
- [ ] Validar RF/NESMA/COSMIC con la ERS del grupo.
- [ ] Obtener revisión formal del ingeniero o integrante revisor.

**Estado:** ejemplo numérico consistente, **NO APROBADO** como presupuesto definitivo.

<!-- G04-PASO19-AUDITORIA-REAL -->
## Paso 1.9 - Resultado de auditoria real
- Entrada historica: 3491 lineas Java productivas = 3.491 KLOC (ZIP historico; NO HEAD actualizado).
- Formula COCOMO 81 basica en modo organico comprobada aritmeticamente.
- Esfuerzo: 8.918844313 PM. Duracion nominal: 5.741921757 meses.
- Pendiente recontar LOC sobre la linea base final y documentar exclusiones de HTML/CSS/JS, SQL y configuracion.
- Tarifa, dedicacion, horas por PM, coordinacion y escenarios son supuestos; no son costos reales.
- Faltan horas observadas y validacion externa.
- Evidencia: G04_09_Informe_Validacion_Automatica.md.
