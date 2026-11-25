# G04 - Paso 1.9 | Auditoria automatica y validacion metodologica

## Contenido
- `06_Memoria_Calculo/G04_09_Validar_Libro.py`: verificador de solo lectura del Excel, sin dependencias externas de Python. **No modifica el .xlsx**.
- `04_Comparaciones/G04_09_Lista_Decisiones_Pendientes.md`: criterios que deben contrastarse con G01, los metodos y la linea base de codigo.

## Aplicacion en el repositorio

1. Confirmar rama `feature/cielo-g04-dimensionamiento-estimacion` y trabajo limpio.
2. Extraer este ZIP en `entregables/G04_Dimensionamiento`. No incluye versiones del XLSX ni sobrescribe resultados anteriores.
3. Correr en PowerShell desde la raiz del repo:

```powershell
py -3 "entregables\G04_Dimensionamiento\06_Memoria_Calculo\G04_09_Validar_Libro.py"
```

Si `py` no existe, usar:

```powershell
python "entregables\G04_Dimensionamiento\06_Memoria_Calculo\G04_09_Validar_Libro.py"
```

4. Abrir el informe que se genera:

```powershell
notepad "entregables\G04_Dimensionamiento\04_Comparaciones\G04_09_Informe_Validacion_Automatica.md"
```

5. Si aparece `REVISAR`, documentar y corregir primero; NO aprobar resultados contradictorios.
6. Conservar `G01 pendiente`, `movimientos COSMIC por validar` y `horas reales parciales` mientras corresponda.
7. Ejecutar `git diff --cached --stat` antes de crear commit en la rama G04.

## Importante

- Las 13 imagenes del paso 1.8 fueron **exportaciones automaticas del libro**; no son capturas genuinas de Cielo inspeccionando Excel.
- El script lee valores cacheados del formato XLSX y revisa sumas y formulas. Si Excel cambia valores sin recalcular, conviene abrirlo, recalcular y guardarlo antes de ejecutar esta auditoria.
- Una formula coherente no valida DET, RET, FTR o movimientos COSMIC sin contrastar el alcance con la ERS.
- Las horas reales nunca se inventan ni infieren de commits.
- Sin merge ni PR hasta que la responsable lo autorice.
