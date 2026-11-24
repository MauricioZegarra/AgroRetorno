# G03 · Paso 1.4 — Diagramas UML de secuencia

**Proyecto:** AgroRetorno · **Responsable:** Cielo Cristal Meza Vizcarra  
**Guía:** G03 — Análisis de Sistemas  
**Estado:** modelos documentales derivados del código; requieren revisión visual por la responsable  
**Fecha de preparación:** 23/11/2026  
**Versión de referencia:** rama `feature/cielo-g03-analisis-de-sistemas` (código revisado mediante GitHub)

## 1. Propósito

Describir cómo colaboran actores, controladores Spring MVC, servicios de negocio y repositorios JPA durante los procesos **registrar solicitud (RF06)**, **aprobar solicitud (RF07)** y **registrar entrega (RF08)**. Los diagramas explican las interacciones *en el tiempo*, complementando los diagramas de casos de uso y clases de G03.

## 2. Frontera y notación

- **Actor:** Productor o Encargado del almacén.
- **Boundary:** `ProductorController` o `EncargadoSolicitudController` (interacción web).
- **Control:** servicios con reglas de negocio, transacciones y auditoría.
- **Entity / Repository:** repositorios Spring Data JPA responsables del acceso a datos.
- **Mensajes sólidos:** invocaciones. **Mensajes discontinuos:** respuestas.
- **alt:** rutas alternativas de éxito o error. **note:** reglas de negocio o simplificaciones.
- Las láminas PNG/SVG son **resúmenes del camino principal** para la exposición; los archivos `.puml` contienen los diagramas UML con alternativas y validaciones. La vista general condensa los tres momentos.

## 3. Secuencia 01 — Registro de solicitud (RF06)

1. El productor envía `POST /productor/solicitudes` a `ProductorController.registrarSolicitud()`.
2. El controlador aplica `@Valid`, controla `BindingResult` y llama a `SolicitudService.registrar(username, req)`.
3. El servicio recupera al productor (`ProductorRepository.findByUsername`), valida cantidad, fecha y motivo, y busca el material con inventario (`MaterialRepository.findByIdConInventario`).
4. `InventarioService.validarDisponibilidad(material, cantidad)` comprueba que esté activo y que haya unidades suficientes.
5. Si todo es válido, se crea y guarda la `Solicitud`, con estado inicial `PENDIENTE`.
6. `HistorialService.registrar` genera la evidencia de auditoría `REGISTRAR_SOLICITUD`.
7. Se devuelve `SolicitudResponse`; el controlador redirige a `/productor/solicitudes` con confirmación.
8. Si el formulario o una regla de negocio falla, se muestra el formulario con el error correspondiente.

**Regla clave:** registrar la solicitud **no descuenta** stock del inventario. El registro prueba disponibilidad en ese momento.

## 4. Secuencia 02 — Aprobación (RF07)

1. El encargado envía `POST /encargado/solicitudes/{id}/aprobar`.
2. El controlador llama a `SolicitudService.aprobar(id, usernameEncargado)`.
3. `UsuarioService` resuelve al encargado y `SolicitudRepository.findByIdParaActualizar(id)` obtiene la solicitud con **bloqueo pesimista** (`@Lock(PESSIMISTIC_WRITE)`).
4. Se verifica que el estado sea exactamente `PENDIENTE`.
5. Se vuelve a validar la disponibilidad mediante `InventarioService.validarDisponibilidad`.
6. Con las reglas satisfechas, se establece `APROBADA`, se almacena el encargado y fecha de respuesta, y se registra `APROBAR_SOLICITUD` en el historial.
7. El controlador redirige al detalle con confirmación. Si falla el estado o la disponibilidad, redirige con mensaje de error.

**Regla clave:** aprobar **no descuenta** inventario ni crea préstamo. La aprobación es una decisión administrativa diferente de la entrega física.

## 5. Secuencia 03 — Entrega física (RF08)

1. El encargado envía `POST /encargado/solicitudes/{id}/entregar`.
2. `EncargadoSolicitudController.entregar` invoca `PrestamoService.registrarEntrega(id, usernameEncargado)`.
3. El servicio obtiene al encargado, bloquea la solicitud con `findByIdParaActualizar` y comprueba que no esté entregada ni exista ya un préstamo (`PrestamoRepository.existsBySolicitudId`).
4. Verifica que su estado sea `APROBADA` y que el material esté activo.
5. Llama a `InventarioService.descontarPorEntrega`, el cual bloquea la fila de inventario mediante `InventarioRepository.findByMaterialIdParaActualizar`.
6. Revalida stock; si es suficiente: **cantidadDisponible -= cantidad**, **cantidadPrestada += cantidad**. Verifica el cuadre y persiste el inventario.
7. `InventarioService` registra un movimiento de tipo `ENTREGA` en `MovimientoInventarioRepository` (este repositorio se oculta en el diagrama visual por legibilidad).
8. `PrestamoRepository.save` crea el nuevo préstamo; la solicitud pasa a `ENTREGADA`.
9. Se registra el evento `REGISTRAR_ENTREGA` en auditoría; se devuelve un `PrestamoResponse` y el controlador muestra confirmación.
10. Si falla alguna validación o la disponibilidad, se devuelve una excepción de negocio y la transacción evita dejar un préstamo inconsistente.

**Reglas clave:** un préstamo por solicitud, entrega solo desde `APROBADA`, actualización consistente de inventario con bloqueo y transacción.

## 6. Matriz de correspondencia con el código

| Proceso | Controlador / método | Servicio / método | Repositorios y colaboradores | RF |
|---|---|---|---|---|
| Registrar solicitud | `ProductorController.registrarSolicitud` | `SolicitudService.registrar` | `ProductorRepository`, `MaterialRepository`, `InventarioService`, `SolicitudRepository`, `HistorialService` | RF06 |
| Aprobar solicitud | `EncargadoSolicitudController.aprobar` | `SolicitudService.aprobar` | `UsuarioService`, `SolicitudRepository`, `InventarioService`, `HistorialService` | RF07 |
| Registrar entrega | `EncargadoSolicitudController.entregar` | `PrestamoService.registrarEntrega` | `UsuarioService`, `SolicitudRepository`, `PrestamoRepository`, `InventarioService`, `InventarioRepository`, `MovimientoInventarioRepository`, `HistorialService` | RF08 |

## 7. Riesgos y decisiones de diseño observadas

| Decisión | Justificación técnica | Efecto en calidad |
|---|---|---|
| Separar `aprobar` de `registrarEntrega` | La aprobación es una decisión; la entrega registra salida física | Mejor trazabilidad y estados consistentes |
| Bloquear solicitud durante aprobación y entrega | Evita operaciones competitivas sobre la misma solicitud | Reduce condiciones de carrera |
| Volver a comprobar stock en la entrega | El stock pudo cambiar desde registro o aprobación | Previene inventario negativo |
| Centralizar movimientos en `InventarioService` | Un único punto aplica reglas de cuadre y registra movimientos | Coherencia y mantenibilidad |
| Registrar historial dentro del servicio | Conserva actor, acción, entidad y momento | Auditabilidad y diagnóstico |

## 8. Alcance, fuentes y reproducibilidad

Fuentes consultadas en el repositorio `MauricioZegarra/AgroRetorno`:

- `src/main/java/pe/edu/agroretorno/controller/ProductorController.java`
- `src/main/java/pe/edu/agroretorno/controller/EncargadoSolicitudController.java`
- `src/main/java/pe/edu/agroretorno/service/SolicitudService.java`
- `src/main/java/pe/edu/agroretorno/service/PrestamoService.java`
- `src/main/java/pe/edu/agroretorno/service/InventarioService.java`
- `src/main/java/pe/edu/agroretorno/service/HistorialService.java`
- `src/main/java/pe/edu/agroretorno/repository/SolicitudRepository.java`
- `src/main/java/pe/edu/agroretorno/repository/InventarioRepository.java`

Las imágenes representan la secuencia principal (vista condensada); las ramas de error y decisiones se detallan en los `.puml`. Este trabajo es un modelo estático del código, **no evidencia de haber ejecutado la aplicación**. Al realizar la revisión académica, verificar consistencia con la versión que entregue el equipo.

## 9. Cómo usar estos archivos

1. Abrir los `.puml` en un visor PlantUML compatible para ajustar y exportar el diagrama UML completo.
2. Insertar `.svg` en la presentación o `.png` en el SDD.
3. Anexar los archivos `.puml` como originales editables.
4. Completar la lista de revisión de `06_Revision` después de inspeccionar los diagramas.
5. Registrar captura de la **revisión real** en `05_Capturas` y no confundirla con un resultado de ejecución de software.
