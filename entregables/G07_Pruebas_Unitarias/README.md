# G07 — Pruebas Unitarias y Cobertura (JUnit 5, Mockito y JaCoCo)

- **Responsable:** Marco Antonio Marquez Herrera (`mmarquezhe@unsa.edu.pe`)
- **Proyecto:** AgroRetorno v1.0.0 (commit base `a28b7e4`)
- **Curso:** Calidad de Software — Semestre 8 (2026-B) · UNSA (EPIS)
- **Rama Git:** `feature/marco-g07-pruebas-unitarias`
- **Línea base de requisitos:** 15 RF (`RF01–RF15`) y 10 RNF (`RNF01–RNF10`) alineados con G03/G04

## Objetivo
Ejecutar, catalogar y auditar la batería de pruebas unitarias de la lógica de negocio de AgroRetorno mediante Maven, JUnit 5, Mockito y JaCoCo, verificando cobertura ≥ 80 % de líneas en `pe.edu.agroretorno.service`, sensibilidad ante defectos controlados y trazabilidad hacia los riesgos de G06.

## Resultados verificados (24/11/2026)
- **Comando reproducible:** `JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw clean verify -Djava.version=17 -B`
- **Suite ejecutada:** **72/72 ejecuciones PASS** (0 fallos, 0 errores, 0 omitidas) en `24.645 s` con orden aleatorio (`runOrder=random`).
  - Pruebas unitarias con Mockito: **43 casos catalogados** (`PU-01`–`PU-43`, **57 ejecuciones** parametrizadas) en 4 servicios (`InventarioService`, `SolicitudService`, `PrestamoService`, `DevolucionService`).
  - Pruebas de integración y seguridad: **15 casos** (`IT-01`–`IT-05`, `SEG-01`–`SEG-10`) con Spring Boot + H2 (perfil `test`).
- **Cobertura JaCoCo 0.8.14:** **97.6 % de líneas** (`496/508`) y **81.7 % de ramas** (`201/246`) en `pe.edu.agroretorno.service` — `jacoco:check` **APROBADO** (meta ≥ 80 %).
- **Defecto controlado (`DevolucionService.java:75`, `>` → `>=`):** 3 errores detectados (`PU-34`, `PU-39`, `PU-40`, `BUILD FAILURE`); tras restaurar (`git diff src/` = 0 líneas), **13/13 PASS** (`BUILD SUCCESS`).

## Estructura de carpetas y evidencias
- `01_Ejecucion_Maven/`: versiones exactas de herramientas, log completo de `./mvnw clean verify` y guía de reproducción.
- `02_Catalogo_Pruebas/`: libro Excel editable `G07_Catalogo_Pruebas_Unitarias_AgroRetorno.xlsx` (110 fórmulas, 0 errores) y análisis de Mockito.
- `03_Reportes_Originales/`: reportes originales sin alterar de Maven Surefire (`surefire-reports/`) y JaCoCo (`jacoco/index.html`, `jacoco.csv`, `jacoco.xml`).
- `04_Defecto_Controlado/`: parche exacto `01_defecto_inyectado.diff`, logs y reportes Surefire del fallo (`02_fallo/`) y de la restauración (`03_restaurado/`).
- `05_Graficos/`: gráficos de cobertura JaCoCo y distribución de la suite.
- `06_Informe_Final/`: informe completo en Markdown, Word (`.docx`) y PDF (`.pdf`).
- `Bitacora.md`: registro cronológico de actividades y participación real.

## Estado
**Completo y verificado** (listo para revisión e integración con el equipo).
