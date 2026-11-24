# G03 — Paso 1.2: Diagrama de casos de uso de AgroRetorno

**Responsable:** Cielo Cristal Meza Vizcarra  
**Guía:** G03 — Análisis de Sistemas  
**Fecha de preparación:** 23/11/2026  
**Versión del modelo:** 0.1 (propuesta para revisión)  
**Fuente de verificación:** código Java de la rama `feature/cielo-g03-g04`, consultado en GitHub el 23/11/2026, antes del cambio de nombre solicitado.

## 1. Objetivo

Representar las funcionalidades que realizan los dos actores autorizados en la aplicación web AgroRetorno. El modelo utiliza casos de uso observables en los controladores, agrupados por actor, dentro del límite del sistema.

## 2. Actores y alcance

- **Productor:** consulta el catálogo de materiales, crea solicitudes y da seguimiento a sus propias operaciones.
- **Encargado:** administra registros y existencias, resuelve solicitudes, registra entregas y devoluciones y consulta información de control.
- **Ambos actores:** pueden iniciar sesión; su acceso a rutas se restringe mediante Spring Security.

**Límite:** funcionalidades de la interfaz web; no se incluyen aquí los endpoints técnicos de `/api/**`, las métricas del Actuator ni acciones no verificadas en el código.

## 3. Catálogo de casos de uso con trazabilidad al código

| ID | Actor | Caso de uso | Evidencia de implementación |
|---|---|---|---|
| CU-01 | Ambos | Iniciar sesión | `InicioController.java` — GET `/login`; autenticación gestionada por `SecurityConfig.java`. |
| CU-02 | Productor | Consultar inventario | `ProductorController.java` — `GET /productor/inventario`. |
| CU-03 | Productor | Registrar solicitud | `ProductorController.java` — `POST /productor/solicitudes`. |
| CU-04 | Productor | Consultar solicitudes | `ProductorController.java` — `GET /productor/solicitudes`. |
| CU-05 | Productor | Consultar préstamos | `ProductorController.java` — `GET /productor/prestamos`. |
| CU-06 | Productor | Consultar historial | `ProductorController.java` — `GET /productor/historial`. |
| CU-07 | Encargado | Gestionar productores | `EncargadoProductorController.java` — `GET/POST /encargado/productores`. |
| CU-08 | Encargado | Gestionar materiales | `EncargadoMaterialController.java` — `GET/POST /encargado/materiales`. |
| CU-09 | Encargado | Ajustar existencias | `EncargadoMaterialController.java` — `POST /encargado/existencias/{id}/ajustes`. |
| CU-10 | Encargado | Revisar solicitudes | `EncargadoSolicitudController.java` — `GET /encargado/solicitudes`. |
| CU-11 | Encargado | Aprobar solicitud | `EncargadoSolicitudController.java` — `POST /encargado/solicitudes/{id}/aprobar`. |
| CU-12 | Encargado | Rechazar solicitud | `EncargadoSolicitudController.java` — `POST /encargado/solicitudes/{id}/rechazar`. |
| CU-13 | Encargado | Registrar entrega | `EncargadoSolicitudController.java` — `POST /encargado/solicitudes/{id}/entregar`. |
| CU-14 | Encargado | Consultar préstamos | `EncargadoPrestamoController.java` — `GET /encargado/prestamos`. |
| CU-15 | Encargado | Registrar devolución | `EncargadoPrestamoController.java` — `POST /encargado/prestamos/{id}/devoluciones`. |
| CU-16 | Encargado | Consultar reportes e historial | `EncargadoReporteController.java` — `GET /encargado/reportes y /encargado/historial`. |

Rutas fuente: `src/main/java/pe/edu/agroretorno/controller/`. Los IDs **CU-01 a CU-16** son identificadores de este modelo de análisis, no equivalencias ya confirmadas con los RF de la ERS.

## 4. Relación con los tres procesos obligatorios del sistema

**Proceso 1 — Solicitud.** El productor consulta inventario (CU-02), registra una solicitud (CU-03) y consulta su estado (CU-04).

**Proceso 2 — Aprobación y entrega.** El encargado revisa solicitudes (CU-10), aprueba o rechaza (CU-11 y CU-12) y, si corresponde, registra la entrega física (CU-13). La aprobación no debe representarse como una entrega automática, porque son acciones distintas en el controlador y servicios.

**Proceso 3 — Devolución y cierre.** El encargado consulta préstamos (CU-14) y registra una devolución (CU-15). `DevolucionService` calcula pendientes y cierra el préstamo cuando se justifican todas las unidades entregadas.

## 5. Criterios de modelado UML

- Actores dibujados fuera del límite del sistema y casos de uso dentro.
- Asociaciones simples (líneas) entre actor y funcionalidad a la que accede.
- No se dibujan relaciones `include` o `extend` porque no se ha justificado semánticamente su necesidad a partir del código revisado.
- CU-01 es un caso compartido por los dos roles; no se duplica como si fueran procesos diferentes.
- Gestión de productores, materiales y existencias se presenta como caso agrupado, aunque tenga varias rutas y acciones CRUD.

## 6. Decisiones, limitaciones y próximos pasos

1. Verificar los nombres y descripciones de los casos contra la ERS oficial antes de la revisión final del SDD.
2. Añadir la matriz **RF ↔ CU ↔ vista/controlador ↔ evidencia** al documento SDD cuando la ERS consolidada esté disponible.
3. Elaborar tres especificaciones de caso de uso detalladas: registrar solicitud, aprobar y entregar, registrar devolución.
4. El diagrama es un **modelo estático de funcionalidades derivado del código**, no evidencia de que todos los flujos hayan sido ejecutados exitosamente en una prueba funcional.
5. Mantener el fuente `.puml` y el vectorial `.svg` para edición; el `.png` se usa como vista previa y en el informe.

## 7. Archivos entregados

- `02_Diagramas_UML/G03_02_Casos_de_Uso.puml`: fuente editable PlantUML (diagrama UML).
- `02_Diagramas_UML/G03_02_Casos_de_Uso.svg`: diagrama vectorial editable, representación visual de los mismos casos y asociaciones.
- `02_Diagramas_UML/G03_02_Casos_de_Uso.png`: imagen exportada para documentación.
- `03_Casos_de_Uso/G03_02_Analisis_Casos_de_Uso.md`: esta justificación y catálogo de casos.

## 8. Verificación para completar manualmente

- [ ] Abrir el `.puml` en PlantUML y comprobar que renderiza sin errores.
- [ ] Revisar la presentación del `.png` y del `.svg`.
- [ ] Revisar que la ERS no renombre ni elimine casos.
- [ ] Anotar observaciones del ingeniero o del equipo y corregir la versión, si hace falta.
- [ ] Publicar los archivos en la rama de G03 y dejar un commit de evidencia.
