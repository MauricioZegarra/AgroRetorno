# G04 — Paso 1.19 · Instrucciones

**1.** Descarga `G04_Paso_1_19_Consolidacion.zip` en Descargas.

**2.** Abre PowerShell dentro de `C:\Users\usuario\Documents\AgroRetorno` y ejecuta:

```powershell
$base = "entregables\G04_Dimensionamiento"
Expand-Archive -LiteralPath "$HOME\Downloads\G04_Paso_1_19_Consolidacion.zip" -DestinationPath $base -Force
py -3 "$base\06_Memoria_Calculo\G04_19_Consolidar_COSMIC_111.py"
notepad "$base\04_Comparaciones\G04_19_Informe_Consolidacion_111_COSMIC.md"
```

**3.** Verifica en pantalla `111 conciliadas | 89 propuestas | 22 por resolver` y que aparezca el commit local correcto. Si el script detecta discrepancias, **detente y reporta el ERROR**; no edites el libro.

**4.** El XLSX del paquete es una matriz auxiliar editable de **3 hojas**, NO reemplaza ni modifica el XLSX original de **37 hojas**.

**5.** Publica SOLO tras revisar el reporte generado:

```powershell
git status
git add -- entregables/G04_Dimensionamiento
git diff --cached --check
git diff --cached --stat
git commit -m "docs(g04): conciliar 111 movimientos COSMIC y aislar 22 decisiones"
git push
git status
```

No hagas merge. No cambies por lote `Por validar` a `Validado` hasta resolver la clasificación funcional.
