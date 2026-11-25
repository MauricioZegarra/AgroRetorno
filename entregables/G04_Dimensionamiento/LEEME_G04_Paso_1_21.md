# G04 — Paso 1.21 | Modelo interno de 117 movimientos

**No modifica los archivos maestros de G04.** El libro auxiliar `04_Comparaciones/G04_21_Matriz_Decisiones_117_G03.xlsx` presenta los 117 movimientos, una hoja de reglas, un resumen con fórmulas y los 22 focos. El CSV y el informe de ejecución deben producirse en tu propio repositorio (con hashes reales) con el siguiente script.

Desde la raíz de AgroRetorno, estando en `feature/cielo-g04-dimensionamiento-estimacion`:

```powershell
$base = 'entregables\G04_Dimensionamiento'
py -3 "$base\06_Memoria_Calculo\G04_21_Consolidar_Modelo_Interno_117.py"
notepad "$base\04_Comparaciones\G04_21_Informe_Decisiones_Internas_117.md"
Start-Process "$base\04_Comparaciones\G04_21_Matriz_Decisiones_117_G03.xlsx"
```

Salida esperada:

```text
G04 1.21 OK | decisiones internas=117/117 | E=21 X=27 R=41 W=28 | +6 X
```

El script **no cambia** el CSV maestro (`02_COSMIC/G04_03_Movimientos_COSMIC.csv`), el XLSX principal de 37 hojas ni sus estados. La decisión de trabajo (117) es interna bajo RF01–RF15 de G03; no es validación externa COSMIC ni conformidad G01.

Una vez comprobado el resultado:

```powershell
git add -- entregables/G04_Dimensionamiento/04_Comparaciones/G04_21_*
git add -- entregables/G04_Dimensionamiento/06_Memoria_Calculo/G04_21_Consolidar_Modelo_Interno_117.py
git add -- entregables/G04_Dimensionamiento/LEEME_G04_Paso_1_21.md
git diff --cached --check
git diff --cached --stat
git commit -m "docs(g04): adoptar modelo interno COSMIC 117 con reglas trazables"
git push
git status
```
