# G03 — Paso 1.1. Análisis de la arquitectura real de AgroRetorno

**Responsable:** Cielo Cristal Meza Vizcarra  
**Curso:** Calidad de Software — 2026-B  
**Fecha de análisis inicial:** 23 de noviembre de 2026  
**Fuente:** repositorio `MauricioZegarra/AgroRetorno`, rama `main`, inspección de archivos Java y migración SQL.

## 1. Objetivo
Reconocer los componentes existentes, las dependencias principales y el estilo arquitectónico implementado, como entrada verificable para el SDD de G03.

## 2. Estilo arquitectónico
El sistema está implementado como **aplicación monolítica Spring Boot** con separación lógica por responsabilidades y patrón MVC para la interfaz web. Contiene también controladores REST que utilizan la misma capa de servicios. No debe describirse como arquitectura de microservicios.

## 3. Componentes identificados

| Componente | Evidencia de código | Responsabilidad |
|---|---|---|
| Presentación | `src/main/resources/templates/` y `static/` | Vistas Thymeleaf, formularios, estilos y JavaScript. |
| Control web MVC | `controller/ProductorController.java`, `controller/EncargadoSolicitudController.java`, `controller/EncargadoPrestamoController.java` | Recibir solicitudes del navegador, coordinar el caso de uso y devolver vistas. |
| Control REST | `api/ProductorApiController.java`, `api/EncargadoApiController.java` | Exponer rutas HTTP para consumidores y pruebas JMeter. |
| Seguridad | `security/SecurityConfig.java` | Rutas por rol, sesión web, CSRF web, HTTP Basic en API y BCrypt. |
| Servicios | `service/SolicitudService.java`, `PrestamoService.java`, `DevolucionService.java`, `InventarioService.java`, otros | Implementar validaciones y transacciones de los tres procesos. |
| Persistencia | `repository/*Repository.java` y entidades de `entity/` | Acceso a datos mediante Spring Data JPA / Hibernate. |
| Base de datos | `db/migration/V1__esquema_inicial.sql` | Esquema PostgreSQL con diez tablas y restricciones. |
| Evolución del esquema | Flyway habilitado en `application.properties` | Aplicar las migraciones versionadas. |

## 4. Tres procesos de negocio confirmados

1. **Solicitud:** el Productor registra una solicitud con material, cantidad y fecha; la solicitud queda pendiente sin disminuir existencias.
2. **Aprobación y entrega:** el Encargado aprueba/rechaza; al entregar físicamente, `PrestamoService` revalida y `InventarioService` actualiza saldos.
3. **Devolución y cierre:** el Encargado registra unidades en buen estado, dañadas o en mantenimiento. `DevolucionService` acepta devoluciones parciales y cierra el préstamo cuando no quedan pendientes.

## 5. Decisiones de diseño que deben explicarse en el SDD

- Separar controladores de reglas de negocio mediante servicios.
- Reutilizar servicios de negocio tanto desde MVC como desde API REST.
- Asegurar autorización por rol en la capa servidor, no solo ocultando opciones del menú.
- Proteger la consistencia de cantidades mediante validaciones transaccionales, bloqueos en repositorios y restricciones de base de datos.
- Registrar historial de operaciones relevantes para trazabilidad.
- Mantener la estructura física de la base con migraciones Flyway.

## 6. Correspondencia con la ERS
La documentación de primer avance ya define **15 RF y 10 RNF**. Algunas anotaciones/comentarios del código todavía usan números RF de una línea base anterior; al redactar la trazabilidad se deben usar los IDs de la ERS vigente, sin asumir que las etiquetas antiguas del código coinciden.

## 7. Fuentes verificadas
- `src/main/java/pe/edu/agroretorno/AgroRetornoApplication.java`
- `src/main/java/pe/edu/agroretorno/security/SecurityConfig.java`
- `src/main/java/pe/edu/agroretorno/controller/ProductorController.java`
- `src/main/java/pe/edu/agroretorno/controller/EncargadoSolicitudController.java`
- `src/main/java/pe/edu/agroretorno/controller/EncargadoPrestamoController.java`
- `src/main/java/pe/edu/agroretorno/service/SolicitudService.java`
- `src/main/java/pe/edu/agroretorno/service/PrestamoService.java`
- `src/main/java/pe/edu/agroretorno/service/DevolucionService.java`
- `src/main/java/pe/edu/agroretorno/repository/SolicitudRepository.java`
- `src/main/resources/db/migration/V1__esquema_inicial.sql`
- `src/main/resources/application.properties`

## 8. Evidencias pendientes de completar localmente
- Captura de la arquitectura abierta en PlantUML / diagrams.net.
- Captura del commit y push de la rama personal.
- Archivo final exportado PNG/SVG desde el editor con la versión identificada.

## 9. Estado
**Borrador inicial basado en código revisado.** Pendiente revisión visual y commit en la rama personal.
