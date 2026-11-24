# G03 — Análisis de mockups y vinculación con el sistema

**Responsable:** Cielo Cristal Meza Vizcarra  
**Versión de referencia:** rama `feature/cielo-g03-analisis-de-sistemas` de AgroRetorno.  
**Naturaleza del material:** prototipos de presentación basados en campos y operaciones existentes; **no son capturas del producto en ejecución**.

## Objetivo

Documentar cuatro vistas editables que cubren tres casos de uso y refuerzan la comprensión de los estados y los controles del ciclo de préstamo.

| Mockup | CU / RF | Pantalla original de referencia | Correspondencia funcional |
|---|---|---|---|
| M01 Solicitud | CU-01 / RF06 | `productor/solicitud-form.html` | material, cantidad, fecha prevista, motivo, enviar solicitud |
| M02 Revisión | CU-02 / RF07 | `encargado/solicitud-detalle.html` | detalles, disponibilidad, aprobar y rechazar con motivo |
| M03 Entrega | CU-02 / RF08 | `encargado/solicitud-detalle.html` | solo en estado APROBADA, registrar entrega física |
| M04 Devolución | CU-03 / RF09 | `encargado/prestamo-detalle.html` | cantidades por condición, observaciones, saldo y estado |

## Criterios de diseño aplicados

1. **Claridad del estado:** PENDIENTE, APROBADA, ENTREGADA, ACTIVO y CERRADO aparecen con etiquetas consistentes con las entidades reales.
2. **Separación de acciones:** aprobar y entregar están representados en pantallas distintas por fines didácticos; el sistema real usa el mismo detalle con acciones condicionadas por estado.
3. **Validación visible:** se explican cantidad, fecha y motivo en CU-01; existencia de stock en CU-02; y límites/observaciones en CU-03.
4. **Trazabilidad:** cada mockup se vincula con una ruta, un controlador, uno o más servicios y un RF.
5. **Accesibilidad visual básica:** tipografía legible, contraste verde/blanco, estructura por secciones y etiquetas de campo. Debe verificarse adicionalmente si se desea implementar el diseño.
6. **Datos:** todos los números, nombres, referencias y fechas mostrados son **ficticios**; no provienen de la base de datos desplegada.

## Diferencias respecto de la implementación

- El estilo visual es un rediseño de carácter académico, no una réplica pixel a pixel de Bootstrap/Thymeleaf.
- El mockup M03 presenta una pantalla conceptual exclusiva de «Entrega» para explicar el estado APROBADA. En la implementación, el botón aparece dentro de `encargado/solicitud-detalle.html`.
- Los botones en HTML son representaciones estáticas: **no** envían datos ni modifican PostgreSQL.
- Los valores de disponibilidad y préstamo son escenarios ilustrativos y no evidencian una ejecución real.

## Instrucciones de revisión

1. Abrir cada `.html` desde `04_Mockups/` en Chrome o Edge; los archivos funcionan sin conexión.
2. Verificar cada pantalla frente a su plantilla Thymeleaf de referencia y a las reglas de negocio documentadas.
3. Si se cambia texto o estilo, editar `.html` y `G03_05_Estilos_Mockups.css`.
4. Si se realiza una revisión propia, tomar una captura real del navegador y guardarla en `05_Capturas`.
5. Conservar también los PNG generados para insertar en el SDD y la sustentación.

**Limitación:** este paso es elaboración de artefactos de diseño, no validación formal por usuarios ni prueba automatizada.
