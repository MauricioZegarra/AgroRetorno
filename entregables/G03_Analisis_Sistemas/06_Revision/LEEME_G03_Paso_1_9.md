# G03 — Paso 1.9 · Evidencias y revisión local

1. Cerrar Word/LibreOffice/Excel y comprobar `git status` y rama G03.
2. Copiar `Verificar-G03.ps1` y `G03_09_Acta_Revision_Local.md` a `entregables/G03_Analisis_Sistemas/06_Revision`.
3. Ejecutar desde la raíz de AgroRetorno: `powershell -NoProfile -ExecutionPolicy Bypass -File "entregables\G03_Analisis_Sistemas\06_Revision\Verificar-G03.ps1"`
4. Revisar el resultado en pantalla y `G03_09_Auditoria_Local.txt`. Si hay incidencias, no marcarlas como conformes.
5. Abrir archivos para inspección: repetir el comando anterior añadiendo `-AbrirVistas`, o abrirlos individualmente desde Explorador/VS Code.
6. Tomar capturas **reales** con `Win+Shift+S`; guardarlas dentro de `05_Capturas`, con nombres descriptivos.
7. Completar el acta, registrar herramientas y versiones realmente utilizadas, fecha, commit, hallazgos y dictamen.
8. Realizar commit de evidencias y documentación en la rama G03, sin merge.

Este script **no** prueba funciones de AgroRetorno ni interpreta el SDD. Solo comprueba existencia de archivos, rótulos RF en SVG y posible texto en partes de encabezados/pies del DOCX. Las verificaciones visuales siguen siendo manuales.
