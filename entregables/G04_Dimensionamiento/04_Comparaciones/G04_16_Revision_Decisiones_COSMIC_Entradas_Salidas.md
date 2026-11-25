# G04 — Paso 1.16 | Dictamen de entradas/salidas y control de validación COSMIC

**Proyecto:** AgroRetorno  
**Rama revisada:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit de GitHub revisado:** `1382e102234a28aa015d1ae77cade3756bb93d8c`  
**Línea base funcional:** RF01–RF15 de G03, commit `1fef1d211`; no se atribuye aprobación a G01.  
**Fuente:** código Java de `src/main/java/pe/edu/agroretorno/` y movimientos `M001`–`M111` de G04.  
**Naturaleza:** revisión técnica basada en código, no certificación formal del método COSMIC.

## 1\. Resultado consolidado de las auditorías 1.9–1.15

* Estructura del libro auditada: 37 hojas.
* NESMA: 8 ILF candidatos (56 UFP), 21 transacciones incluidas (75 UFP); total **131 UFP preliminares**, coherente aritméticamente.
* COSMIC: 111 candidatos distribuidos en E=21, X=21, R=41 y W=28. Los 111 tienen referencias Java localizadas por la auditoría 1.12. **Trazabilidad textual no equivale a revisión funcional individual**.
* COCOMO: 80 archivos Java, 3491 LOC aproximadas = 3.491 KLOC; 8.91884 PM y 5.74192 meses para el escenario nominal.
* Costos y sensibilidad: 6 controles técnicos OK, con los supuestos académicos expresamente declarados.
* Horas observadas: 0 filas comprobadas y corte PARCIAL. No existe desviación observada calculable.

## 2\. Corrección de una inferencia previa sobre M045 y M097

**No se debe excluir automáticamente una E solamente porque el método web no reciba parámetros `@RequestParam`, `@PathVariable` o `@ModelAttribute`.** En COSMIC, el evento que dispara un proceso y el movimiento de entrada deben examinarse dentro de la frontera funcional; la firma HTTP por sí sola no resuelve esa cuestión.

|ID|Proceso|Código verificado|Dictamen técnico|
|-|-|-|-|
|M045|P12 consultar solicitudes propias|`ProductorController.misSolicitudes(Authentication, Model)` obtiene el usuario de sesión y consulta solicitudes del productor|No hay filtros HTTP explícitos. **No excluir ni dar por validada la E a partir de esta observación solamente.** Documentar qué información del evento/grupo de datos cruza la frontera en la definición COSMIC adoptada.|
|M097|P21 consultar historial propio|`ProductorController.historial(Authentication, Model)` usa la identidad autenticada para mostrar solicitudes, préstamos y devoluciones|Mismo criterio que M045. No establecer un total 109 CFP únicamente por estas dos firmas.|
|M080|P18 consultar préstamos|`ProductorController.misPrestamos` usa sesión; `EncargadoPrestamoController.listar` recibe estado de consulta|Una E candidata en un proceso común a dos roles exige describir la variante de evento. No crear dos procesos automáticos ni eliminar la E por ausencia de parámetros en una variante.|

**Decisión de conservación:** mientras no haya otro cambio funcional sustentado, el total de trabajo sigue siendo **111 CFP provisionales**, no 109 CFP. Esta decisión revierte la sugerencia anterior de excluir M045/M097 basándose solamente en parámetros HTTP.

## 3\. Revisión específica de 10 X posteriores a operaciones de escritura

Se verificó que los controladores devuelven `redirect` y comunican información por `RedirectAttributes.addFlashAttribute("exito", ...)`. La respuesta no es necesariamente un simple acuse: varios mensajes incluyen atributos de negocio. La existencia del texto, sin embargo, tampoco garantiza por sí sola un grupo de datos X medible según COSMIC.

|ID|Proceso|Datos de negocio identificados en el código Java|Fuente Java|Decisión de medición de trabajo|
|-|-|-|-|-|
|M005|P03 registrar productor|Nombre completo del productor registrado|`controller/EncargadoProductorController.java`, líneas 45–54|Conservar X candidata; hay dato de negocio observado, verificar grupo de salida|
|M010|P04 actualizar productor|Nombre completo del productor actualizado|`controller/EncargadoProductorController.java`, líneas 75–84|Conservar X candidata; no tratar como un mero `OK`|
|M019|P06 registrar material|Código del material y cantidad inicial|`controller/EncargadoMaterialController.java`, líneas 64–74|Conservar X candidata; datos funcionales presentes|
|M024|P07 actualizar material|Código del material actualizado|`controller/EncargadoMaterialController.java`, líneas 95–104|Conservar X candidata; revisar estructura del grupo de salida|
|M033|P09 ajustar existencias|Existencias disponibles posteriores al ajuste|`controller/EncargadoMaterialController.java`, líneas 128–138|Conservar X candidata; resultado cuantitativo de negocio|
|M044|P11 registrar solicitud|Identificador y estado de la solicitud creada|`controller/ProductorController.java`, líneas 94–106|Conservar X candidata; atributos funcionales expresos|
|M064|P15 aprobar solicitud|Número de solicitud y nuevo estado `APROBADA`|`controller/EncargadoSolicitudController.java`, líneas 60–68|Conservar X candidata; revisar que la salida no sea solo control|
|M069|P16 rechazar solicitud|Número de solicitud y nuevo estado `RECHAZADA`|`controller/EncargadoSolicitudController.java`, líneas 71–80|Conservar X candidata; revisar clasificación COSMIC|
|M079|P17 registrar entrega|Número del préstamo generado y cantidad entregada|`controller/EncargadoSolicitudController.java`, líneas 87–96|Conservar X candidata; identificación funcional concreta|
|M096|P20 registrar devolución|Cantidad devuelta y cantidad pendiente o cierre|`controller/EncargadoPrestamoController.java`, líneas 49–61|Conservar X candidata; magnitudes de negocio concretas|

**Decisión:** ninguna de estas diez X debe borrarse automáticamente por el hecho de usar `redirect`. Para certificar que realmente son movimientos X, revisar los grupos de datos del usuario funcional y la delimitación de su proceso.

## 4\. Otras decisiones documentadas que no deben deshacerse

* **P18 / M080–M083:** las cuatro referencias a dos controladores se encontraron correctamente en Java. M082 se vincula a datos de Solicitud utilizados en `PrestamoResponse`, pero un `JOIN FETCH` no constituye, por sí solo, un movimiento R adicional.
* **D08:** existen `HistorialService.registrar`, `HistorialService.consultar` y pantalla de historial del encargado. Hay funcionalidad visible de consulta; no eliminar automáticamente las diez W, una R y una X de D08 como si todo fuera logging técnico.
* **P15 y P17:** la aprobación de solicitud no descuenta inventario, mientras que la entrega física crea el préstamo y descuenta existencias; son objetivos de negocio diferenciables.
* **P20:** registra condiciones de devolución y puede cerrar el préstamo; la trazabilidad debe respetar esas transiciones de negocio.

## 5\. Alcance que sí puede declararse validado

**Validado técnicamente:** ubicación de las 111 referencias Java del modelo, corrección de las cuatro alertas de referencias alternativas en P18, coherencia aritmética del total actual, recuento de Java, fórmulas nominales y escenarios de costos/sensibilidad.

**No se declara validación funcional definitiva de las 111 filas**: no consta revisión individual completa de E/X/R/W conforme a una versión identificada del manual COSMIC, ni conformidad de una ERS G01 publicada. Tampoco constan horas reales o revisión externa. No renombrar estados para ocultar estas restricciones.

## 6\. Siguiente decisión sobre el libro de 37 hojas

1. Mantener **sin alteración** las columnas `COSMIC\_Movimientos!K` (Contar) y `COSMIC\_Movimientos!M` (Estado de revisión) hasta conciliar un conjunto completo de decisiones funcionales.
2. Validar por separado: (a) eventos E, incluidos los GET sin filtro; (b) grupos de datos R/W; (c) salidas X en POST/redirect; (d) D08 y M082.
3. Cuando cada decisión esté sustentada, actualizar **CSV y XLSX conjuntamente**, recalcular `COSMIC\_Procesos` / `COSMIC\_Resumen`, registrar el nuevo SHA-256 y repetir auditoría del libro.
4. Integrar el resultado realmente obtenido al informe G04. La limitación de una revisión externa o G01 no se sustituye por una marca automática.

**Dictamen:** el presente paso cierra el análisis técnico sobre la evidencia HTTP de M045/M097/M080 y documenta la existencia de atributos de negocio en diez X posteriores a POST. No transforma sin prueba el modelo preliminar en una medida COSMIC aprobada.

