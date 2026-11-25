# Lista de revisión NESMA — G04 Paso 1.2

**Responsable:** Cielo Cristal Meza Vizcarra. **Estado:** pendiente de completar con evidencias verificadas.

- [ ] Contrastar RF01–RF15 con ERS de G01 (anotar versión y responsable).
- [ ] Confirmar frontera única de AgroRetorno y servicios externos.
- [ ] Identificar ILF reconocidos y mantenidos; evaluar todos los candidatos D01–D08.
- [ ] Confirmar si existe algún EIF real, mantenido por otra aplicación.
- [ ] Enumerar RET/DET con fuentes de negocio para cada archivo lógico.
- [ ] Revisar todas las funciones transaccionales P01–P23, fusionando/descartando las redundantes.
- [ ] Justificar EI, EO, EQ y FTR/DET, citando rutas y comportamiento de código.
- [ ] Revisar fórmulas y pesos del método aplicado.
- [ ] Completar ejemplos manuales de al menos un ILF, un EI y un EQ/EO.
- [ ] Recalcular y registrar total UFP validado.
- [ ] Conservar libro editado, capturas de revisión y constancia de quien revisó.

## Registro de observaciones

| Fecha | Elemento | Observación | Decisión tomada | Evidencia | Revisor |
|---|---|---|---|---|---|
| Pendiente | D01 | Validar agrupación Usuarios + Productores | Sin resolver | Entidades Java | Pendiente |
| Pendiente | D07/D08 | Confirmar reconocimiento como grupos lógicos | Sin resolver | Código + ERS | Pendiente |
| Pendiente | P01/P02 | Validar si cuentan como procesos elementales | Sin resolver | SecurityConfig | Pendiente |

**No marque este checklist como aprobado sin revisar los criterios con la ERS y las reglas de medición.**

<!-- G04-PASO19-AUDITORIA-REAL -->
## Paso 1.9 - Resultado de auditoria real
- Auditoria automatica: consistente para el modelo actual de 37 hojas.
- NESMA: 8 ILF incluidos (56 UFP) y 21 transacciones incluidas (10 EI, 1 EO, 10 EQ; 75 UFP).
- Total calculado de forma independiente: 131 UFP PRELIMINARES.
- No se han validado los DET, RET, FTR, los grupos D01/D07/D08 ni la decision EIF.
- P01/P02 y la frontera requieren contraste con la ERS definitiva de G01.
- Las casillas de validacion funcional anteriores permanecen pendientes.
- Evidencia: G04_09_Salida_Real.txt y G04_09_Informe_Validacion_Automatica.md.
