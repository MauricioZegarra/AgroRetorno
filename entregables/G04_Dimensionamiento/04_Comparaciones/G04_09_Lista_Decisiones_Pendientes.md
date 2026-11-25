# G04 - Paso 1.9 | Registro de decisiones de validacion

**Proyecto:** AgroRetorno  
**Responsable:** Cielo Cristal Meza Vizcarra  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Estado:** Aritmetica comprobable; medicion funcional **preliminar**, pendiente de G01 y revision de otra persona.

## I. Comprobaciones automaticas y sus limites

El script `06_Memoria_Calculo/G04_09_Validar_Libro.py` inspecciona el XLSX **sin modificarlo** y escribe un registro reproducible en `04_Comparaciones/G04_09_Informe_Validacion_Automatica.md`.

- Verifica 37 hojas, subtotales NESMA (56 datos + 75 transacciones = 131 UFP), 111 movimientos COSMIC, parametros y formulas COCOMO, y que las desviaciones de horas reales no se publiquen como finales mientras el corte sea `PARCIAL`.
- Comprueba coherencia aritmetica y valores cacheados, **no** justifica que los procesos y datos sean funcionalmente correctos.
- Si Excel ya contiene registros reales, el script los detecta pero **no** los declara validados automaticamente.

## II. Decision pendiente: NESMA

| ID | Pregunta que debe resolver G01 / equipo | Fuente que hay que contrastar | Estado |
|---|---|---|---|
| N01 | D01: ¿Usuarios + Productores representan un solo archivo logico, o dos? | ERS G01 + Usuario.java + Productor.java | Pendiente |
| N02 | D03: ¿Inventario/Existencias es grupo de datos independiente de Materiales? | Casos de uso, reglas de negocio y modelo SQL | Pendiente |
| N03 | D07: ¿Movimientos de inventario tiene significado funcional propio? | Requisitos de trazabilidad + entidades | Pendiente |
| N04 | D08: ¿Historial de operaciones es archivo logico de negocio o auditoria solo tecnica? | RF14, RF15 y controladores de reporte | Pendiente |
| N05 | ¿Existe EIF mantenido por otra aplicacion? | Frontera funcional de AgroRetorno | Pendiente |
| N06 | ¿RET/DET y complejidades son defendibles con la ERS y campos reconocidos por usuarios? | Hoja NESMA_Datos, NESMA_Reglas | Pendiente |
| N07 | ¿Los P01/P02 de sesion son funciones elementales medibles? | RF01 + Spring Security | Pendiente |
| N08 | ¿P08/P10 y variantes de consulta por rol son funciones distintas o duplicadas? | Controladores y eventos de negocio | Pendiente |
| N09 | ¿EI/EO/EQ, DET y FTR de P03-P23 son correctos? | NESMA_Transacciones + metodos Java | Pendiente |

**No cambiar las casillas `Contar` ni el total 131 UFP sin documentar la decision y el recuento.**

## III. Decision pendiente: COSMIC

- C01 - Verificar 111 movimientos candidatos E/X/R/W; el campo `Estado de revision` (`COSMIC_Movimientos!M`) comienza como `Por validar`.
- C02 - Confirmar el evento disparador de cada proceso P03-P23: una ruta HTTP no implica automaticamente un proceso funcional distinto.
- C03 - Verificar que cada X transporta datos de negocio, no solo una confirmacion de control.
- C04 - Revisar si los R/W de D08 pertenecen al alcance funcional o a logging tecnico.
- C05 - Revisar duplicados por grupo de datos y alternativas por rol.
- C06 - Verificar que las 21 E, 21 X, 41 R y 28 W incluidas dan 111 CFP **solo como hipotesis**.

**No equiparar `COSMIC_Movimientos!K = Si` con `Aprobado`: las 111 filas siguen sujetas a revision funcional.**

## IV. Decision pendiente: COCOMO y costos

| ID | Criterio | Estado |
|---|---|---|
| K01 | Repetir conteo de LOC desde codigo actual de la rama base que adopte el grupo | Pendiente |
| K02 | Explicar limitacion de 3491 lineas de codigo Java en ZIP historico (sin frontend/SQL/pruebas) | Documentado, ratificar |
| K03 | Confirmar hipotesis de modelo organico COCOMO 81 | Pendiente revision externa |
| K04 | Verificar la tarifa ilustrativa S/ 20/h, 160 h/PM, 80 % dedicacion, 15 % coordinacion | Pendiente acuerdo del equipo |
| K05 | Revisar sensibilidad +/-20 % sin confundirlo con un intervalo de confianza | Pendiente inspeccion de formulas |
| K06 | Registrar horas reales de cuatro integrantes con evidencia, sin deducirlas de commits | Pendiente |
| K07 | No publicar desviacion definitiva si `REALES_Resumen!B5 = PARCIAL` | Mantener |

## V. Criterio de cierre G04

- [ ] Guardar salida de auditoria automatica ejecutada sobre el XLSX **actual**.
- [ ] Resolver los criterios N01-N09 con G01 y el equipo.
- [ ] Revisar/justificar los movimientos C01-C06.
- [ ] Confirmar K01-K05; registrar K06 solo si se dispone de datos reales.
- [ ] Registrar fecha, version, nombres y decision del revisor externo.
- [ ] Integrar resultados definitivos, hallazgos y limitaciones al informe G04 Word/PDF.

**No confundir:** que una suma cierre aritmeticamente no significa que el tamanio funcional haya sido aprobado.
