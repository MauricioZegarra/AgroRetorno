# G04 - Paso 1.23 | Auditoria independiente del libro COSMIC 117

**Ejecucion local:** 2026-11-25T00:14:53-05:00  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**HEAD:** `14d5628cfbfc1952757a20934400f005a6d44dcc`  
**SHA-256 libro G04_22:** `4e17024f115d7ec107c40567354f41db27d18ea04dbc63bbe4fcb4c9624de621`  
**SHA-256 CSV G04_22:** `38e9b225f888ac0ba594709273b0cd1591979ade79169aca5016071907e423a8`  
**Resultado:** 32 controles; 32 OK; 0 REVISAR.

| Estado | Control | Evidencia |
|---|---|---|
| OK | Rama G04 | feature/cielo-g04-dimensionamiento-estimacion; HEAD=14d5628cfbfc |
| OK | Integridad CSV maestro | 2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a |
| OK | Integridad libro original | 2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472 |
| OK | Hashes de las nuevas salidas | CSV=38e9b225f888ac0ba594709273b0cd1591979ade79169aca5016071907e423a8; XLSX=4e17024f115d7ec107c40567354f41db27d18ea04dbc63bbe4fcb4c9624de621; coinciden con informe 1.22=True |
| OK | Filas del CSV | maestro=111; modelo interno=117 |
| OK | IDs completos y unicos | unicos=117; M001...M117, secuencia=True |
| OK | Maestro preservado | campos modificados en M001-M111: 0; muestra=[] |
| OK | Distribucion COSMIC CSV | E=21, X=27, R=41, W=28; total=117 |
| OK | Seis Exit adicionales | M112-M117, incorrectos=ninguno |
| OK | Inclusion interna y estado CSV | Contar distinto de Si=[]; estado interno diferente=[] |
| OK | 37 hojas preservadas | origen=37; nuevo=37; nombres/orden coinciden=True |
| OK | Hojas para auditar | Estan las hojas COSMIC, NESMA, COCOMO, comparacion y horas reales |
| OK | 117 filas del XLSX vs CSV | diferencias=0; muestra=[] |
| OK | 117 formulas L de inclusion | formulas incorrectas=0, resultados distintos de 1=0; muestra=[] |
| OK | Sin fila extra M118 | A123=None |
| OK | 92 formulas SUMIFS ampliadas | limite hasta fila 122; formulas con error=0; muestra=[] |
| OK | Total COSMIC_Procesos F30 | observado=21.0; esperado=21 |
| OK | Total COSMIC_Procesos G30 | observado=27.0; esperado=27 |
| OK | Total COSMIC_Procesos H30 | observado=41.0; esperado=41 |
| OK | Total COSMIC_Procesos I30 | observado=28.0; esperado=28 |
| OK | Total COSMIC_Procesos J30 | observado=117.0; esperado=117 |
| OK | COSMIC_Resumen!A7 | valor=117.0; esperado=117 |
| OK | COSMIC_Resumen!G7 | valor=117.0; esperado=117 |
| OK | COMPARACION_Metodos!E8 | valor=117.0; esperado=117 |
| OK | NESMA_Resumen!B16 | valor=131.0; esperado=131 |
| OK | COCOMO_Modelo!B6 | valor=3.491; esperado=3.491 |
| OK | COCOMO_FuenteLOC!D17 | valor=3491.0; esperado=3491 |
| OK | NESMA detalle reproducido | ILF=8; datos=56.0; transacciones=21; puntos=75.0 |
| OK | Hojas ajenas a COSMIC sin ediciones | hojas comparadas=33; formulas modificadas=0; valores inesperados=0; recalculos de formula permitidos=1; muestra formulas=[], valores=[] |
| OK | COCOMO_Comparacion B7: vinculo COSMIC recalculado | esperado 111 -> 117 por =COSMIC_Resumen!A7; encontrado=['COCOMO_Comparacion!B7: 111 -> 117 (formula sin cambios)'] |
| OK | Horas reales sin inventar | filas registradas=0; corte=PARCIAL; sin observaciones finales estimadas |
| OK | Sin errores de celda Excel | errores=0; ejemplos=[] |

## Interpretacion y limites

- Esta auditoria independiente lee los valores y formulas OOXML del XLSX; no ejecuta una segunda vez Excel.
- Verifica integridad de CSV, rangos, caches y correspondencia entre Excel/CSV, y preservacion de otras hojas.
- Los 117 CFP son **modelo interno G03**, no certificacion COSMIC ni validacion de una ERS G01 externa.
- El estado `Validado internamente G03` es una decision interna, NO significa que la edicion oficial del metodo COSMIC haya sido acreditada para cada fila.
- NESMA (131 UFP) y COSMIC (117 CFP) no son unidades convertibles ni deben sumarse.
- No se generan horas reales. Con cero registros, no existe variacion observada de esfuerzo.
- No se modifico ningun libro ni CSV. Solo se escribio este informe de auditoria.

**Dictamen automatico:** CONSISTENCIA TECNICA SIN DISCREPANCIAS
