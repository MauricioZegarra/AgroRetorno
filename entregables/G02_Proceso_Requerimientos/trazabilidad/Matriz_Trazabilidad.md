# Matriz de trazabilidad | AgroRetorno

**Fuente** · Primer avance, §§3.4–3.5. Los CU proceden del catálogo G03. Los identificadores PU, IT y SEG se refieren al comportamiento comprobado de los casos. Algunas etiquetas históricas entre corchetes usan la numeración anterior.

**Interpretación** · «Ejecutado» identifica un caso Maven disponible en la regresión. «Parcial» indica que el caso cubre parte del criterio o usa un entorno distinto. La validación funcional por representantes sigue pendiente.

## Requisitos funcionales

| ID | Diseño G03 | Componentes | Casos y alcance |
|---|---|---|---|
| RF01 | CU-01 | SecurityConfig, InicioController | SEG-02, ejecución de login y logout. CR-01 ejercita la configuración del modo demo. |
| RF02 | CU-01/03/11/13/15 | SecurityConfig, Authorization y controladores | SEG-04 y SEG-05, ejecutados para rutas web y API seleccionadas. |
| RF03 | CU-07 | EncargadoProductorController, ProductorService | IT-04, registro, actualización, unicidad y hash. Validar activación e inactivación desde la interfaz. |
| RF04 | CU-08 | EncargadoMaterialController, MaterialService | IT-05, código único y efecto de material INACTIVO. |
| RF05 | CU-09 | EncargadoMaterialController, InventarioService | PU-11–PU-15, ajustes positivos, inválidos y cuadre. |
| RF06 | CU-02 | ProductorController, InventarioService | PU-01–PU-06 y SEG-07. Completar búsqueda por texto y categoría en Chrome. |
| RF07 | CU-03, ED-01 | ProductorController, ProductorApiController, SolicitudService | PU-16–PU-21, SEG-07/08/10, registro y validaciones. |
| RF08 | CU-04 | ProductorApiController, SolicitudService | SEG-06 y PU-27, propiedad y consulta. Confirmar motivo en vista. |
| RF09 | CU-11, ED-02 | EncargadoSolicitudController, SolicitudService | PU-22/23/25 e IT-01, aprobación, falta de stock y estado. |
| RF10 | CU-12, ED-02 | EncargadoSolicitudController, SolicitudService | PU-24/26 e IT-03, regla y motivo. Confirmar visualización en Chrome. |
| RF11 | CU-13, ED-02 | EncargadoSolicitudController, PrestamoService | PU-28–PU-32 e IT-01, entrega, revalidación y unicidad. |
| RF12 | CU-05/14 | ProductorController, EncargadoPrestamoController, PrestamoService, PrestamoResponse | PU-33 e IT-01. La condición vencido se calcula en servidor. |
| RF13 | CU-15, ED-03 | EncargadoPrestamoController, DevolucionService | PU-34–PU-42 e IT-01, devoluciones, observaciones y cierre. |
| RF14 | CU-06/16 | HistorialService, ProductorController, EncargadoReporteController | IT-03, filtros sobre una muestra. Validar restricciones y filtros por rol en UI. |
| RF15 | CU-16 | EncargadoReporteController, ReporteService | IT-03 y SEG-09, consolidado y filtros sobre los casos ejecutados. |

## Requisitos no funcionales

| ID | Diseño o mecanismo | Caso o artefacto | Estado al corte |
|---|---|---|---|
| RNF01 | MVC, API REST y entorno G08 | Escenario JMeter A | Pendiente de adjuntar medición de carga de la revisión entregada. |
| RNF02 | BCrypt, persistencia de usuarios y configuración demo | SEG-01, IT-04 y CR-01 | Ejecutado en laboratorio para las muestras cubiertas. |
| RNF03 | Spring Security, autorización de métodos, CSRF | SEG-03–SEG-06; UI-03 | Ejecutado en Maven. Evidencia Chrome pendiente en este paquete. |
| RNF04 | PostgreSQL y migración Flyway | Conteos e IDs antes y después de reiniciar | Reportado en primer avance. Evidencia operativa pendiente en esta revisión. |
| RNF05 | Bean Validation, reglas de dominio y transacciones | IT-02, SEG-08/10 y casos PU | Ejecutado en subconjunto Maven. |
| RNF06 | Invariante de inventario, bloqueo y restricciones CHECK/UNIQUE | PU-11–PU-15/28–32, IT-01, SQL Flyway | Parcial. Casos Maven e inspección del esquema. Conciliación en PostgreSQL pendiente. |
| RNF07 | Historial transaccional por actor, acción y fecha | IT-03 y conciliación de movimientos | Parcial. Se comprobó una muestra y sus filtros. |
| RNF08 | Plantillas Thymeleaf y navegabilidad por roles | UI-01–UI-10 Chrome | Reportado en primer avance. Evidencia de la revisión actual pendiente. |
| RNF09 | JaCoCo por paquete de servicios | Reporte de líneas y `mvn verify` | Comprobado para la regresión de G05. |
| RNF10 | Respaldo y recuperación PostgreSQL | Procedimiento y comparación de filas | Reportado en primer avance. La restauración SVN corresponde a G05. |

## Trazabilidad de CR-01

| Origen | Requisitos | Diseño | Implementación | Verificación | Integración y cierre |
|---|---|---|---|---|---|
| Revisión de la línea base `a28b7e4` | RF01, RNF02 y regresión RNF03 | [Cambio de arranque](../Cambios.md#diseño-antes-y-después) | [ERS](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g01-requerimientos/entregables/G01_Requerimientos/ERS.md#aclaración-de-configuración-vinculada-a-cr-01), [código](../../../src/main/java/pe/edu/agroretorno/config/DatosDemoInitializer.java), [commit Git](https://github.com/MauricioZegarra/AgroRetorno/commit/eaa20e9d9c3b0404bd2b946a1a078a2f3e7e5acd) y SVN r6 | [DemoDataDefaultTest](../../../src/test/java/pe/edu/agroretorno/config/DemoDataDefaultTest.java) y [regresión integrada](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g05-gestion-de-configuracion/entregables/G05_Gestion_Configuracion/README.md#regresión-de-cr-01) | SVN r9. Cierre técnico registrado. Aprobación y autoría académicas pendientes de evidencia de Ower. |
