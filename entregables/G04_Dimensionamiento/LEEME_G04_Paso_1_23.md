# G04 — Paso 1.23 | Auditoría independiente del Excel 117 CFP

Este paso revisa el libro **sin editarlo**, usando únicamente Python 3 estándar. Su salida es `04_Comparaciones/G04_23_Informe_Auditoria_Independiente_117.md`.

## Requisitos

- Haber publicado el Paso 1.22 (rama `feature/cielo-g04-dimensionamiento-estimacion`).
- Tener los libros G04_07 y G04_22 y los CSV G04_03 y G04_22 en sus rutas habituales.
- No se necesita abrir Excel ni instalar paquetes Python.

## Ejecución desde la raíz de AgroRetorno

```powershell
$base = "entregables\G04_Dimensionamiento"
py -3 "$base\06_Memoria_Calculo\G04_23_Auditar_Integracion_117.py"
notepad "$base\04_Comparaciones\G04_23_Informe_Auditoria_Independiente_117.md"
```

La auditoría comprobará: hashes de fuentes y salidas, 117 filas del CSV, igualdad con el libro, 117 fórmulas `L6:L122`, 92 fórmulas SUMIFS, resúmenes, 37 hojas, 131 UFP NESMA, COCOMO, hojas no COSMIC sin ediciones, horas reales y ausencia de errores Excel.

**Si muestra `REVISAR`: no hacer commit todavía.** Compartir el informe para resolver exactamente la discrepancia. El auditor no cambia datos.

Si muestra `0 REVISAR`, publicar solo el script, este README y el informe generado:

```powershell
git add -- "$base\06_Memoria_Calculo\G04_23_Auditar_Integracion_117.py"
git add -- "$base\04_Comparaciones\G04_23_Informe_Auditoria_Independiente_117.md"
git add -- "$base\LEEME_G04_Paso_1_23.md"
git diff --cached --check
git diff --cached --stat
git commit -m "test(g04): auditar independientemente Excel COSMIC 117"
git push
git status
```

**Límite:** una auditoría aritmética, estructural y de integridad no certifica que todos los movimientos cumplen COSMIC ni implica conformidad de G01. La adopción de 117 CFP y estados `Validado internamente G03` es una decisión interna documentada.
