# CU-01 — Registrar solicitud de préstamo

**Proyecto:** AgroRetorno  |  **Guía:** G03  |  **Responsable:** Cielo Cristal Meza Vizcarra
**Estado:** Especificación derivada de código; pendiente de revisión académica.

**Requisitos vinculados:** RF06
**Actores:** Productor (principal); sistema AgroRetorno (interno)
**Objetivo:** Permitir que un productor autenticado solicite una cantidad de un material agrícola reutilizable, indicando una fecha prevista de devolución y un motivo.
**Disparador:** El productor selecciona «Solicitar» desde el inventario disponible o abre «Nueva solicitud».

## 1. Precondiciones
1. El actor inició sesión con rol PRODUCTOR; la ruta /productor/** está protegida.
2. Existe un material ACTIVO con inventario consultable.
3. El productor está asociado a su cuenta de usuario.

## 2. Flujo principal de éxito

| Paso | Responsable | Actividad | Evidencia en código |
|---:|---|---|---|
| 1 | Productor | Abre «Inventario disponible» y localiza el material mediante búsqueda y/o categoría. | `ProductorController.inventario; GET /productor/inventario` |
| 2 | Sistema | Muestra materiales, cantidad disponible y acción «Solicitar» si hay stock. | `productor/inventario.html` |
| 3 | Productor | Selecciona «Solicitar» y abre el formulario de solicitud. | `GET /productor/solicitudes/nueva?materialId=...` |
| 4 | Sistema | Muestra material, cantidad, fecha prevista y motivo; la cantidad inicial es 1. | `productor/solicitud-form.html` |
| 5 | Productor | Introduce cantidad positiva, fecha de devolución y motivo, y pulsa «Enviar solicitud». | `POST /productor/solicitudes` |
| 6 | Sistema | Valida los datos del formulario (@Valid) y deriva el registro a SolicitudService.registrar. | `ProductorController.registrarSolicitud` |
| 7 | Sistema | Valida productor, reglas de fecha y motivo, material activo y disponibilidad existente. | `SolicitudService.validarDatos; InventarioService.validarDisponibilidad` |
| 8 | Sistema | Guarda una entidad Solicitud con estado PENDIENTE y registra el evento en el historial. | `SolicitudRepository.save; HistorialService.registrar` |
| 9 | Sistema | Redirige a «Mis solicitudes» con aviso de registro y número de solicitud. | `redirect:/productor/solicitudes` |

## 3. Flujos alternativos y excepciones

| ID | Situación | Resultado |
|---|---|---|
| A1 · Formulario inválido | Si falta material, la cantidad no es positiva, la fecha está fuera del intervalo permitido o el motivo no cumple la longitud, se vuelve a mostrar el formulario y se indica el error. | Sin nueva solicitud. |
| A2 · Stock insuficiente | Si la cantidad solicitada supera la disponibilidad, SolicitudService rechaza la operación. | No se persiste la solicitud. |
| A3 · Material no apto | Si el material no existe o está INACTIVO, se rechaza el registro. | Sin nueva solicitud. |
| A4 · Rol incorrecto | Una persona sin rol PRODUCTOR no puede utilizar el flujo del productor. | Acceso denegado por seguridad. |

## 4. Postcondiciones
**Éxito:** Se crea una solicitud PENDIENTE, queda asociada al productor y material y existe un evento de historial. El inventario NO disminuye todavía.
**Fallo:** No se registra una nueva solicitud válida; la transacción de registro no completa la operación.

## 5. Reglas de negocio
- RN-01: cantidad > 0 y <= 10000 en el DTO; además no puede superar el stock disponible al validar.
- RN-02: fecha prevista dentro de hoy + plazo mínimo y hoy + plazo máximo (valores configurables; 1 y 90 días por defecto).
- RN-03: motivo obligatorio, mínimo configurable de 5 caracteres y máximo 300.
- RN-04: material ACTIVO y actor con rol PRODUCTOR.
- RN-05: registrar una solicitud no descuenta existencias; el descuento ocurre durante la entrega.

## 6. Referencias al código de AgroRetorno
- `src/main/java/pe/edu/agroretorno/controller/ProductorController.java`
- `src/main/java/pe/edu/agroretorno/service/SolicitudService.java`
- `src/main/java/pe/edu/agroretorno/service/InventarioService.java`
- `src/main/java/pe/edu/agroretorno/dto/SolicitudRequest.java`
- `src/main/resources/templates/productor/inventario.html`
- `src/main/resources/templates/productor/solicitud-form.html`

## 7. Condiciones de verificación propuestas (no ejecutadas)
- CT-CU01-01: registrar solicitud válida y observar estado PENDIENTE.
- CT-CU01-02: impedir cantidad mayor que stock.
- CT-CU01-03: impedir fecha fuera de plazo o motivo muy corto.
- CT-CU01-04: confirmar que el inventario se conserva tras registrar.

## 8. Mockups correspondientes
- `G03_05_M01_Solicitud.html`

> Nota de integridad: los casos de prueba son condiciones diseñadas para comprobación futura; este documento no constituye evidencia de ejecución de pruebas o validación con usuarios.
