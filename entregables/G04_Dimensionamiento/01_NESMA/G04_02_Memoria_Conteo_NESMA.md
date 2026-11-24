# G04 — Paso 1.2: Conteo funcional NESMA de AgroRetorno

**Responsable:** Cielo Cristal Meza Vizcarra  
**Proyecto:** AgroRetorno, Calidad de Software — 2026-B  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Estado:** **modelo preliminar editable; NO APROBADO**  
**Versión de trabajo:** 0.2 / 23 de noviembre de 2026

## 1. Objetivo

Obtener un conteo trazable de puntos de función **sin ajustar (UFP)** mediante el método NESMA, sobre la frontera funcional definida en G04 Paso 1.1. La cifra numérica provisional facilita la revisión, pero **no es una medición final**: depende de confirmar la ERS de G01, los grupos lógicos, el número de campos reconocibles para el usuario y la unicidad de los procesos elementales.

## 2. Definición de la frontera

**Dentro de la aplicación:** catálogos de productores y materiales, existencias, solicitudes, aprobación y rechazo, entregas, devoluciones, consultas e historial, reportes. Se consideran dos roles: Productor y Encargado.  
**Fuera de la aplicación:** GitHub, Render, Maven y demás herramientas de soporte. PostgreSQL sirve como persistencia de la propia aplicación, **no** es una interfaz externa (EIF) por el mero hecho de ser un producto diferente.

**Código que fundamenta el inventario:**

- Migración del esquema: `src/main/resources/db/migration/V1__esquema_inicial.sql`.
- Entidades: `src/main/java/pe/edu/agroretorno/entity/`.
- Controladores: `src/main/java/pe/edu/agroretorno/controller/`.
- Servicios de negocio: `src/main/java/pe/edu/agroretorno/service/`.
- Documento G03: `G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx` (catálogo de RF **provisional**, aún por cotejar con ERS).

Enlace base: https://github.com/MauricioZegarra/AgroRetorno/tree/main

## 3. Técnica y vocabulario

El conteo NESMA/IFPUG de funcionalidad distingue **funciones de datos** y **funciones transaccionales**.

| Símbolo | Nombre | Criterio principal |
|---|---|---|
| ILF | Internal Logical File / Archivo lógico interno | Grupo lógico reconocible por el usuario, mantenido por la aplicación |
| EIF | External Interface File / Archivo de interfaz externa | Grupo reconocido y consultado por esta aplicación, mantenido por otra aplicación |
| RET | Record Element Type | Subgrupo lógico de registros reconocible por usuario |
| DET | Data Element Type | Campo de datos único y reconocible por usuario; no debe confundirse con toda columna SQL |
| EI | External Input / Entrada externa | Proceso elemental que mantiene uno o más ILF o modifica comportamiento |
| EO | External Output / Salida externa | Información que sale de la frontera y presenta cálculos/derivaciones relevantes |
| EQ | External Inquiry / Consulta externa | Consulta con recuperación y presentación sin lógica derivada sustancial |
| FTR | File Type Referenced | ILF o EIF referenciado o mantenido por una transacción |
| UFP | Unadjusted Function Points | Total de puntos de función sin ajustar, suma de pesos de datos y transacciones |

La hoja `NESMA_Reglas` conserva las matrices de complejidad y los pesos de cada tipo y grado; las hojas `NESMA_Datos` y `NESMA_Transacciones` aplican estas reglas mediante fórmulas. **Los valores en amarillo son editables**.

## 4. Funciones de datos: primera hipótesis

La migración contiene **diez tablas físicas**. Para la primera pasada se proponen **ocho grupos lógicos**, sujetos a revisión funcional:

| Grupo | Tablas | Tipo propuesto | RET / DET propuestos | Cuestión por validar |
|---|---|---|---|---|
| D01 Cuentas y productores | usuarios; productores | ILF | 2 / 13 | ¿Dos subgrupos de un ILF o dos ILF independientes? |
| D02 Materiales | materiales | ILF | 1 / 7 | Delimitar campos de negocio frente a campos técnicos |
| D03 Existencias | inventario | ILF | 1 / 6 | Confirmar si es grupo independiente de Materiales |
| D04 Solicitudes | solicitudes | ILF | 1 / 10 | Identificar campos reconocidos al ingresar y evaluar solicitud |
| D05 Préstamos | prestamos | ILF | 1 / 11 | Revisar estado, conteos y campos redundantes |
| D06 Devoluciones | devoluciones; detalles_devolucion | ILF | 2 / 6 | Cabecera y detalle como un grupo lógico con dos RET |
| D07 Movimientos | movimientos_inventario | ILF | 1 / 7 | Justificar utilidad funcional para el usuario |
| D08 Historial | historial_operaciones | ILF | 1 / 6 | Delimitar registros de auditoría y datos de negocio |

**Importante:** los DET son **propuestas didácticas**, no sumas automáticas de columnas SQL; deben revisarse atributo por atributo con evidencia de entrada/salida y con la ERS. En la hipótesis actual todos los grupos tienen complejidad *Baja*, con peso de **7 PF por ILF**, y por eso la suma de datos es **56 PF preliminares**. No significa que todos deban quedarse como ILF.

Se ha supuesto **0 EIF** porque no se identificó un grupo lógico mantenido por otra aplicación; esta conclusión también debe contrastarse con la frontera y las integraciones reales.

## 5. Funciones transaccionales: primera hipótesis

Se parte de los **23 procesos candidatos** del Paso 1.1. Para este primer cálculo, P01 (inicio de sesión) y P02 (cierre de sesión) se dejan excluidos **por confirmar** porque podría tratarse de servicios técnicos transversales. Los restantes se clasifican tentativamente: **10 EI, 1 EO y 10 EQ**. Esta decisión no es definitiva; el conteo puede cambiar si dos consultas representan el mismo proceso elemental o si una salida tiene derivaciones.

Entre los ejemplos centrales:

### Ejemplo A — P11: registrar solicitud (EI)

- **Actor:** Productor.
- **Código:** `ProductorController.registrarSolicitud()` y `SolicitudService.registrar()`.
- **Objetivo:** registrar una solicitud en estado `PENDIENTE`.
- **DET propuestos (4):** material seleccionado, cantidad, fecha prevista de devolución y motivo.
- **FTR propuestos (4):** D01 productor/cuenta, D02 material, D03 existencias, D04 solicitud.
- **Aplicación provisional de matriz EI:** DET=4, FTR=4 ⇒ *Media* ⇒ **4 PF**.
- **Qué revisar:** si alguna información de seguridad o identificador externo es un DET adicional; si todos los grupos incluidos se cuentan verdaderamente como FTR según el método.

### Ejemplo B — P15: aprobar solicitud (EI)

- **Código:** `EncargadoSolicitudController.aprobar()` y `SolicitudService.aprobar()`.
- **Efecto:** se valida disponibilidad y pasa `PENDIENTE` a `APROBADA`.
- **Precaución:** **no descuenta existencias**. Descontar existencias pertenece a P17, que representa la entrega física.

### Ejemplo C — P17: registrar entrega física (EI)

- **Código:** `EncargadoSolicitudController.entregar()`, `PrestamoService.registrarEntrega()`, `InventarioService.descontarPorEntrega()`.
- **Efecto:** modifica existencias, crea un préstamo y registra movimientos y auditoría.
- **Precaución:** esta función no debe fusionarse con la simple aprobación porque tiene un objetivo y cambio de estado diferentes.

### Ejemplo D — P20: registrar devolución (EI)

- **Código:** `EncargadoPrestamoController.devolver()` y `DevolucionService.registrar()`.
- **Efecto:** registra cantidades en buen estado, dañadas y mantenimiento; actualiza existencias y cierra el préstamo si no quedan pendientes.
- **Precaución:** las devoluciones parciales no son un caso de uso adicional por cada entrega de unidades.

### Ejemplo E — P23: generar reporte consolidado (EO)

- **Código:** `EncargadoReporteController.reporte()` y `ReporteService`.
- **Hipótesis:** salida con información agregada o derivada, clasificada temporalmente como EO. Verificar cálculos y campos presentados.

Las demás filas, fuentes, DET y FTR propuestos se encuentran en `NESMA_Transacciones`. La suma de pesos de la hipótesis actual es **75 PF transaccionales preliminares**.

## 6. Resultado del modelo preliminar

| Concepto | PF no ajustados propuestos |
|---|---:|
| 8 ILF propuestos | 56 |
| 0 EIF propuestos | 0 |
| 10 EI propuestas | Subtotal incluido en hoja de transacciones |
| 1 EO propuesta | Subtotal incluido en hoja de transacciones |
| 10 EQ propuestas | Subtotal incluido en hoja de transacciones |
| Total transaccional propuesto | 75 |
| **UFP PROPUESTOS — NO APROBADOS** | **131** |

El valor de 131 **no debe presentarse al ingeniero como medida confirmada**. Es el resultado automatizado de un conjunto de hipótesis identificadas y documentadas. Cambiar DET, RET, FTR, clasificaciones o exclusiones en el libro recalcula el total.

## 7. Hallazgos que debemos comprobar antes de cerrar

1. Confirmar que los RF01–RF15 de G03/G04 coincidan con la ERS final elaborada en G01.
2. Revisar si D01 es un ILF con dos RET o si contiene dos ILF separados.
3. Revisar si D02 y D03 deben considerarse grupos lógicos diferentes para el usuario.
4. Confirmar la inclusión de D07 y D08 como información de negocio y no solo rastros técnicos.
5. Enumerar **DET reales**, no inferirlos automáticamente a partir de columnas SQL.
6. Enumerar **RET reales** basados en subgrupos lógicos.
7. Revisar FTR a partir de cada método de servicio y no solo de sus dependencias declaradas.
8. Confirmar que P08, P10 y otras consultas representan funciones diferentes.
9. Decidir si P01 y P02 se incluyen o no según el criterio de la versión NESMA empleada.
10. Verificar P14, P19, P21 y P23 para distinguir EO frente a EQ.
11. Repetir las fórmulas, comprobar totales y registrar fecha, revisor y observaciones.

## 8. Instrucciones para reproducir

1. Abrir `06_Memoria_Calculo/G04_02_Conteo_NESMA.xlsx`.
2. Consultar `Resumen`, `Frontera`, `RF_Base`, `Datos`, `Procesos` y `Fuentes` — pestañas conservadas de la línea base original.
3. Abrir `NESMA_Reglas` para revisar pesos y matrices.
4. Ir a `NESMA_Datos` y editar `Tipo`, `RET`, `DET` y `Contar` por cada grupo. Verificar cómo se recalculan complejidad y PF.
5. Ir a `NESMA_Transacciones` y editar `Tipo`, `DET`, `FTR` y `Contar` por función elemental.
6. Revisar `NESMA_Resumen` para conocer los subtotales y el UFP recalculado.
7. Registrar toda corrección en `Bitacora.md` con motivo y evidencia.
8. Guardar una captura real de las hojas abiertas **después de revisarlas** para presentarla como evidencia de revisión, no como prueba de ejecución de la aplicación.

## 9. Limitaciones y siguientes pasos

Esta medición es una **primera aproximación con reglas y pesos editables**. Aún requiere validación de G01 y revisión rigurosa de DET/RET/FTR. El siguiente paso G04 será aplicar **COSMIC** sobre la *misma frontera*, sin equiparar 131 UFP con 131 CFP. Después se elaborará COCOMO con un tamaño de entrada coherente, costos y escenarios de una y tres personas, y análisis de sensibilidad.

---

**Conclusión:** hay suficiente información de los controladores, servicios, entidades, migración SQL y pantallas para construir el método de conteo, pero todavía no para afirmar un tamaño funcional definitivo sin la revisión indicada.
