# ED-02 — Evaluar solicitud y registrar entrega física

**Proyecto:** AgroRetorno  |  **Guía:** G03  |  **Responsable:** Cielo Cristal Meza Vizcarra
**Estado:** Especificación derivada de código; pendiente de revisión académica.

**Requisitos vinculados:** RF09, RF10, RF11
**Actores:** Encargado (principal); sistema AgroRetorno (interno)
**Objetivo:** Permitir que el encargado revise una solicitud pendiente, la apruebe o rechace y, si ha sido aprobada, registre la entrega física sin generar préstamos duplicados.
**Disparador:** El encargado abre la lista de solicitudes y entra en el detalle de una solicitud PENDIENTE.

## 1. Precondiciones
1. El actor inició sesión con rol ENCARGADO.
2. Existe una Solicitud PENDIENTE asociada a un productor y un material.
3. La entrega requiere que la solicitud esté APROBADA y que el material permanezca ACTIVO y disponible.

## 2. Flujo principal de éxito

| Paso | Responsable | Actividad | Evidencia en código |
|---:|---|---|---|
| 1 | Encargado | Abre «Solicitudes» y consulta una solicitud PENDIENTE. | `GET /encargado/solicitudes; GET /encargado/solicitudes/{id}` |
| 2 | Sistema | Presenta datos de solicitud, productor, disponibilidad actual y las acciones permitidas. | `encargado/solicitud-detalle.html` |
| 3 | Encargado | Selecciona «Aprobar solicitud». | `POST /encargado/solicitudes/{id}/aprobar` |
| 4 | Sistema | Bloquea la solicitud, comprueba estado PENDIENTE y disponibilidad, y la marca APROBADA. | `SolicitudService.aprobar; SolicitudRepository.findByIdParaActualizar` |
| 5 | Sistema | Registra quién respondió y cuándo, y agrega una entrada de historial. No cambia el inventario. | `SolicitudService.aprobar; HistorialService.registrar` |
| 6 | Encargado | Cuando se produce la entrega real, pulsa «Registrar entrega física». | `POST /encargado/solicitudes/{id}/entregar` |
| 7 | Sistema | Valida solicitud APROBADA, ausencia de préstamo previo y material ACTIVO. | `PrestamoService.registrarEntrega` |
| 8 | Sistema | Bloquea el inventario, revalida stock, disminuye disponibles y aumenta unidades prestadas. | `InventarioService.descontarPorEntrega` |
| 9 | Sistema | Crea Prestamo ACTIVO, cambia Solicitud a ENTREGADA y registra evento de entrega. | `PrestamoRepository.save; HistorialService.registrar` |
| 10 | Sistema | Muestra confirmación y enlace al préstamo generado. | `encargado/solicitud-detalle.html` |

## 3. Flujos alternativos y excepciones

| ID | Situación | Resultado |
|---|---|---|
| A1 · Rechazar solicitud | Desde PENDIENTE, o APROBADA antes de entrega, el encargado puede rechazar con motivo válido. | Estado RECHAZADA; no se crea préstamo. |
| A2 · Sin existencias al aprobar | La aprobación falla si ya no existe stock suficiente. | Solicitud permanece sin aprobar. |
| A3 · Stock cambia antes de entrega | Si el stock se redujo desde la aprobación, la revalidación durante la entrega impide saldos negativos. | No se completa la entrega. |
| A4 · Entrega duplicada o no aprobada | Si ya existe préstamo o el estado no es APROBADA, no se permite registrar la entrega. | Sin segundo préstamo. |
| A5 · Material inactivo | Se impide registrar la entrega aunque la solicitud esté aprobada. | Inventario y préstamo sin cambios. |
| A6 · Actor sin permiso | Las rutas y métodos exigen ENCARGADO. | Acceso denegado. |

## 4. Postcondiciones
**Éxito:** La solicitud queda ENTREGADA; se crea exactamente un préstamo ACTIVO; las existencias disponibles disminuyen y las prestadas aumentan por igual cantidad. Se conserva la trazabilidad de la operación.
**Fallo:** La operación no completa sus efectos; si no se logra entregar, no se registra un nuevo préstamo válido.

## 5. Reglas de negocio
- RN-06: solo se aprueba una solicitud PENDIENTE.
- RN-07: puede rechazarse desde PENDIENTE o APROBADA, pero no desde ENTREGADA o RECHAZADA.
- RN-08: la aprobación no descuenta stock; la entrega es una operación separada.
- RN-09: la entrega exige APROBADA, stock vigente, material activo y ausencia de préstamo previo.
- RN-10: controles de concurrencia mediante bloqueo de filas, transacciones y unicidad de solicitud_id en préstamos.

## 6. Referencias al código de AgroRetorno
- `src/main/java/pe/edu/agroretorno/controller/EncargadoSolicitudController.java`
- `src/main/java/pe/edu/agroretorno/service/SolicitudService.java`
- `src/main/java/pe/edu/agroretorno/service/PrestamoService.java`
- `src/main/java/pe/edu/agroretorno/service/InventarioService.java`
- `src/main/java/pe/edu/agroretorno/dto/RechazoRequest.java`
- `src/main/resources/templates/encargado/solicitudes.html`
- `src/main/resources/templates/encargado/solicitud-detalle.html`

## 7. Condiciones de verificación propuestas (no ejecutadas)
- CT-CU02-01: aprobar PENDIENTE y verificar que inventario no varíe.
- CT-CU02-02: rechazar con motivo y verificar estado RECHAZADA.
- CT-CU02-03: entregar APROBADA y verificar préstamo e inventario.
- CT-CU02-04: impedir segunda entrega de la misma solicitud.
- CT-CU02-05: impedir entrega si stock dejó de ser suficiente.

## 8. Mockups correspondientes
- `G03_05_M02_Revision.html`
- `G03_05_M03_Entrega.html`

> Nota de integridad: los casos de prueba son condiciones diseñadas para comprobación futura; este documento no constituye evidencia de ejecución de pruebas o validación con usuarios.
