# G04 - Paso 1.22 | Integracion de modelo interno G03 en copia XLSX

**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit de base:** `0dee8890fc2b3b164bbcf49c73320e17d6435fca`  
**Fuente XLSX original:** `G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`  
**SHA-256 XLSX original:** `2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472`  
**Nuevo XLSX (sin sobrescribir original):** `G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx`  
**SHA-256 XLSX nuevo:** `4e17024f115d7ec107c40567354f41db27d18ea04dbc63bbe4fcb4c9624de621`  
**Nuevo CSV derivado:** `G04_22_Movimientos_117_ModeloInterno.csv`  
**SHA-256 CSV derivado:** `38e9b225f888ac0ba594709273b0cd1591979ade79169aca5016071907e423a8`

## Resultado comprobado en Excel de escritorio

- Hojas del libro: **37**, conservadas.
- COSMIC: **117 movimientos** con decision de inclusion interna G03.
- E=21, X=27, R=41, W=28; **117 CFP de trabajo**.
- Se agregaron M112-M117 en filas 117-122 con formulas de CFP individuales.
- SUMIFS de COSMIC_Procesos ampliados de fila 116 a 122; COSMIC_Resumen y COMPARACION_Metodos recalculados.
- NESMA_Resumen!B16: **131 UFP**, preservados.
- Estados de revision: **Validado internamente G03**; no implican certificacion COSMIC, aval G01 ni pruebas externas.
- El maestro CSV de 111 y el XLSX G04_07 originales quedaron **intactos**.

## Interpretacion

117 CFP es el **modelo interno adoptado** en G04_21. Persisten los limites de
validez metodologica establecidos en ese dictamen (GET sin datos, POST-redirect-GET,
agrupacion de salidas y grupos R/W). Las verificaciones automaticas de este paso
acreditan coherencia estructural y aritmetica del XLSX, no certificacion COSMIC.