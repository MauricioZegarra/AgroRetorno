# G04 — Paso 1.13 | Dictamen técnico de focos COSMIC y P18

**Proyecto:** AgroRetorno  
**Responsable de la medición:** Cielo Cristal Meza Vizcarra  
**Rama fuente:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Línea base del catálogo RF:** G03, commit `1fef1d211`  
**Código Java contrastado:** rama G04, commit `8021edd`  
**CSV de movimientos:** `02_COSMIC/G04_03_Movimientos_COSMIC.csv`  
**SHA-256 CSV:** `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`  

> Este documento cierra **la explicación y tratamiento de las alertas de trazabilidad/estructura** dentro de la línea base técnica actual. No equivale a una certificación del método COSMIC, a una revisión externa ni a la conformidad de una ERS G01 que no se ha publicado.

## 1. Estado verificable

- 111 movimientos candidatos sobre 21 procesos incluidos.
- Auditoría 1.12: **111 referencias Java localizadas, 0 referencias ausentes**; las cuatro referencias de P18 usan dos alternativas de controlador.
- No existen duplicados exactos de la terna `(proceso, tipo, grupo)` en el CSV de origen.
- **24 focos de clasificación estructural**, distribuidos en D08 (12), entradas con grupo de datos FILTRO (11), salida derivada REPORTE (1). Las otras 87 filas no produjeron estas alertas; esto no equivale a revisión semántica individual.
- La suma del modelo de origen sigue siendo E=21, X=21, R=41, W=28, **111 CFP preliminares**. Este dictamen no modifica el Excel ni las casillas Contar.

## 2. Decisiones de alcance para los focos

**D08 — Historial de operaciones.** Se conserva como **candidato a grupo lógico de negocio** en la línea base de medición, porque existe una operación visible de consulta de historial para el ENCARGADO (`EncargadoReporteController.historial` → `HistorialService.consultar`). Las diez escrituras registradas son persistentes y alimentan ese historial, pero deben interpretarse como movimientos funcionales solo bajo el requisito de trazabilidad adoptado en esta versión. No se asume que todo logging técnico sea funcional.

**FILTRO — Datos de consulta o disparador.** Las once E se modelan como datos/criterios de solicitud del usuario funcional (texto, categoría, identificador, estado, período o alcance propio). **FILTRO no es un ILF/EIF ni un grupo persistente**: es la agrupación de datos de entrada de la petición que dispara el proceso. Se conserva el modelo de una E candidata por proceso a efectos de la línea base, sin equiparar un clic de control vacío a un movimiento válido de datos.

**REPORTE — Salida calculada.** `M111` se conserva como candidato a X de un reporte consolidado reconocible por el encargado. Se trata de datos funcionales derivados que salen del sistema, no de un nuevo archivo lógico persistente. La fuente Java relevante es `ReporteService.generar` a través de `EncargadoReporteController.reporte`.

## 3. Tratamiento individual de 24 focos

| ID | P | RF | Tipo | Foco | Decisión documentada de la línea base actual |
|---|---|---|---|---|---|
| M004 | P03 | RF03 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M009 | P04 | RF03 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M011 | P05 | RF03 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M018 | P06 | RF04 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M023 | P07 | RF04 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M025 | P08 | RF04 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M032 | P09 | RF05 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M034 | P10 | RF06 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M043 | P11 | RF07 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M045 | P12 | RF08 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M049 | P13 | RF09 / RF10 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M053 | P14 | RF09 / RF10 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M063 | P15 | RF09 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M068 | P16 | RF10 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M078 | P17 | RF11 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M080 | P18 | RF12 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M084 | P19 | RF12 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M095 | P20 | RF13 | W | D08 · historial | Conservar escritura candidata de bitácora funcional consultable |
| M097 | P21 | RF14 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M102 | P22 | RF14 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M103 | P22 | RF14 | R | D08 · historial | Conservar como movimiento candidato de historial accesible funcionalmente |
| M104 | P22 | RF14 | X | D08 · historial | Conservar como movimiento candidato de historial accesible funcionalmente |
| M105 | P23 | RF15 | E | FILTRO · entrada | Conservar E candidata de solicitud/criterio; no contabilizar FILTRO como ILF |
| M111 | P23 | RF15 | X | REPORTE · salida | Conservar X candidata de resultado consolidado calculado |

## 4. P18 — Resolver las cuatro referencias alternativas

**Proceso:** P18 Consultar préstamos. **Requisito:** RF12 del catálogo G03 (consultar préstamos y pendientes). Los dos controladores representan variantes de alcance para distintos usuarios, no un método Java inexistente:

- `ProductorController.misPrestamos()` llama `PrestamoService.listarPorProductor` y restringe por productor autenticado.
- `EncargadoPrestamoController.listar()` llama `PrestamoService.listar` y restringe por estado de préstamo.
- `PrestamoRepository.findByProductorId` y `findByEstado` contienen `join fetch pr.solicitud` para construir `PrestamoResponse`.

| Movimiento | Tipo/grupo | Tratamiento en la línea base técnica | Soporte |
|---|---|---|---|
| M080 | E / criterios | Conservar la solicitud de consulta como E candidata | Dos rutas de consulta por rol |
| M081 | R / D05 Préstamos | Conservar lectura candidata | `PrestamoRepository.findByEstado/findByProductorId` |
| M082 | R / D04 Solicitudes | **Conservar en el modelo propuesto** porque la respuesta de préstamo utiliza información de solicitud, reconocida como grupo separado D04 en la frontera de trabajo; no basar el R únicamente en la sintaxis `JOIN FETCH` | `PrestamoResponse.from` usa `p.getSolicitud()` y extrae productor/material y `solicitudId` |
| M083 | X / D05 Préstamos | Conservar salida candidata con los préstamos y estados al usuario | `model.addAttribute("prestamos", ...)` en ambos controladores |

**Decisión P18:** la alerta de cuatro métodos «no localizados» se **resuelve como falso negativo del algoritmo de búsqueda**. La clasificación anterior es una **decisión de modelado justificable para el alcance provisional**, no una aprobación externa. No duplicar P18 por el mero hecho de existir dos rutas/roles.

## 5. Inventario de los 21 procesos de la línea base

| Proceso | RF | E | X | R | W | CFP candidato |
|---|---|---:|---:|---:|---:|---:|
| P03 — Registrar productor | RF03 | 1 | 1 | 1 | 2 | 5 |
| P04 — Actualizar productor | RF03 | 1 | 1 | 1 | 2 | 5 |
| P05 — Buscar productores | RF03 | 1 | 1 | 1 | 0 | 3 |
| P06 — Registrar material | RF04 | 1 | 1 | 1 | 3 | 6 |
| P07 — Actualizar material | RF04 | 1 | 1 | 1 | 2 | 5 |
| P08 — Consultar materiales | RF04 | 1 | 1 | 1 | 0 | 3 |
| P09 — Ajustar existencias | RF05 | 1 | 1 | 1 | 3 | 6 |
| P10 — Consultar inventario disponible | RF06 | 1 | 1 | 2 | 0 | 4 |
| P11 — Registrar solicitud | RF07 | 1 | 1 | 3 | 2 | 7 |
| P12 — Consultar solicitudes propias | RF08 | 1 | 1 | 2 | 0 | 4 |
| P13 — Consultar solicitudes del almacén | RF09 / RF10 | 1 | 1 | 2 | 0 | 4 |
| P14 — Consultar detalle de solicitud | RF09 / RF10 | 1 | 1 | 4 | 0 | 6 |
| P15 — Aprobar solicitud | RF09 | 1 | 1 | 2 | 2 | 6 |
| P16 — Rechazar solicitud | RF10 | 1 | 1 | 1 | 2 | 5 |
| P17 — Registrar entrega física | RF11 | 1 | 1 | 3 | 5 | 10 |
| P18 — Consultar préstamos | RF12 | 1 | 1 | 2 | 0 | 4 |
| P19 — Consultar detalle de préstamo | RF12 | 1 | 1 | 2 | 0 | 4 |
| P20 — Registrar devolución | RF13 | 1 | 1 | 2 | 5 | 9 |
| P21 — Consultar historial del productor | RF14 | 1 | 1 | 3 | 0 | 5 |
| P22 — Consultar historial de auditoría | RF14 | 1 | 1 | 1 | 0 | 3 |
| P23 — Generar reporte consolidado | RF15 | 1 | 1 | 5 | 0 | 7 |

## 6. Condiciones de uso y límites explícitos

1. **Trazabilidad técnica de referencias:** comprobada en las 111 filas por el Paso 1.12, sujeto a que sea el mismo commit de código. **Focos estructurales:** identificados y tratados explícitamente en esta acta, sin alterar el conteo.
2. **Medición funcional definitiva:** las decisiones sobre grupos de datos, salidas de simple acuse y lectura de entidades relacionadas requieren interpretación de requisitos COSMIC. Este documento no dice que las 111 filas fueron aprobadas individualmente.
3. **Comparación con ERS G01:** la rama G01 y su ERS no estaban publicadas en el repositorio durante la revisión. Para reproducibilidad se fija y nombra la línea base RF de G03; no se atribuye validación de G01.
4. **Horas reales:** 0 filas en el libro auditado; no se calcula desviación observada ni se inventan horas.
5. **Reporte final:** puede describir estas limitaciones como alcance de la medición y como decisiones justificadas, pero no declarar una revisión académica inexistente.

## 7. Fuentes para reproducir

- CSV de movimientos: `entregables/G04_Dimensionamiento/02_COSMIC/G04_03_Movimientos_COSMIC.csv`.
- Trazabilidad técnica detallada: `entregables/G04_Dimensionamiento/04_Comparaciones/G04_12_Auditoria_Trazabilidad_COSMIC.md` y `G04_12_Control_COSMIC_Filas.csv`.
- Línea base RF: `entregables/G03_Analisis_Sistemas/03_Casos_de_Uso/G03_06_Matriz_RF_CU_Codigo_Interfaz.csv`, rama G03.
- Código: `src/main/java/pe/edu/agroretorno/controller/ProductorController.java`, `EncargadoPrestamoController.java`, `EncargadoReporteController.java`; `service/PrestamoService.java`, `HistorialService.java`, `ReporteService.java`; `repository/PrestamoRepository.java`; `dto/PrestamoResponse.java`.

**Dictamen de este paso:** 4 alertas de referencia P18 resueltas; 24 focos estructurales con tratamiento documentado; 111 CFP conservados **como línea base de trabajo**, no como medición aprobada.
