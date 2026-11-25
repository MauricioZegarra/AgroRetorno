# G06 — Catálogo de Activos, Escenarios Activo–Amenaza y Valoración MAGERIT v3 (Pasos 3.2–3.5)

## 1. Activos identificados en AgroRetorno v1.0.0 (8 activos ≥ 5 exigidos)
| ID | Tipo MAGERIT | Nombre | Responsable | Depende de | C/I/D/A/T | V (Máx) |
|---|---|---|---|---|---:|---:|
| `ACT-01` | `[D] Datos / BD` | Base de datos PostgreSQL (`V1__esquema_inicial.sql`, 10 tablas) | Administrador TI / Encargado | `ACT-06` | 4/5/5/3/4 | 5 |
| `ACT-02` | `[D] Credenciales` | Credenciales y cuentas (`usuarios.password_hash`, `DB_PASSWORD`, demo) | Admin. Seguridad / Encargado | `ACT-01, ACT-05` | 5/5/4/5/4 | 5 |
| `ACT-03` | `[D] Datos operativos` | Registros de solicitudes, préstamos, devoluciones e inventario | Encargado de Almacén | `ACT-01, ACT-05` | 3/5/5/4/5 | 5 |
| `ACT-04` | `[D] Datos personales` | Datos de productores agrícolas (DNI, nombres, teléfono, sector) | Encargado / Cooperativa | `ACT-01, ACT-05` | 5/4/3/3/3 | 5 |
| `ACT-05` | `[SW] Aplicación` | Aplicación web MVC y API REST (`Spring Boot 3.5.16` / Security 6) | Equipo Desarrollo / Marco Marquez | `ACT-06, ACT-01` | 3/5/5/4/4 | 5 |
| `ACT-06` | `[HW/S] Servidor` | Servidor de despliegue (Render / Docker + JVM + HikariCP pool=10) | Responsable Infraestructura | `ACT-07` | 3/4/5/3/3 | 5 |
| `ACT-07` | `[COM] Red / Canal` | Canal de comunicaciones HTTP/HTTPS (`JSESSIONID` y HTTP Basic) | Responsable Infraestructura | Ninguno | 5/4/4/4/3 | 5 |
| `ACT-08` | `[S] Auditoría` | Servicio de historial, movimientos y respaldos (`HistorialService`) | Encargado / Auditor Calidad | `ACT-01, ACT-05, ACT-06` | 3/5/4/4/5 | 5 |

## 2. Escenarios Activo–Amenaza y resultados recalculados (10 escenarios ≥ 8 exigidos)
Valores exactos extraídos del libro `03_Matriz_MAGERIT_Editable/G06_Matriz_Riesgos_MAGERIT_AgroRetorno.xlsx` (220 fórmulas, 0 errores):

| ID | Activo | Amenaza MAGERIT | Deg. | Imp. Inh. | Frec. | R. Inh. | Nivel Inh. | Rank | Estado Control | R. Res. | Nivel Res. | Reducción |
|---|---|---|---:|---:|---:|---:|---|---:|---|---:|---|---:|
| `ESC-01` | `ACT-05` | `[A.11]` Acceso no autorizado a rutas de encargado | 80 % | 4.00 | 4 | 16.00 | CRÍTICO | #3 | Implementado y probado (T1) | 1.40 | BAJO | -91.2 % |
| `ESC-02` | `ACT-03` | `[E.1/A.15]` Manipulación/error de cantidades e inventario | 80 % | 4.00 | 5 | 20.00 | CRÍTICO | #1 | Implementado y probado (T2) | 1.00 | BAJO | -95.0 % |
| `ESC-03` | `ACT-02` | `[A.5]` Credenciales demo (`DEMO_DATA=true`) / fuerza bruta | 100 % | 5.00 | 4 | 20.00 | CRÍTICO | #1 | Parcial / Planificado | 3.00 | BAJO | -85.0 % |
| `ESC-04` | `ACT-04` | `[A.11]` Acceso IDOR a solicitudes de otro productor | 80 % | 4.00 | 4 | 16.00 | CRÍTICO | #3 | Implementado y probado (T1) | 1.20 | BAJO | -92.5 % |
| `ESC-05` | `ACT-05` | `[A.22]` Falsificación de peticiones entre sitios (CSRF) | 80 % | 4.00 | 3 | 12.00 | ALTO | #5 | Implementado y probado (T1) | 1.20 | BAJO | -90.0 % |
| `ESC-06` | `ACT-03` | `[E.20/E.21]` Condición de carrera en entregas simultáneas | 80 % | 4.00 | 3 | 12.00 | ALTO | #5 | Implementado y probado (T2) | 1.00 | BAJO | -91.7 % |
| `ESC-07` | `ACT-01` | `[I.5/E.2]` Avería lógica o pérdida de BD sin respaldo probado | 100 % | 5.00 | 2 | 10.00 | ALTO | #8 | Planificado | 1.50 | BAJO | -85.0 % |
| `ESC-08` | `ACT-07` | `[A.14]` Interceptación de HTTP Basic / credenciales sin TLS | 80 % | 4.00 | 3 | 12.00 | ALTO | #5 | Parcial / Planificado | 1.47 | BAJO | -87.8 % |
| `ESC-09` | `ACT-06` | `[A.24/I.6]` Saturación / hibernación de servidor y pool HikariCP | 50 % | 2.50 | 4 | 10.00 | ALTO | #8 | Planificado (G08) | 2.70 | BAJO | -73.0 % |
| `ESC-10` | `ACT-08` | `[A.13]` Repudio / pérdida de trazabilidad operativa | 50 % | 2.50 | 3 | 7.50 | MEDIO | #10 | Implementado y probado | 0.92 | BAJO | -87.8 % |
| **Prom.** | — | **Resumen global (10 escenarios)** | — | **3.90** | **3.50** | **13.55** | **4C / 5A / 1M** | — | **6 probados / 4 planif.** | **1.54** | **10 Bajos** | **-88.6 %** |
