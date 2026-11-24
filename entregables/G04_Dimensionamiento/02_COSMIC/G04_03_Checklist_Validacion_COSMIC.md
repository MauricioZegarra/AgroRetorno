# G04 — Paso 1.3 · Checklist de validación COSMIC
**Responsable:** Cielo Cristal Meza Vizcarra

## A. Frontera y requisito
- [ ] Confirmar frontera con ERS definitiva de G01 (mismo alcance usado en NESMA).
- [ ] Verificar si P01 y P02 deben medirse y justificar el tratamiento.
- [ ] Confirmar que P03–P23 son procesos funcionales diferenciables, no solamente rutas HTTP.
- [ ] Identificar el usuario funcional y el evento disparador de cada proceso.
- [ ] Definir grupos de datos independientes reconocibles por el usuario; justificar D01–D08.

## B. Movimientos y pesos
- [ ] Revisar las 111 filas candidatas E/X/R/W; ninguna queda aprobada solo por tener una fórmula.
- [ ] Verificar cada E y X como movimiento de grupo de datos por la frontera.
- [ ] Descartar acuses simples de confirmación sin salida de datos funcionales.
- [ ] Comprobar R y W por grupo lógico, no por método JPA ni por consulta SQL.
- [ ] Evaluar si los movimientos a D08 son requisitos funcionales o logging técnico.
- [ ] Comprobar que una consulta con join no se desdoble sin motivo.
- [ ] Revisar duplicados entre P08/P10, P18 y entre roles.
- [ ] Mantener `COSMIC_Movimientos!K` como decisión Sí/No, con justificación documentada.
- [ ] Confirmar la edición del manual COSMIC utilizada y referenciarla.

## C. Libro y evidencias
- [ ] Abrir Excel y comprobar que `COSMIC_Procesos` suma correctamente los movimientos.
- [ ] Cambiar una decisión Sí→No y constatar el recálculo; restaurar o conservar según revisión.
- [ ] Verificar que el resultado sea CFP y que NESMA permanezca en UFP.
- [ ] Revisar `COSMIC_Resumen` y confirmar que no hay errores de fórmula.
- [ ] Guardar capturas reales del Excel abierto con fórmulas visibles.
- [ ] Registrar decisiones y resultados en `Bitacora.md`.
- [ ] Guardar el archivo `.xlsx`, esta memoria `.md`, el CSV y la vista de resumen en GitHub.
- [ ] Registrar fecha, revisor, observaciones, versión medida y estado final.

**ESTADO:** preparado para revisión; NO APROBADO.
