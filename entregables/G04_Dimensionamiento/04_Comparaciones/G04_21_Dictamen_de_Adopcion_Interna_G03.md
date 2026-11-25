# G04 — Paso 1.21 | Dictamen interno de medición COSMIC — línea base G03

**Proyecto:** AgroRetorno  
**Rama de trabajo:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Código GitHub revisado:** `c2b707bc0e44dc71a34dc8c8409f6321895221b1` (G04 1.20 publicado)  
**Catálogo de requisitos de trabajo:** RF01–RF15 de G03, commit `1fef1d211`; sin atribuir conformidad a G01.  
**CSV maestro de referencia:** SHA-256 `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`.

## 1. Decisión de medición académica interna

Para la **línea base interna G03**, se adopta como **modelo de estimación** una variante de **117 movimientos = 117 CFP propuestos**:

| Tipo de movimiento | Maestro de 111 | Variante adoptada internamente |
|---|---:|---:|
| Entry (E) | 21 | 21 |
| Exit (X) | 21 | 27 |
| Read (R) | 41 | 41 |
| Write (W) | 28 | 28 |
| **Total** | **111** | **117** |

Los **seis X nuevos** son M112–M114 para P14, M115 para P19 y M116–M117 para P21. La decisión no se basa en la cantidad de campos, `model.addAttribute`, pestañas o consultas SQL, sino en la **hipótesis interna** de que hay grupos de datos de salida de objetos de interés distintos. Es una hipótesis documentada: no se ha cotejado todavía con una edición verificable del manual COSMIC.

**Precisión esencial:** las 117 filas tendrán una **decisión de inclusión en el modelo interno G03**, pero **NO** el estado «COSMIC validado formalmente». Ningún archivo de este paso atribuye aprobación a G01, al docente o a un auditor. Así cerramos la elección del escenario interno sin falsear la validez externa.

## 2. Reglas adoptadas (decisiones internas, no citas del manual)

| Regla G04 | Alcance | Tratamiento |
|---|---|---|
| `R21-E0` | Entradas con formulario o identificador | Incluir una Entry de los datos relevantes para el evento funcional |
| `R21-E1` | Consultas con filtros opcionales | Incluir una Entry para la solicitud de consulta; no multiplicarla por parámetro |
| `R21-E2` | Consultas de sesión sin payload | Incluir la petición explícita de consulta como Entry de trabajo; no contabilizar Authentication como otra Entry |
| `R21-E3` | P18, dos roles | Mantener un proceso de consulta común en el modelo académico, documentando variación de filtrado y alcance |
| `R21-X0` | Listados y reportes | Incluir salida de datos de negocio reconocibles en el grupo del proceso |
| `R21-X1` | `POST → redirect → GET` | Atribuir una salida de resultado de negocio una sola vez al evento que la originó; no añadir otra X por el flash mostrado tras redirección |
| `R21-X2` | P14 / P19 / P21 | Desdoblar grupos de datos de salida cuando se adopte la hipótesis de objetos de interés independientes |
| `R21-RW` | 41 R y 28 W | Conservar decisiones del Paso 1.17 por grupo lógico de datos y proceso, evitando una unidad por cada SQL/JPA |

### Notas de frontera

- **Consultas sin parámetros:** el uso de `Authentication` por sí solo no constituye un grupo funcional de datos de entrada; aquí se adopta la petición de consultar como evento de negocio, no la lectura de sesión como Entry separada. La validez de esta agrupación debe contrastarse con una edición concreta del manual si se solicita medición COSMIC formal.
- **Navegación con redirección:** los diez controladores POST comunican información de negocio vía `RedirectAttributes.addFlashAttribute`, y el fragmento Thymeleaf de alertas del GET destino la muestra. La decisión interna trata ese resultado como salida funcional de la acción completa y prohíbe duplicarla. Un HTTP 302 por sí solo no se cuenta como Exit adicional.
- **P18 y roles:** Productor consulta sus propios préstamos; Encargado filtra préstamos por estado. La agrupación como un solo proceso es una simplificación explícita para el modelo académico G03.
- **R/W y D08:** el historial D08 es consultable por el Encargado; esta evidencia respalda una **propuesta de dato funcional persistente**, no implica que cada operación de logging de cualquier sistema sea medible.

## 3. Resolución individual de los 22 focos

| Movimiento(s) | Categoría | Decisión de trabajo | Evidencia / límite |
|---|---|---|---|
| M011, M025, M034, M049, M102, M105 | E de filtro opcional | Incluir (`R21-E1`) | GET de listado/reporte con filtros opcionales o predeterminados; no sumar una E por filtro |
| M045, M097 | E por sesión | Incluir (`R21-E2`) | Productor solicita lista e historial propios; `Authentication` solo determina el ámbito |
| M080 | E por roles | Incluir (`R21-E3`) | `ProductorController.misPrestamos` y `EncargadoPrestamoController.listar` consultan el dominio de préstamos |
| M005, M010, M019, M024, M033, M044, M064, M069, M079, M096 | X tras POST | Incluir (`R21-X1`) | Flash con atributos de negocio visible en `fragments/layout.html`; salida única por acción, sin doble conteo |
| M058, M087, M101 | X con grupos múltiples | Mantener X original y añadir seis candidatas (`R21-X2`) | Plantillas Thymeleaf muestran grupos distintos; hipótesis de independencia por objeto de interés |

**Resultado de esta tabla:** 22/22 focos reciben **decisión operativa interna**, pero no se convierten en 22 certificaciones COSMIC.

## 4. Nuevos movimientos propuestos

| ID | Proceso | Tipo / grupo | Fundamento funcional observado |
|---|---|---|---|
| M112 | P14 | X / D01 Productores | Detalle de solicitud incluye datos del productor |
| M113 | P14 | X / D03 Existencias | Detalle de solicitud muestra disponibilidad vigente |
| M114 | P14 | X / D05 Préstamos | La pantalla muestra préstamo relacionado si existe |
| M115 | P19 | X / D06 Devoluciones | Detalle de préstamo presenta tabla de devoluciones |
| M116 | P21 | X / D05 Préstamos | Historial del productor presenta tabla de préstamos |
| M117 | P21 | X / D06 Devoluciones | Historial del productor presenta tabla de devoluciones |

Las primeras 111 filas se mantienen en la matriz de decisiones con evidencia G04_17/G04_18/G04_19. No se modifican ni el maestro de 111, ni los escenarios 1.20, ni las hojas del Excel principal.

## 5. Interpretación del estado «Validado» y paso posterior

Para evitar sobredeclaraciones, hay **tres estados diferentes**:

1. **Trazabilidad de referencias Java:** verificada textualmente para 111/111 (Paso 1.12). Las seis salidas añadidas se vinculan a controladores y vistas ya inspeccionadas.
2. **Decisión de inclusión en la medición interna G03:** emitida para 117/117 según las ocho reglas R21 especificadas; el script G04_21 genera un CSV por movimiento con motivo y fuente.
3. **Conformidad formal COSMIC / G01 / revisor externo:** **no acreditada**. No se rellena artificialmente «Validado» en el maestro de 37 hojas; hacerlo ocultaría la diferencia entre criterio de trabajo y revisión metodológica.

El Paso 1.22 podrá **integrar el modelo elegido** al libro XLSX **con respaldo y nueva validación de fórmulas**, usando la versión exacta del archivo G04_07 publicada en la rama. No se debe reescribir la hoja `COSMIC_Movimientos` sin saber cómo se referencian sus 111 filas desde `COSMIC_Procesos` y `COSMIC_Resumen`.

## 6. Evidencia de ejecución reproducible

Ejecutar `06_Memoria_Calculo/G04_21_Consolidar_Modelo_Interno_117.py` desde la rama G04. El script verifica SHA-256 del maestro, claves M001–M117, categorías de los 22 focos, grupos y referencias de las seis adiciones, y que los 111 registros originales no hayan sido alterados en el escenario. Genera:

- `04_Comparaciones/G04_21_Decisiones_Internas_117_COSMIC.csv`;
- `04_Comparaciones/G04_21_Informe_Decisiones_Internas_117.md`.

**Dictamen:** 117 candidatos incorporados en el **modelo interno adoptado para G03** y 0 filas sin decisión **interna**. No se presenta 117 CFP como certificación metodológica ni se atribuye aprobación de requisitos G01.
