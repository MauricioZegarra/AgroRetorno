# G07 — Catálogo de Pruebas, Cobertura de Casos y Verificación de Mockito (Pasos 3.10–3.13)

## 1. Componentes de negocio evaluados (4 servicios > mínimo de 3)
1. **`InventarioService`** (`InventarioServiceTest`, `PU-01`–`PU-15`, 17 ejecuciones): disponibilidad, entregas con descuento, retornos clasificados y ajustes de inventario con invariante `total = disponible + prestada + dañada + mantenimiento`.
2. **`SolicitudService`** (`SolicitudServiceTest`, `PU-16`–`PU-27`, 21 ejecuciones): registro, validaciones de entradas y plazos (`1..90` días), aprobación, rechazo con motivo y aislamiento de datos por propietario.
3. **`PrestamoService`** (`PrestamoServiceTest`, `PU-28`–`PU-33`, 6 ejecuciones): entrega física, revalidación de stock, bloqueo de entregas sin aprobación o duplicadas y detección de vencimientos.
4. **`DevolucionService`** (`DevolucionServiceTest`, `PU-34`–`PU-43`, 13 ejecuciones): devoluciones totales y parciales acumuladas, clasificación (buen estado, dañadas, mantenimiento), cierre automático y bloqueo sobre préstamos cerrados.

## 2. Verificación del uso de Mockito y determinismo (Paso 3.13)
- Todas las clases unitarias están anotadas con `@ExtendWith(MockitoExtension.class)` y `@MockitoSettings(strictness = Strictness.LENIENT)`.
- Los repositorios (`InventarioRepository`, `MaterialRepository`, `MovimientoInventarioRepository`, `SolicitudRepository`, `PrestamoRepository`, `DevolucionRepository`, `ProductorRepository`) y servicios colaboradores (`UsuarioService`, `HistorialService`) se aíslan con `@Mock` y stubs `when(...).thenReturn(...)` / `thenAnswer(...)`.
- Se verifica explícitamente la **no persistencia** ante fallos mediante `verify(..., never()).save(any())` y se inspeccionan objetos persistidos con `ArgumentCaptor<MovimientoInventario>`.
- Todas las fechas dependen de un reloj determinista fijo (`Fixtures.RELOJ` = `2026-11-17T10:00 America/Lima`), garantizando resultados reproducibles en cualquier día de ejecución.

## 3. Cobertura de tipos de prueba (Paso 3.12)
- **Casos válidos (happy path):** `PU-01`, `PU-06`, `PU-07`, `PU-09`, `PU-11`, `PU-12`, `PU-14`, `PU-15`, `PU-16`, `PU-22`, `PU-24`, `PU-27`, `PU-28`, `PU-33`, `PU-34`, `PU-35`, `PU-36`, `PU-37`, `PU-39`, `PU-43`.
- **Casos inválidos (parametrizados y simples):** `PU-03`, `PU-04` (`{0, -1, -20}`), `PU-08`, `PU-10`, `PU-13`, `PU-17` (10 combinaciones vía `@MethodSource`), `PU-23`, `PU-26`, `PU-29`, `PU-30`, `PU-31`, `PU-41` (`(0,0,0), (-1,0,0), (0,-2,0), (3,0,-1)` vía `@CsvSource`), `PU-42`.
- **Valores límite:** `PU-05` (50 exacto vs. 51), `PU-18` (`HOY+1` y `HOY+90` exactos), `PU-19` (51 > 50), `PU-34` (20 de 20 exactos), `PU-38` (21 > 20), `PU-39` (cierre exacto acumulado `7+8+5=20`).
- **Excepciones controladas:** `RecursoNoEncontradoException` (`PU-02`, `PU-20`, `PU-27`, `PU-32`, `PU-33`, `PU-42`), `ValidacionException` (`PU-04`, `PU-13`, `PU-15`, `PU-17`, `PU-26`, `PU-41`, `PU-42`), `ReglaNegocioException` (`PU-05`, `PU-08`, `PU-10`, `PU-13`, `PU-19`, `PU-20`, `PU-23`, `PU-25`, `PU-26`, `PU-29`, `PU-30`, `PU-31`, `PU-32`, `PU-38`, `PU-40`) y `OperacionNoPermitidaException` (`PU-21`, `PU-33`, `PU-43`).

El catálogo detallado caso por caso (43 PU + 15 IT/SEG) con fórmulas de conteo y trazabilidad a `RF01–RF15` y `RNF01–RNF10` se encuentra en el libro editable `G07_Catalogo_Pruebas_Unitarias_AgroRetorno.xlsx`.
