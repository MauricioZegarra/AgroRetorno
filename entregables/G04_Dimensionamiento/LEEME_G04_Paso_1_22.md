# G04 — Paso 1.22 | Integración segura del modelo COSMIC interno (117 CFP)

## Qué se comprobó previamente

- Rama GitHub: `feature/cielo-g04-dimensionamiento-estimacion`, HEAD `0dee8890fc2b3b164bbcf49c73320e17d6435fca` en el momento de preparación.
- Paso 1.21 publicado: decisiones internas para M001–M117. El modelo 117 es **interno G03**, no una conformidad formal del manual COSMIC ni una aprobación de G01.
- Inspección estructural de un libro de referencia G04_07 de **37 hojas** (una instantánea distinta del binario actual de la rama). Columnas de `COSMIC_Movimientos`: encabezado fila 5, M001–M111 filas 6–116, K=Contar, L=CFP, M=Estado. `COSMIC_Procesos` usa SUMIFS con límite de fila 116; `COSMIC_Resumen!G7` cuenta hasta 116.
- Simulación de las seis adiciones con `artifact_tool` sobre la instantánea de referencia: E=21, X=27, R=41, W=28, 117 CFP, sin errores de fórmulas detectados. **Esta simulación no reemplaza la ejecución local sobre el libro vigente.**

## Instrucciones en Windows PowerShell (raíz de AgroRetorno)

1. Descarga y descomprime `G04_Paso_1_22_Integracion_Segura.zip` desde Descargas:

```powershell
$base = "entregables\G04_Dimensionamiento"
Expand-Archive -LiteralPath "$HOME\Downloads\G04_Paso_1_22_Integracion_Segura.zip" -DestinationPath $base -Force
```

2. **Prevalidación sin escrituras** (requiere estar en tu rama G04):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File "$base\06_Memoria_Calculo\G04_22_Integrar_Modelo_117_Excel.ps1"
```

Debe indicar `G04 1.22 PREVIO OK ... modelo=117`, sin crear archivos. Si falla el SHA del original, **no fuerces el resultado**: el archivo es distinto y necesita revisar la versión antes de tocarlo.

3. Cierra el archivo Excel original en la interfaz de Excel y **aplica sobre una COPIA**. Requiere Microsoft Excel de escritorio instalado:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File "$base\06_Memoria_Calculo\G04_22_Integrar_Modelo_117_Excel.ps1" -Aplicar
```

**Resultado esperado**: `G04 1.22 OK | 37 hojas | E=21 X=27 R=41 W=28 | 117 CFP | NESMA 131 UFP`.

## Archivos generados al aplicar

- `06_Memoria_Calculo/G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx` — copia nueva con 37 hojas y M001–M117. **No sustituye** `G04_07`.
- `02_COSMIC/G04_22_Movimientos_117_ModeloInterno.csv` — CSV nuevo coherente con el libro, 117 filas.
- `04_Comparaciones/G04_22_Informe_Integracion_Excel.md` — SHA256, origen, destino y resultados observados en Excel.

**No cambia** `02_COSMIC/G04_03_Movimientos_COSMIC.csv` ni `06_Memoria_Calculo/G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`. Nunca borres las fuentes originales. Si las salidas ya existen, el script se detiene; no sobrescribe revisiones.

## Significado de «Validado internamente G03»

Es el estado de **decisión de inclusión del modelo académico** según el dictamen `G04_21`: no es certificación COSMIC, no valida la ERS de G01 y no acredita revisión externa. Este alcance se escribe explícitamente en el libro derivado, el CSV y el informe. Si el profesor pide una certificación formal u otra edición de la norma, se requiere una revisión diferente.

## Qué revisar antes de publicar

- Resultado de ejecución y archivos generados.
- `COSMIC_Movimientos`: 117 movimientos, filas nuevas 117–122, estados `Validado internamente G03`.
- `COSMIC_Procesos`: 117 CFP (P14=9, P19=5, P21=7), total X=27.
- `COSMIC_Resumen`: 117 CFP, 117 movimientos.
- `NESMA_Resumen`: 131 UFP sin cambio; COCOMO y registro de horas no alterados.

Al confirmar las cifras con la ejecución real, publicar **solo** los archivos nuevos del Paso 1.22, sin fusionar ramas:

```powershell
git status
git add -- "$base\06_Memoria_Calculo\G04_22_Integrar_Modelo_117_Excel.ps1"
git add -- "$base\06_Memoria_Calculo\G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx"
git add -- "$base\02_COSMIC\G04_22_Movimientos_117_ModeloInterno.csv"
git add -- "$base\04_Comparaciones\G04_22_Informe_Integracion_Excel.md"
git add -- "$base\LEEME_G04_Paso_1_22.md"
git diff --cached --check
git diff --cached --stat
git commit -m "feat(g04): integrar modelo COSMIC interno 117 en copia del Excel"
git push
git status
```

**Nota:** no se ejecutó el código PowerShell/Excel COM en este entorno Linux; la ejecución de la copia vigente y su informe deben verificarse en tu equipo. El script valida antes de aplicar y se niega a usar un binario distinto al hash registrado.
