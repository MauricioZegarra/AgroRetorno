# CU-03 — Registrar devolución y cerrar préstamo

**Proyecto:** AgroRetorno  |  **Guía:** G03  |  **Responsable:** Cielo Cristal Meza Vizcarra
**Estado:** Especificación derivada de código; pendiente de revisión académica.

**Requisitos vinculados:** RF09
**Actores:** Encargado (principal); sistema AgroRetorno (interno)
**Objetivo:** Registrar una devolución total o parcial, clasificar materiales por condición y actualizar existencias y saldos del préstamo.
**Disparador:** El encargado abre el detalle de un préstamo ACTIVO y selecciona «Registrar devolución».

## 1. Precondiciones
1. El encargado inició sesión con rol ENCARGADO.
2. Existe un préstamo ACTIVO con cantidad pendiente mayor que cero.
3. Se conoce la clasificación de unidades recibidas: buen estado, dañadas y mantenimiento.

## 2. Flujo principal de éxito

| Paso | Responsable | Actividad | Evidencia en código |
|---:|---|---|---|
| 1 | Encargado | Abre «Préstamos» y selecciona el detalle del préstamo ACTIVO. | `GET /encargado/prestamos/{id}` |
| 2 | Sistema | Muestra indicadores de entregadas, devueltas y pendientes, más el historial de devoluciones. | `encargado/prestamo-detalle.html` |
| 3 | Encargado | Indica cantidades por condición y observaciones, si corresponde. | `DevolucionRequest` |
| 4 | Encargado | Selecciona «Registrar devolución». | `POST /encargado/prestamos/{id}/devoluciones` |
| 5 | Sistema | Valida valores no negativos; suma positiva; total no mayor que lo pendiente. | `DevolucionService.registrar` |
| 6 | Sistema | Si existen daños o mantenimiento, exige observaciones; verifica préstamo ACTIVO. | `DevolucionService.registrar` |
| 7 | Sistema | Bloquea inventario, descuenta unidades prestadas y reparte entre disponibles, dañadas y mantenimiento. | `InventarioService.registrarRetorno` |
| 8 | Sistema | Acumula cantidades devueltas, crea Devolucion y sus DetalleDevolucion, y registra los movimientos. | `DevolucionService.registrar` |
| 9 | Sistema | Si ya no quedan unidades pendientes, marca préstamo CERRADO y establece fecha de cierre; si quedan, conserva ACTIVO. | `DevolucionService.registrar` |
| 10 | Sistema | Muestra confirmación, saldos actualizados e historial de devoluciones. | `encargado/prestamo-detalle.html` |

## 3. Flujos alternativos y excepciones

| ID | Situación | Resultado |
|---|---|---|
| A1 · Devolución parcial | La cantidad recibida es menor que el saldo pendiente. | Se guarda la devolución; préstamo continúa ACTIVO. |
| A2 · Devolución total | La cantidad recibida cubre exactamente el pendiente. | Préstamo CERRADO con fecha de cierre. |
| A3 · Cantidades inválidas | Negativos, suma cero o cantidad total superior a la pendiente. | Operación rechazada sin devolución válida. |
| A4 · Daño sin observación | Hay unidades dañadas o en mantenimiento sin observación descriptiva. | Se exige observación para continuar. |
| A5 · Préstamo cerrado | Se intenta recibir nuevas unidades sobre un préstamo CERRADO. | Operación rechazada. |
| A6 · Acceso no autorizado | Un usuario sin rol ENCARGADO intenta registrar devolución. | Acceso denegado. |

## 4. Postcondiciones
**Éxito:** Se guardan la devolución, sus detalles y movimientos; se actualizan inventario y cantidades acumuladas del préstamo. Se mantiene ACTIVO o pasa a CERRADO según saldo pendiente.
**Fallo:** No se confirma una devolución inválida ni se altera el saldo del préstamo.

## 5. Reglas de negocio
- RN-11: cantidades por condición >= 0 y suma total > 0.
- RN-12: suma recibida <= unidades pendientes del préstamo.
- RN-13: observaciones obligatorias cuando hay dañadas o mantenimiento; máximo 500 caracteres según DTO.
- RN-14: buen estado incrementa disponibles; dañadas y mantenimiento se clasifican como no disponibles.
- RN-15: cierre solo si cantidadPendiente == 0; permitidas múltiples devoluciones parciales.

## 6. Referencias al código de AgroRetorno
- `src/main/java/pe/edu/agroretorno/controller/EncargadoPrestamoController.java`
- `src/main/java/pe/edu/agroretorno/service/DevolucionService.java`
- `src/main/java/pe/edu/agroretorno/service/InventarioService.java`
- `src/main/java/pe/edu/agroretorno/dto/DevolucionRequest.java`
- `src/main/resources/templates/encargado/prestamo-detalle.html`

## 7. Condiciones de verificación propuestas (no ejecutadas)
- CT-CU03-01: registrar devolución parcial y confirmar estado ACTIVO.
- CT-CU03-02: devolver todo lo pendiente y confirmar CERRADO.
- CT-CU03-03: registrar dañadas sin observación y verificar rechazo.
- CT-CU03-04: superar cantidad pendiente y verificar rechazo.
- CT-CU03-05: comprobar ecuación de inventario tras devolución.

## 8. Mockups correspondientes
- `G03_05_M04_Devolucion.html`

> Nota de integridad: los casos de prueba son condiciones diseñadas para comprobación futura; este documento no constituye evidencia de ejecución de pruebas o validación con usuarios.
