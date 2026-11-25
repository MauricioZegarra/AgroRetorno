# Bitácora G07 — Pruebas Unitarias y Cobertura

- **Responsable:** Marco Antonio Marquez Herrera (`mmarquezhe@unsa.edu.pe`)
- **Rama Git:** `feature/marco-g07-pruebas-unitarias`
- **Commit base de `main`:** `a28b7e4` (AgroRetorno v1.0.0)

## 2026-11-24 — Paso 3.9: Preparación de compilación Maven y ejecución completa
- **Actividad:** Incorporación de `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/` y `.gitignore` desde `AgroRetorno-v1.0.0.zip` (sin modificar `src/`) y ejecución completa `./mvnw clean verify`.
- **Herramientas:** Apache Maven Wrapper `3.9.16`, Eclipse Temurin JDK `17.0.17` (`-Djava.version=17`), Spring Boot `3.5.16`, JUnit 5 (Jupiter), Mockito, AssertJ `3.27.7`, Maven Surefire `3.5.6`, JaCoCo `0.8.14`.
- **Resultado:** `Tests run: 72, Failures: 0, Errors: 0, Skipped: 0`, `jacoco:check` aprobado (`BUILD SUCCESS` en `24.645 s`).
- **Evidencias:** `01_Ejecucion_Maven/00_entorno_y_versiones.txt`, `01_Ejecucion_Maven/01_mvn_clean_verify.log`, `03_Reportes_Originales/surefire-reports/`, `03_Reportes_Originales/jacoco/`.

## 2026-11-24 — Pasos 3.10–3.14: Catálogo de casos, verificación Mockito y análisis JaCoCo
- **Actividad:** Revisión de las 6 clases de prueba (`InventarioServiceTest`, `SolicitudServiceTest`, `PrestamoServiceTest`, `DevolucionServiceTest`, `ProcesosNegocioIntegrationTest`, `SeguridadIntegrationTest`), catalogación de 43 casos unitarios (`PU-01`–`PU-43`) + 15 de integración/seguridad alineados a la línea base de 15 RF / 10 RNF y verificación de cobertura JaCoCo.
- **Resultado:** Cobertura medida en `pe.edu.agroretorno.service` = **97.6 % de líneas** (`496/508`) y **81.7 % de ramas** (`201/246`), superando el mínimo de 80 %. Exclusiones documentadas (`controller` cubierto por Selenium en G09; `AgroRetornoApplication.main`).
- **Evidencias:** `02_Catalogo_Pruebas/G07_Catalogo_Pruebas_Unitarias_AgroRetorno.xlsx` (110 fórmulas recalculadas, 0 errores), `02_Catalogo_Pruebas/G07_02_Catalogo_y_Analisis_Mockito.md`, `05_Graficos/G07_01_Cobertura_JaCoCo_Servicios.png`, `05_Graficos/G07_02_Distribucion_Suite_Pruebas.png`.

## 2026-11-24 — Pasos 3.15–3.16: Experimento de defecto controlado y restauración
- **Actividad:** Mutación temporal en `src/main/java/pe/edu/agroretorno/service/DevolucionService.java` línea 75 (`if (total > prestamo.getCantidadPendiente())` → `>=`), ejecución de `DevolucionServiceTest`, captura del fallo, restauración exacta con `git checkout` y reejecución.
- **Resultado:** Con defecto inyectado: `Tests run: 13, Failures: 0, Errors: 3` (`PU-34`, `PU-39`, `PU-40`, `BUILD FAILURE`). Tras restaurar (`git diff src/` = 0 líneas): `Tests run: 13, Failures: 0, Errors: 0` (`BUILD SUCCESS`).
- **Evidencias:** `04_Defecto_Controlado/01_defecto_inyectado.diff`, `04_Defecto_Controlado/02_fallo/`, `04_Defecto_Controlado/03_restaurado/`, `04_Defecto_Controlado/G07_03_Defecto_Controlado_y_Restauracion.md`.

## 2026-11-24 — Pasos 3.17–3.18: Trazabilidad G06↔G07 e informe final
- **Actividad:** Vinculación de casos JUnit con los escenarios de riesgo y controles T1/T2 de G06, redacción del informe técnico con cuestionario LAB07, exportación a DOCX y PDF y auditoría visual de todas las páginas.
- **Evidencias:** `06_Informe_Final/G07_Informe_Pruebas_Unitarias_AgroRetorno_Marco.{md,docx,pdf}`.
- **Pendientes para integración final:** Consolidar la trazabilidad general con Ower (G01) y anexar el resultado de pruebas funcionales Selenium (G09) de Mauricio sobre los controladores web.
