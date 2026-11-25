# G06 — Gestión de Riesgos (Metodología MAGERIT v3)

- **Responsable:** Marco Antonio Marquez Herrera (`mmarquezhe@unsa.edu.pe`)
- **Proyecto:** AgroRetorno v1.0.0 (commit base `a28b7e4`)
- **Curso:** Calidad de Software — Semestre 8 (2026-B) · UNSA (EPIS)
- **Rama Git:** `feature/marco-g06-gestion-de-riesgos`
- **Línea base de requisitos:** 15 RF (`RF01–RF15`) y 10 RNF (`RNF01–RNF10`) alineados con G03/G04

## Objetivo
Aplicar el proceso de gestión de riesgos de MAGERIT v3 (ISO 31000 / ISO/IEC 27005) sobre AgroRetorno v1.0.0: evaluar PILAR y alternativas (RMAT, Excel MAGERIT, Pirani), inventariar activos y dependencias, valorar escenarios activo–amenaza, priorizar salvaguardas con riesgo residual y verificar mediante pruebas reales ejecutadas dos tratamientos críticos de seguridad e integridad.

## Resultados verificados (24/11/2026)
- **Activos evaluados:** **8 activos** reales (`ACT-01`–`ACT-08`, mínimo exigido: 5) con responsables, dependencias y valoración C/I/D/A/T.
- **Escenarios activo–amenaza:** **10 escenarios** (`ESC-01`–`ESC-10`, mínimo exigido: 8) codificados con el catálogo oficial MAGERIT v3.
- **Reducción de riesgo calculada (220 fórmulas, 0 errores):**
  - Riesgo inherente: **4 CRÍTICO, 5 ALTO, 1 MEDIO, 0 BAJO** (promedio **13.55/25**, pico **20.00/25**).
  - Riesgo residual: **0 CRÍTICO, 0 ALTO, 0 MEDIO, 10 BAJO** (promedio **1.54/25**, reducción **-88.6 %**, pico **3.00/25**).
- **Dos tratamientos verificados con ejecución real Maven/JUnit 5:**
  - **Control T1 (RBAC, propiedad de datos, BCrypt y CSRF):** **11/11 PASS** (`SeguridadIntegrationTest` + `SolicitudServiceTest$Registro#restriccionesDePropiedad`).
  - **Control T2 (Validación de cantidades, disponibilidad e integridad de inventario):** **60/60 PASS** (`InventarioServiceTest*`, `SolicitudServiceTest*`, `PrestamoServiceTest`, `DevolucionServiceTest`, `IT-02`, `SEG-08`, `SEG-10`).

## Estructura de carpetas y evidencias
- `01_Herramienta_y_Metodologia/`: impedimento documentado de PILAR en Linux/licencia, alternativa validable y matriz RMAT vs. PILAR + cuestionario LAB06.
- `02_Activos_y_Escenarios/`: catálogo de 8 activos, dependencias y valoración de los 10 escenarios MAGERIT v3.
- `03_Matriz_MAGERIT_Editable/`: libro Excel editable `G06_Matriz_Riesgos_MAGERIT_AgroRetorno.xlsx` (7 hojas, 220 fórmulas verificadas en LibreOffice con 0 errores).
- `04_Tratamientos_y_Pruebas_Controles/`: plan de salvaguardas, logs reales y reportes XML/TXT originales de Surefire de Control T1 (`control_1_rbac/`) y Control T2 (`control_2_validacion/`).
- `05_Graficos/`: mapa de calor Inherente vs. Residual, comparativa de reducción por escenario y grafo de dependencias de activos.
- `06_Informe_Final/`: informe completo en Markdown, Word (`.docx`) y PDF (`.pdf`).
- `Bitacora.md`: registro cronológico de actividades y participación real.

## Estado
**Completo y verificado** (pendiente únicamente validación docente de la alternativa abierta a PILAR e integración con G01/G08).
