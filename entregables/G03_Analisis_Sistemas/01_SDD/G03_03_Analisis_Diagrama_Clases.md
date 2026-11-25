# G03 — Paso 1.3: Diagrama de clases de AgroRetorno

**Responsable:** Cielo Cristal Meza Vizcarra  
**Proyecto:** AgroRetorno  
**Guía:** G03 — Análisis de Sistemas  
**Fecha de preparación:** 23 de noviembre de 2026  
**Fuente:** repositorio `MauricioZegarra/AgroRetorno`, rama `feature/cielo-g03-analisis-de-sistemas`.

## 1. Propósito y alcance

Modelar la estructura estática del dominio implementado en AgroRetorno, con sus entidades persistentes, atributos representativos, métodos relevantes y asociaciones JPA. El modelo presenta la arquitectura del dominio del sistema de préstamo y devolución de materiales agrícolas reutilizables, y sirve como entrada del SDD y del análisis de consistencia con PostgreSQL.

No es un diagrama de casos de uso ni un diagrama entidad-relación exclusivo: representa **clases Java y sus asociaciones**, interpretadas mediante multiplicidades UML. Por legibilidad, la visualización se divide en *núcleo del préstamo* y *entidades de soporte*; el archivo `.puml` contiene una vista integrada de las diez clases.

## 2. Entidades modeladas

| Paquete conceptual | Clases reales | Responsabilidad |
|---|---|---|
| Identidad | `Usuario`, `Productor` | Acceso por rol y datos del productor agrícola. |
| Catálogo e inventario | `Material`, `Inventario` | Catálogo y distribución de existencias. |
| Solicitudes y préstamos | `Solicitud`, `Prestamo` | Petición y entrega física de unidades. |
| Devoluciones | `Devolucion`, `DetalleDevolucion` | Registro de devoluciones parciales o totales y clasificación de estado. |
| Soporte | `MovimientoInventario`, `HistorialOperacion` | Movimientos trazables de existencias y auditoría. |

Total: **10 clases anotadas con `@Entity`**. Los tipos `Rol`, `CategoriaMaterial`, `EstadoMaterial`, `EstadoSolicitud`, `EstadoPrestamo`, `CondicionDevolucion` y `TipoMovimiento` se utilizan como enumeraciones, no como tablas de catálogo independientes en el diagrama.

## 3. Asociaciones y multiplicidades principales

| Origen | Destino | Cardinalidad | Justificación en código |
|---|---|---|---|
| Usuario | Productor | `1 ↔ 0..1` | `Productor.usuario` usa `@OneToOne` y `usuario_id` único. Un encargado puede existir sin perfil `Productor`. |
| Productor | Solicitud | `1 ↔ 0..*` | `Solicitud.productor` usa `@ManyToOne` obligatorio. |
| Material | Solicitud | `1 ↔ 0..*` | `Solicitud.material` usa `@ManyToOne` obligatorio. |
| Material | Inventario | `1 ↔ 0..1` | `Inventario.material` usa `@OneToOne`; en BD `material_id` es único. |
| Solicitud | Prestamo | `1 ↔ 0..1` | `Prestamo.solicitud` usa `@OneToOne`; `solicitud_id` es único. |
| Prestamo | Devolucion | `1 ↔ 0..*` | `Devolucion.prestamo` usa `@ManyToOne`; admite devoluciones parciales. |
| Devolucion | DetalleDevolucion | `1 ↔ 0..*` | `@OneToMany(cascade = ALL, orphanRemoval = true)`; corresponde una composición de ciclo de vida. |
| Usuario | Solicitud | `0..1 ↔ 0..*` (respuesta opcional) | `Solicitud.respondidoPor` es opcional y puede ser nulo; no toda solicitud tiene respuesta. |
| Usuario | Prestamo | `1 ↔ 0..*` | `Prestamo.entregadoPor` es obligatorio. |
| Usuario | Devolucion | `1 ↔ 0..*` | `Devolucion.recibidoPor` es obligatorio. |
| Material | MovimientoInventario | `1 ↔ 0..*` | `MovimientoInventario.material` es obligatorio. |
| Usuario | MovimientoInventario | `1 ↔ 0..*` | `MovimientoInventario.usuario` es obligatorio. |

**Precisión:** Para `Solicitud.respondidoPor`, la multiplicidad del lado de `Usuario` es **0..1** porque las solicitudes pendientes no tienen encargado que haya respondido; el otro extremo es **0..*** porque un encargado puede responder varias solicitudes. Para `Prestamo.entregadoPor` y `Devolucion.recibidoPor`, el usuario es obligatorio. La vista de núcleo omite estas asociaciones para evitar cruces de líneas y la vista PlantUML las incluye.

## 4. Reglas de diseño verificadas

### 4.1 Una solicitud origina como máximo un préstamo

La entidad `Prestamo` contiene `@OneToOne` a `Solicitud` y una restricción única. Esto evita generar dos préstamos independientes de la misma solicitud. **Aprobación y entrega son operaciones distintas**: `Solicitud` pasa por `APROBADA`; el préstamo se materializa cuando el encargado registra la entrega.

### 4.2 Las devoluciones pueden ser parciales

`Devolucion` mantiene una asociación muchos-a-uno con `Prestamo`; cada préstamo puede tener múltiples devoluciones registradas en diferentes fechas. El detalle clasifica la cantidad recibida como `BUEN_ESTADO`, `DANADO` o `REQUIERE_MANTENIMIENTO`.

### 4.3 El inventario tiene una invariante verificable

La clase `Inventario.estaCuadrado()` comprueba:

`cantidadTotal = cantidadDisponible + cantidadPrestada + cantidadDanada + cantidadMantenimiento`

Esta restricción también aparece como `CHECK` en la migración SQL `V1__esquema_inicial.sql`.

### 4.4 Separación entre movimientos e historial

`MovimientoInventario` tiene referencias JPA a `Material` y `Usuario`. `HistorialOperacion`, en cambio, registra nombres textuales de actor, entidad y acción; no hay una clave foránea JPA que la relacione directamente con las otras entidades. Por ello, **no se inventó** tal asociación en el diagrama.

## 5. Leyenda UML

- **Recuadros:** clases Java persistentes.
- **Parte superior:** nombre de clase.
- **Sección de atributos:** atributos seleccionados del código (`−` significa privado).
- **Sección de operaciones:** métodos públicos relevantes (`+` significa público).
- **Línea y multiplicidades:** asociación estructural y cardinalidad.
- **Composición:** una `Devolucion` controla el ciclo de vida de sus `DetalleDevolucion` mediante `orphanRemoval`.
- **Colores:** agrupación conceptual para lectura, no estereotipos UML de ejecución.

Las tablas y atributos adicionales se encuentran en las entidades Java y en la migración SQL. Las clases se han sintetizado para mantener legible el dibujo.

## 6. Archivos originales y reproducción

| Archivo | Finalidad |
|---|---|
| `G03_03_Clases_Completo.puml` | Vista integrada en PlantUML, editable, fuente principal de modelado UML. |
| `G03_03_Clases_Nucleo.dot` | Fuente editable de Graphviz del núcleo, usada para renderizar PNG y SVG. |
| `G03_03_Clases_Nucleo.svg/.png` | Vista de presentación: ocho clases y asociaciones del proceso. |
| `G03_03_Clases_Soporte.dot` | Fuente editable Graphviz del soporte. |
| `G03_03_Clases_Soporte.svg/.png` | Vista de auditoría y movimientos de inventario. |

Los `.dot` se renderizan localmente con Graphviz: `dot -Tsvg archivo.dot -o archivo.svg` o `dot -Tpng archivo.dot -o archivo.png`. El archivo PlantUML es una **representación integrada semánticamente equivalente**; no es la fuente del diseño gráfico exacto de las imágenes Graphviz, por lo que se incluyen ambas fuentes editables.

## 7. Referencias al repositorio

- `src/main/java/pe/edu/agroretorno/entity/{Usuario,Productor,Material,Inventario,Solicitud,Prestamo,Devolucion,DetalleDevolucion,MovimientoInventario,HistorialOperacion}.java`
- `src/main/java/pe/edu/agroretorno/entity/{Rol,CategoriaMaterial,EstadoMaterial,EstadoSolicitud,EstadoPrestamo,CondicionDevolucion,TipoMovimiento}.java`
- `src/main/resources/db/migration/V1__esquema_inicial.sql`

## 8. Estado de revisión

**Preparado para revisión.** Las relaciones y atributos se verificaron mediante lectura del código publicado. No se ha ejecutado una herramienta UML dentro de la computadora de la estudiante y no se presenta esta preparación como una validación de ejecución de la aplicación. Cielo deberá revisar la legibilidad del diagrama y registrar capturas reales de su inspección si el informe exige evidencia de herramienta.
