# G04 — Paso 1.1. Precátalogo para NESMA
**Responsable:** Cielo Cristal Meza Vizcarra  
**Estado:** inventario de candidatos, no conteo final.

## Paso 1. Identificar candidatos a ILF
Se registran en `G04_01_Frontera_y_Linea_Base.xlsx` → pestaña **Datos**. La base física tiene diez tablas, pero ocho *grupos lógicos candidatos*. La agrupación no está confirmada hasta evaluar si cada grupo es reconocible para el usuario y mantenido por AgroRetorno.

- D01 Cuentas y productores: `usuarios`, `productores`. Revisar si se requiere separación por mantenimiento o propósito.
- D02 Materiales: `materiales`.
- D03 Existencias: `inventario`. Revisar separación de Materiales.
- D04 Solicitudes: `solicitudes`.
- D05 Préstamos: `prestamos`.
- D06 Devoluciones: `devoluciones`, `detalles_devolucion`.
- D07 Movimientos: `movimientos_inventario`.
- D08 Historial: `historial_operaciones`. Confirmar el reconocimiento del usuario.

## Paso 2. Revisar candidatos a EIF
Aún **no se identifica un grupo de datos mantenido por un sistema externo y referenciado por AgroRetorno**. El número de EIF es provisionalmente cero y debe verificarse. PostgreSQL no es en sí un EIF.

## Paso 3. Revisar transacciones
Usar la pestaña **Procesos** para decidir cuáles son EI (entrada), EO (salida derivada) o EQ (consulta), y cuáles deben agruparse. No confundir HTTP endpoints con procesos elementales.

## Paso 4. Completar parámetros
En el paso siguiente mediremos **DET/RET para archivos** y **DET/FTR para transacciones** siguiendo matrices de complejidad NESMA con fuentes documentadas, y calcularemos pesos con fórmulas editables. No asignamos puntajes arbitrarios hoy.

Fuente: migración SQL y controladores, rutas indicadas en el libro.
