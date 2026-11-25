# G07 — Experimento de Defecto Controlado y Restauración (Pasos 3.15 y 3.16)

## 1. Propósito
Demostrar objetivamente que la suite JUnit 5 detecta errores reales de lógica de negocio mediante aserciones que fallan la compilación Maven (`BUILD FAILURE`), evitando falsos positivos.

## 2. Defecto inyectado temporalmente (Paso 3.15)
En `src/main/java/pe/edu/agroretorno/service/DevolucionService.java` (línea 75) se cambió el operador estricto `>` por `>=`, impidiendo erróneamente devoluciones exactas iguales a las unidades pendientes (archivo `01_defecto_inyectado.diff`):

```diff
--- a/src/main/java/pe/edu/agroretorno/service/DevolucionService.java
+++ b/src/main/java/pe/edu/agroretorno/service/DevolucionService.java
@@ -72,7 +72,7 @@ public class DevolucionService {
         if (prestamo.getEstado() == EstadoPrestamo.CERRADO) {
             throw new ReglaNegocioException("El préstamo " + prestamoId + " ya está cerrado");
         }
-        if (total > prestamo.getCantidadPendiente()) {
+        if (total >= prestamo.getCantidadPendiente()) {
             throw new ReglaNegocioException("La cantidad devuelta (" + total + ") supera las unidades pendientes ("
                     + prestamo.getCantidadPendiente() + ")");
         }
```

## 3. Ejecución fallida capturada (`02_fallo/ejecucion_con_defecto.log`)
Comando:
```bash
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test -Djava.version=17 -Dtest=DevolucionServiceTest -B
```
Resultado real:
```text
[ERROR]   DevolucionServiceTest.cierreTrasVariasParciales:150->devolver:82 » ReglaNegocio La cantidad devuelta (5) supera las unidades pendientes (5)
[ERROR]   DevolucionServiceTest.devolucionTotalBuenEstado:88->devolver:82 » ReglaNegocio La cantidad devuelta (20) supera las unidades pendientes (20)
[ERROR]   DevolucionServiceTest.prestamoCerrado:166->devolver:82 » ReglaNegocio La cantidad devuelta (20) supera las unidades pendientes (20)
[ERROR] Tests run: 13, Failures: 0, Errors: 3, Skipped: 0
[INFO] BUILD FAILURE
```
Los 3 casos afectados fueron `PU-34`, `PU-39` y `PU-40` (todos los escenarios de cierre exacto).

## 4. Restauración y verificación de regresión (`03_restaurado/ejecucion_restaurada.log`, Paso 3.16)
Se restauró la regla original con:
```bash
git checkout -- src/main/java/pe/edu/agroretorno/service/DevolucionService.java
```
Verificación de integridad del código fuente: `git diff src/` devolvió **0 líneas**.

Reejecución inmediata:
```text
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.586 s -- in Componente 3 - DevolucionService
[INFO] BUILD SUCCESS
```
Los archivos XML y TXT originales de Surefire de ambas ejecuciones se conservan en `02_fallo/` y `03_restaurado/`.
