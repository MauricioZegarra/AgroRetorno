# Bitácora G06 — Gestión de Riesgos (MAGERIT v3)

- **Responsable:** Marco Antonio Marquez Herrera (`mmarquezhe@unsa.edu.pe`)
- **Rama Git:** `feature/marco-g06-gestion-de-riesgos`
- **Commit base de `main`:** `a28b7e4` (AgroRetorno v1.0.0)

## 2026-11-24 — Paso 3.1 y 3.8: Evaluación de PILAR, RMAT y alternativa abierta
- **Actividad:** Verificación de viabilidad de PILAR (CCN-CERT) en entorno Linux (Nobara 44, kernel 7.2.11) y análisis de licenciamiento; comparación estructurada de PILAR, RMAT, Pirani y matriz MAGERIT v3 abierta en Excel/LibreOffice.
- **Resultado:** Se documentó formalmente el impedimento de licencia propietaria/SO de PILAR y se adoptó el libro Excel editable con fórmulas vivas MAGERIT v3 (sujeto a validación docente conforme al Paso 3.1).
- **Evidencias:** `01_Herramienta_y_Metodologia/G06_01_Evaluacion_PILAR_Alternativa_y_RMAT.md`, hoja `Matriz_RMAT_vs_PILAR` en `03_Matriz_MAGERIT_Editable/G06_Matriz_Riesgos_MAGERIT_AgroRetorno.xlsx`.

## 2026-11-24 — Pasos 3.2, 3.3, 3.4 y 3.5: Activos, escenarios, valoración y salvaguardas
- **Actividad:** Auditoría directa del código fuente (`SecurityConfig.java`, `V1__esquema_inicial.sql`, `application.properties`, `DatosDemoInitializer.java`, servicios y repositorios) para identificar 8 activos reales, sus dependencias, 10 escenarios activo–amenaza MAGERIT v3, escalas C/I/D/A/T, cálculo inherente/residual y plan de tratamiento.
- **Resultado:** Libro Excel con 7 hojas y **220 fórmulas recalculadas en LibreOffice con 0 errores** (`recalc.py` `"status": "success"`). Riesgo promedio reducido de `13.55` a `1.54` (`-88.6 %`).
- **Evidencias:** `02_Activos_y_Escenarios/G06_02_Activos_Escenarios_y_Valoracion.md`, `03_Matriz_MAGERIT_Editable/G06_Matriz_Riesgos_MAGERIT_AgroRetorno.xlsx`, `05_Graficos/*.png`.

## 2026-11-24 — Pasos 3.6 y 3.7: Verificación empírica de dos tratamientos implementados
- **Actividad:** Ejecución real mediante Maven Wrapper `3.9.16` y Temurin JDK `17.0.17` de las pruebas automatizadas que comprueban Control T1 (RBAC, propiedad de datos, BCrypt y CSRF) y Control T2 (validación de cantidades, disponibilidad, concurrencia e integridad de inventario).
- **Resultado:** Control T1 = **11/11 PASS** (`BUILD SUCCESS`); Control T2 = **60/60 PASS** (`BUILD SUCCESS`). Se conservaron logs completos y reportes originales XML/TXT de Surefire.
- **Evidencias:** `04_Tratamientos_y_Pruebas_Controles/G06_04_Tratamientos_y_Verificacion_Controles.md`, `04_Tratamientos_y_Pruebas_Controles/control_1_rbac/`, `04_Tratamientos_y_Pruebas_Controles/control_2_validacion/`.

## 2026-11-24 — Informe final y auditoría visual
- **Actividad:** Generación del informe consolidado G06 (incluyendo trazabilidad hacia G07 y cuestionario LAB06), exportación a DOCX y PDF, y revisión visual página por página.
- **Evidencias:** `06_Informe_Final/G06_Informe_Gestion_Riesgos_AgroRetorno_Marco.{md,docx,pdf}`.
- **Pendientes para integración final:** Validación docente de la alternativa a PILAR, consolidación con Ower (G01) y cruce de escenarios `ESC-07` / `ESC-09` con los resultados JMeter de Mauricio (G08).
