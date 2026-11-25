# G04 — Paso 1.19 | Preconciliación de los 111 movimientos COSMIC

**Proyecto:** AgroRetorno. **Rama:** `feature/cielo-g04-dimensionamiento-estimacion`.  
**Último commit de GitHub revisado:** `b46153c5f91ba6e3cba5f2f1e063019601bd5e35`.  
**Base RF:** G03 `1fef1d211096d4ac3ba46f6adb16d3a5e5869ef2` (no se atribuye aprobación de G01).  
**Fuentes:** G04_14 (111 filas), G04_17 (69 R/W), G04_18 (42 E/X); CSV maestro G04_03 de 111 movimientos.

## Conciliación de evidencia

| Control | Resultado |
|---|---:|
| Movimientos M001–M111, sin duplicados ni huecos | 111 |
| Procesos funcionales candidatos | 21 |
| R/W con propuesta de conservación sustentada en código | 69 |
| E/X con propuesta de conservación sustentada en código | 20 |
| E/X que requieren decidir un criterio metodológico específico | 22 |
| Trazabilidad Java textual previamente comprobada (paso 1.12) | 111/111 |
| Valor de trabajo sin ajustes funcionales | 111 CFP preliminares |

**89 propuestas de conservación NO son 89 aprobaciones COSMIC.** El estado `Validado` solo es defendible para la **localización de la referencia Java**; no se puede trasladar automáticamente al movimiento funcional. El trabajo de las 22 excepciones no significa que las otras 89 tengan ya validación funcional individual definitiva.

## Las cinco decisiones metodológicas críticas

| Categoría | Cantidad | IDs | Regla de cierre requerida |
|---|---:|---|---|
| X_POST_REDIRECT | 10 | M005, M010, M019, M024, M033, M044, M064, M069, M079, M096 | Determinar si los datos flash constituyen salida funcional X de la transacción, contabilizándola una sola vez en el flujo POST→redirect→GET |
| E_FILTRO_OPCIONAL | 6 | M011, M025, M034, M049, M102, M105 | Identificar la Entry disparadora y el grupo que llega con y sin filtro |
| E_SESION_SIN_PAYLOAD | 2 | M045, M097 | Documentar datos de entrada del usuario funcional aunque el controlador use identidad de sesión; no deducir automáticamente de Authentication |
| E_VARIANTES_ROL | 1 | M080 | Definir si el proceso común para PRODUCTOR/ENCARGADO tiene un único evento funcional con variantes o dos procesos distinguibles |
| X_VARIOS_GRUPOS | 3 | M058, M087, M101 | Determinar grupos de salida por objeto de interés y si es necesario separar uno en varias X |

### Evidencia adicional verificada en las plantillas Thymeleaf

- **M058 / P14:** `encargado/solicitud-detalle.html` muestra solicitud, datos del productor, disponibilidad y préstamo. El simple número de objetos del controlador no equivale automáticamente al número de X; estudiar objetos de interés y grupos de datos.
- **M087 / P19:** `encargado/prestamo-detalle.html` muestra el préstamo y una tabla de devoluciones. Debe verificarse si son grupos de salida independientes.
- **M101 / P21:** `productor/historial.html` separa solicitudes, préstamos y devoluciones en pestañas y tablas. Se requiere delimitar grupos de datos de salida; no suponer una sola X porque es una sola vista.

## Alcance de la matriz adjunta

La matriz CSV y el libro XLSX **son una preconsolidación de los dictámenes previamente publicados**; las decisiones `NO EMITIDA` son deliberadas. El libro contiene `Resumen`, `Matriz_111` y `Focos_22`, con columna editable para dictamen interno. No sustituye el libro maestro de 37 hojas.

**Para tener una auditoría con hashes reales del repositorio local**, ejecutar `06_Memoria_Calculo/G04_19_Consolidar_COSMIC_111.py`. El script validará el CSV maestro y los CSV de los pasos 1.17/1.18 en esa máquina y generará `G04_19_Informe_Consolidacion_111_COSMIC.md` y un CSV de conciliación reproducible. **No altera** el CSV maestro `G04_03_Movimientos_COSMIC.csv`, `COSMIC_Movimientos!K/M`, la hoja de resumen, los 111 CFP ni ningún XLSX.

## Para el siguiente cierre

1. Fijar versión y criterios de COSMIC para Entry/Exit, objeto de interés y frontera funcional.
2. Confrontar las 22 decisiones con plantillas/requisitos; si una salida representa varios grupos, ajustar movimientos y recontar, sin forzar que permanezcan 111.
3. Completar también el examen funcional individual de las otras 89 propuestas.
4. Solo entonces escribir estados finales con motivación y mantener CSV y libro de 37 hojas coherentes, previa copia de seguridad y validación automatizada.

**Conclusión:** conciliación documental de 111 filas completa; clasificación funcional definitiva y 111 CFP finales aún no acreditados.
