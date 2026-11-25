# G03 — Paso 1.12 | Corrección focalizada de trazabilidad ED-02

**Rama requerida:** `feature/cielo-g03-analisis-de-sistemas` (sin merge). El script reconoce las versiones existentes y realiza únicamente las correcciones que faltan.

## Hallazgo verificado en GitHub
- La matriz general `G03_06_Matriz_RF_CU_Codigo_Interfaz.csv` contiene RF10 → ED-02.
- La matriz detallada `G03_05_Matriz_Trazabilidad.csv` no cuenta con una fila específica para ED-02/RF10.
- La especificación Markdown ED-02 omite RF10 del campo «Requisitos vinculados», aunque documenta el rechazo.
- El DOCX de los tres casos detallados se comprueba localmente y, si conserva la numeración antigua CU-01/CU-02/CU-03 y RF06/RF07/RF08/RF09, se normaliza a ED-01/ED-02/ED-03 y RF07/RF09-RF11/RF13.

## Cómo ejecutarlo
1. Activar la rama G03 después de confirmar que G04 quedó limpio (`git status`).
2. Extraer el ZIP con destino `entregables/G03_Analisis_Sistemas`.
3. Ejecutar `py -3 entregables/G03_Analisis_Sistemas/06_Revision/G03_12_Conciliar_Casos_RF.py`. Es solo lectura.
4. Si el control previo da OK, ejecutar el mismo comando con `--aplicar`.
5. Abrir en Word el DOCX de casos y examinar índice, títulos y tabla de requisitos de los tres casos detallados. Comprobar con `git diff --check`, `git diff --stat` y el acta de revisión.
6. Publicar solo cuando el DOCX se vea bien. El script NO crea un PDF nuevo ni altera el PDF o DOCX del SDD.

**Requisito local:** Python 3 y paquete `python-docx` (`py -3 -m pip install python-docx` si falta). El script guarda una copia de los cuatro archivos previos en una carpeta temporal de respaldo **fuera** del repositorio.

**Límites:** Es un cierre de consistencia interna, no una conformidad con la ERS de G01 ni una validación docente. No se debe crear PR ni hacer merge en este paso.
