# Verificación de G05 con TortoiseSVN

**Estado** · Procedimiento preparado para Windows. La ejecución existente de G05 acredita Subversion 1.14.5 por CLI en Linux. La versión instalada de TortoiseSVN y su comprobación gráfica requieren evidencia de una sesión Windows.

## Preparación

1. Obtener TortoiseSVN para la arquitectura de Windows y Subversion CLI compatible con repositorios de SVN 1.14. Verificar origen y versión. [Descargas oficiales de TortoiseSVN](https://tortoisesvn.net/downloads.html).
2. Abrir **TortoiseSVN → About**. Capturar la versión de TortoiseSVN y la de su biblioteca SVN. Guardar la salida de `svn --version --quiet` y `svnadmin --version --quiet` por separado.
3. Restaurar el dump con [el procedimiento G05](README.md#respaldo-portable) en una ruta local nueva. Verificar SHA-256, revisión 12 y etiqueta `v1.0.0`.

## Demostración y registros

| Operación | Acción en Windows | Resultado esperado |
|---|---|---|
| Explorar | Abrir **Repo-browser** en `file:///C:/ruta/agroretorno-restaurado`. | Aparecen trunk, branches y tags. |
| Checkout | En una carpeta distinta, ejecutar **SVN Checkout** con URL terminada en `/tags/v1.0.0` y HEAD. | Se obtiene la versión etiquetada y se muestra la revisión consultada. |
| Update e historial | Ejecutar **SVN Update** en la copia. Abrir **Show log** sobre el repositorio y localizar el merge r9 y la etiqueta r11. | La copia sigue disponible y los dos hitos son visibles. |
| Recuperar | Hacer checkout de `trunk/AgroRetorno` en otra carpeta y seleccionar r3 en **Revision**. | Se obtiene la versión antigua en la carpeta elegida, preservando la copia de la etiqueta. |

| Evidencia | Qué debe quedar visible | Estado |
|---|---|---|
| WIN-01 | Ventana About y versiones exactas. | Pendiente de ejecución. |
| WIN-02 | Checkout y Update, con URL local y revisión. | Pendiente de ejecución. |
| WIN-03 | Log de r9 y r11 y checkout histórico de r3. | Pendiente de ejecución. |
