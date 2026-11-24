# G03 â€” Paso 1.2: Diagrama de casos de uso de AgroRetorno

**Responsable:** Cielo Cristal Meza Vizcarra  
**GuÃ­a:** G03 â€” AnÃ¡lisis de Sistemas  
**Fecha de preparaciÃ³n:** 23/11/2026  
**VersiÃ³n del modelo:** 0.1 (propuesta para revisiÃ³n)  
**Fuente de verificaciÃ³n:** cÃ³digo Java de la rama `feature/cielo-g03-analisis-de-sistemas`, consultado en GitHub el 23/11/2026, antes del cambio de nombre solicitado.

## 1. Objetivo

Representar las funcionalidades que realizan los dos actores autorizados en la aplicaciÃ³n web AgroRetorno. El modelo utiliza casos de uso observables en los controladores, agrupados por actor, dentro del lÃ­mite del sistema.

## 2. Actores y alcance

- **Productor:** consulta el catÃ¡logo de materiales, crea solicitudes y da seguimiento a sus propias operaciones.
- **Encargado:** administra registros y existencias, resuelve solicitudes, registra entregas y devoluciones y consulta informaciÃ³n de control.
- **Ambos actores:** pueden iniciar sesiÃ³n; su acceso a rutas se restringe mediante Spring Security.

**LÃ­mite:** funcionalidades de la interfaz web; no se incluyen aquÃ­ los endpoints tÃ©cnicos de `/api/**`, las mÃ©tricas del Actuator ni acciones no verificadas en el cÃ³digo.

## 3. CatÃ¡logo de casos de uso con trazabilidad al cÃ³digo

| ID | Actor | Caso de uso | Evidencia de implementaciÃ³n |
|---|---|---|---|
| CU-01 | Ambos | Iniciar sesiÃ³n | `InicioController.java` â€” GET `/login`; autenticaciÃ³n gestionada por `SecurityConfig.java`. |
| CU-02 | Productor | Consultar inventario | `ProductorController.java` â€” `GET /productor/inventario`. |
| CU-03 | Productor | Registrar solicitud | `ProductorController.java` â€” `POST /productor/solicitudes`. |
| CU-04 | Productor | Consultar solicitudes | `ProductorController.java` â€” `GET /productor/solicitudes`. |
| CU-05 | Productor | Consultar prÃ©stamos | `ProductorController.java` â€” `GET /productor/prestamos`. |
| CU-06 | Productor | Consultar historial | `ProductorController.java` â€” `GET /productor/historial`. |
| CU-07 | Encargado | Gestionar productores | `EncargadoProductorController.java` â€” `GET/POST /encargado/productores`. |
| CU-08 | Encargado | Gestionar materiales | `EncargadoMaterialController.java` â€” `GET/POST /encargado/materiales`. |
| CU-09 | Encargado | Ajustar existencias | `EncargadoMaterialController.java` â€” `POST /encargado/existencias/{id}/ajustes`. |
| CU-10 | Encargado | Revisar solicitudes | `EncargadoSolicitudController.java` â€” `GET /encargado/solicitudes`. |
| CU-11 | Encargado | Aprobar solicitud | `EncargadoSolicitudController.java` â€” `POST /encargado/solicitudes/{id}/aprobar`. |
| CU-12 | Encargado | Rechazar solicitud | `EncargadoSolicitudController.java` â€” `POST /encargado/solicitudes/{id}/rechazar`. |
| CU-13 | Encargado | Registrar entrega | `EncargadoSolicitudController.java` â€” `POST /encargado/solicitudes/{id}/entregar`. |
| CU-14 | Encargado | Consultar prÃ©stamos | `EncargadoPrestamoController.java` â€” `GET /encargado/prestamos`. |
| CU-15 | Encargado | Registrar devoluciÃ³n | `EncargadoPrestamoController.java` â€” `POST /encargado/prestamos/{id}/devoluciones`. |
| CU-16 | Encargado | Consultar reportes e historial | `EncargadoReporteController.java` â€” `GET /encargado/reportes y /encargado/historial`. |

Rutas fuente: `src/main/java/pe/edu/agroretorno/controller/`. Los IDs **CU-01 a CU-16** son identificadores de este modelo de anÃ¡lisis, no equivalencias ya confirmadas con los RF de la ERS.

## 4. RelaciÃ³n con los tres procesos obligatorios del sistema

**Proceso 1 â€” Solicitud.** El productor consulta inventario (CU-02), registra una solicitud (CU-03) y consulta su estado (CU-04).

**Proceso 2 â€” AprobaciÃ³n y entrega.** El encargado revisa solicitudes (CU-10), aprueba o rechaza (CU-11 y CU-12) y, si corresponde, registra la entrega fÃ­sica (CU-13). La aprobaciÃ³n no debe representarse como una entrega automÃ¡tica, porque son acciones distintas en el controlador y servicios.

**Proceso 3 â€” DevoluciÃ³n y cierre.** El encargado consulta prÃ©stamos (CU-14) y registra una devoluciÃ³n (CU-15). `DevolucionService` calcula pendientes y cierra el prÃ©stamo cuando se justifican todas las unidades entregadas.

## 5. Criterios de modelado UML

- Actores dibujados fuera del lÃ­mite del sistema y casos de uso dentro.
- Asociaciones simples (lÃ­neas) entre actor y funcionalidad a la que accede.
- No se dibujan relaciones `include` o `extend` porque no se ha justificado semÃ¡nticamente su necesidad a partir del cÃ³digo revisado.
- CU-01 es un caso compartido por los dos roles; no se duplica como si fueran procesos diferentes.
- GestiÃ³n de productores, materiales y existencias se presenta como caso agrupado, aunque tenga varias rutas y acciones CRUD.

## 6. Decisiones, limitaciones y prÃ³ximos pasos

1. Verificar los nombres y descripciones de los casos contra la ERS oficial antes de la revisiÃ³n final del SDD.
2. AÃ±adir la matriz **RF â†” CU â†” vista/controlador â†” evidencia** al documento SDD cuando la ERS consolidada estÃ© disponible.
3. Elaborar tres especificaciones de caso de uso detalladas: registrar solicitud, aprobar y entregar, registrar devoluciÃ³n.
4. El diagrama es un **modelo estÃ¡tico de funcionalidades derivado del cÃ³digo**, no evidencia de que todos los flujos hayan sido ejecutados exitosamente en una prueba funcional.
5. Mantener el fuente `.puml` y el vectorial `.svg` para ediciÃ³n; el `.png` se usa como vista previa y en el informe.

## 7. Archivos entregados

- `02_Diagramas_UML/G03_02_Casos_de_Uso.puml`: fuente editable PlantUML (diagrama UML).
- `02_Diagramas_UML/G03_02_Casos_de_Uso.svg`: diagrama vectorial editable, representaciÃ³n visual de los mismos casos y asociaciones.
- `02_Diagramas_UML/G03_02_Casos_de_Uso.png`: imagen exportada para documentaciÃ³n.
- `03_Casos_de_Uso/G03_02_Analisis_Casos_de_Uso.md`: esta justificaciÃ³n y catÃ¡logo de casos.

## 8. VerificaciÃ³n para completar manualmente

- [ ] Abrir el `.puml` en PlantUML y comprobar que renderiza sin errores.
- [ ] Revisar la presentaciÃ³n del `.png` y del `.svg`.
- [ ] Revisar que la ERS no renombre ni elimine casos.
- [ ] Anotar observaciones del ingeniero o del equipo y corregir la versiÃ³n, si hace falta.
- [ ] Publicar los archivos en la rama de G03 y dejar un commit de evidencia.

