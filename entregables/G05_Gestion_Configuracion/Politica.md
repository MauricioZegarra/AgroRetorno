# G05 | Política de configuración

## Repositorios y líneas base

GitHub aloja el repositorio oficial. La rama `feature/ower-g05-gestion-de-configuracion` contiene la política, el historial y el respaldo de G05. La práctica de configuración usa un repositorio SVN FSFS local con acceso `file://`.

| Ruta SVN | Uso |
|---|---|
| `trunk/AgroRetorno` | Trabajo integrado. |
| `branches/CR-01-demo-opt-in` | Cambio y prueba aislados. |
| `tags/v1.0.0` | Instantánea seleccionada de la práctica. Política de etiqueta de solo lectura. |

| Elemento de configuración | Regla |
|---|---|
| Java, recursos web, configuración, migraciones y Maven | Versionar archivos fuente y descriptor de compilación con la revisión. |
| ERS, análisis, solicitudes y trazabilidad | Identificar requisito, revisión, aprobación y evidencia. |
| Pruebas y resultados seleccionados | Vincular el caso y el entorno con la revisión probada. |
| Respaldo SVN | Publicar dump comprimido, SHA-256 y resultados de una restauración independiente. |
| Secretos y datos | Usar datos sintéticos. Excluir secretos operativos, copias de la base, archivos temporales y compilaciones. |

SemVer distingue cambios incompatibles, funciones compatibles y correcciones. La etiqueta `v1.0.0` identifica la instantánea SVN de laboratorio. Se crea después de la integración y la repetición de pruebas. Su ruta de origen y la relación con los archivos de Git se registran en el informe G05.

## Flujo de un cambio

1. Registrar solicitud, alcance y análisis. [Registro de cambios de G02](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g02-proceso-de-requerimientos/entregables/G02_Proceso_Requerimientos/Cambios.md).
2. Crear una copia SVN desde `trunk/AgroRetorno` a `branches/<cambio>`.
3. Revisar diff, ejecutar pruebas y registrar una revisión con cambios verificables.
4. Integrar a trunk, inspeccionar conflictos y resolverlos dejando los tres estados.
5. Volver a ejecutar pruebas y seleccionar una revisión para etiquetar.
6. Restaurar un dump en un repositorio nuevo y cotejar historial y código.

## Comparación

| Aspecto | Apache Subversion | Git | GitHub |
|---|---|---|---|
| Modelo | Centralizado; revisión secuencial del repositorio. | Distribuido; cada clon conserva historia y usa objetos por hash. | Plataforma de alojamiento y colaboración para Git. |
| Aislamiento | Copia barata mediante `svn copy` y merge con rutas. | Ramas como referencias y merge por historial. | Gestiona ramas, permisos y revisiones de código en remoto. |
| Recuperación | `checkout -r`, dump/load y `svnadmin verify`. | Clon, bundle o checkout de commit según el objetivo. | Copias en remoto y controles que administra el equipo. |
| Uso en esta entrega | Requisito de laboratorio y práctica FSFS local. | Historial oficial y rama de entregables. | Repositorio oficial para revisión del equipo. |

## Herramientas y compatibilidad

Se verificaron `svn` y `svnadmin` **1.14.5** con su salida de versión en Debian 13. La referencia Windows del avance reporta 1.7.8. Para el respaldo de esta ejecución se usa un cliente y servidor compatibles con SVN 1.14 o posterior.

TortoiseSVN integra el Explorador de Windows. La ejecución actual se hizo en Linux con la CLI y queda pendiente evidencia de instalación, versión y una operación en Windows para el paso 1.17. En un equipo Windows, abrir **TortoiseSVN → About** y registrar la versión exacta del cliente. Hacer un checkout o update sobre una copia de práctica y conservar su salida junto a la versión. [Procedimiento Windows](TortoiseSVN.md).
