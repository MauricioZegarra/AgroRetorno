# G04 — Paso 1.24 | Publicación del informe de cierre técnico interno

**Rama correcta:** `feature/cielo-g04-dimensionamiento-estimacion`. No fusionar ramas.

## Contenido

- `04_Comparaciones/G04_24_Informe_Cierre_Tecnico_G04.md`: versión Markdown para GitHub.
- `04_Comparaciones/G04_24_Informe_Cierre_Tecnico_G04.docx`: informe académico editable de seis páginas.
- `04_Comparaciones/G04_24_Informe_Cierre_Tecnico_G04.pdf`: versión PDF de seis páginas.
- `README.md`: actualización del estado vigente de G04; conserva el paso 1.9 como registro histórico.
- `LEEME_G04_Paso_1_24.md`: estas instrucciones.

## Extracción

El ZIP contiene rutas relativas a `entregables/G04_Dimensionamiento`. Extraer con `Expand-Archive ... -DestinationPath $base -Force` desde la raíz del repositorio. Este paso **actualiza** `README.md`, por lo que verificar el diff antes de publicar. No cambia XLSX ni CSV anteriores.

## Antes del commit

1. Comprobar la rama y `git status`.
2. Abrir el DOCX o PDF para inspección.
3. Verificar `git diff -- README.md` y `git diff --check`.
4. Agregar los archivos G04_24, README y LEEME; ejecutar `git diff --cached --check`.
5. Confirmar commit y `git push`, sin merge.

## Alcance

**117 CFP** y **131 UFP** siguen siendo resultados de trabajo según la línea base interna G03, no aprobaciones de COSMIC, G01 o el docente. La auditoría G04_23 **32 OK y 0 REVISAR** verifica estructura, fórmulas y consistencia del libro; no autentica horas reales que no existen.
