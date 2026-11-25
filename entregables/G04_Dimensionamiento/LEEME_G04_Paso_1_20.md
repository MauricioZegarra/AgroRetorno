# G04 — Paso 1.20 | Instrucciones

1. Descarga `G04_Paso_1_20_Criterios_y_Escenario.zip` y extrae su contenido dentro de `entregables\G04_Dimensionamiento`.
2. Ejecuta desde la raíz del repositorio:

```powershell
$base = "entregables\G04_Dimensionamiento"
py -3 "$base\06_Memoria_Calculo\G04_20_Generar_Escenario_117.py"
```

3. Comprueba la salida `G04 Paso 1.20 OK | maestro=111 candidatos | escenario=117 candidatos | +6 X`, y que el hash maestro coincide con el histórico.
4. Lee `04_Comparaciones\G04_20_Dictamen_22_Focos_y_Escenario.md` y `G04_20_Informe_Escenario_117.md`.
5. Solo si la comprobación es correcta, agrega los archivos G04_20 y el LEEME a tu rama G04; no hagas merge.

El CSV de 117 filas es **una hipótesis metodológica**, no sustituye el CSV de 111 ni el Excel de 37 hojas. Los seis nuevos X requieren comprobar la edición COSMIC aplicable; las X POST-redirect también están sujetas a la frontera adoptada.
