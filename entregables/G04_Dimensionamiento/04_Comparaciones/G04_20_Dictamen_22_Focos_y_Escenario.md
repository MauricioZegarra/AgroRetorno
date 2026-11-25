# G04 — Paso 1.20 | Dictamen de tratamiento de los 22 focos COSMIC

**Proyecto:** AgroRetorno · **Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Código de referencia:** commit `11e92eeab7aaadfc45f77f34ee0f4a2ce5d37f94`  
**Requisitos de trabajo:** RF01–RF15 de G03, `1fef1d211` (sin conformidad G01)  
**Fuente del conteo:** `02_COSMIC/G04_03_Movimientos_COSMIC.csv`, SHA-256 esperado `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`.

## 1. Resultado: decisiones internas y escenario de sensibilidad

El Paso 1.19 concilió **111 filas** (21 E, 21 X, 41 R, 28 W) con **89 propuestas de conservación** y **22 focos**. En este paso se establece **una propuesta de tratamiento por foco**, diferenciando lo que demuestra el código de lo que exige interpretar una regla funcional específica del método COSMIC. No se afirma certificación, ejecución funcional en navegador, ni conformidad externa.

| Categoría | Filas | Tratamiento interno propuesto | Efecto en el escenario |
|---|---:|---|---:|
| E con filtros opcionales | 6 | Conservar una E de petición por proceso; no una por cada campo | 0 |
| E por consulta del usuario de sesión | 2 | Conservar propuesta de E disparadora; sesión define alcance, no una nueva E | 0 |
| E consulta de préstamos por dos roles | 1 | Conservar una E para el proceso común propuesto, sujeto a frontera por rol | 0 |
| X mediante POST→redirect→GET | 10 | Mantener X **condicionales** como salida de negocio visible en el flujo compuesto; excluir si la regla adoptada las asigna a otro proceso | 0 en escenario base |
| X de pantallas con varios grupos | 3 | Desdoblamiento **hipotético** de grupos de datos independientes; crear seis X nuevas en escenario | +6 |
| **Total** | **22** | **No son 22 aprobaciones COSMIC** | **111 → 117 candidatos hipotéticos** |

## 2. Evidencia específica de las tres pantallas con grupos diferentes

**P14 / M058: `EncargadoSolicitudController.detalle` y `vistaDetalle`.** El controlador coloca en el modelo `solicitud`, `productor`, `disponibilidad` y `prestamo`. `encargado/solicitud-detalle.html` muestra los cuatro bloques. En el CSV de partida existen lecturas R diferenciadas D04 (M054), D01 (M055), D03 (M056), D05 (M057), pero **solo una X D04** (M058). Para el escenario se conservan M058 y se agregan M112 (X/D01), M113 (X/D03), M114 (X/D05). **Cuidado:** la aparición condicional de préstamo y los datos embebidos pueden hacer que grupos no sean independientes bajo la norma aplicable.

**P19 / M087: `EncargadoPrestamoController.detalle` y `vista`.** `encargado/prestamo-detalle.html` muestra valores del préstamo y filas de devolución. El CSV tiene M085 R/D05 y M086 R/D06 y solo M087 X/D05. Para el escenario, proponer M115 X/D06 adicional.

**P21 / M101: `ProductorController.historial`.** `productor/historial.html` presenta tres pestañas con tablas propias: solicitudes, préstamos y devoluciones. El CSV tiene lecturas M098 R/D04, M099 R/D05, M100 R/D06; solo M101 X/D04. Se proponen M116 X/D05 y M117 X/D06.

En todos los casos, **un `model.addAttribute`, una pestaña o una tabla NO garantiza automáticamente un movimiento COSMIC**. La interpretación de objetos de interés, grupos de datos y frontera deberá verificarse antes de convertir esas seis propuestas en recuento definitivo.

## 3. Tratamiento de las otras 19 decisiones

**Nueve E de consulta:** se adopta como regla *de trabajo* que la solicitud del usuario que dispara una consulta es el grupo de entrada funcional, aunque filtros sean opcionales o los resultados dependan del usuario autenticado. Los valores de `Authentication` no generan por sí solos entradas independientes. Se conservan **M011, M025, M034, M045, M049, M080, M097, M102 y M105** en el escenario. P18 permanece como un único proceso candidato que admite variante por rol. Hay que comprobar con la edición concreta de COSMIC si la misma operación con variantes por rol justifica una o dos unidades de proceso.

**Diez X tras POST:** `RedirectAttributes.addFlashAttribute("exito",...)` guarda datos de negocio (ID, nombre, estado, cantidad o saldo), y el fragmento `fragments/layout.html` los representa como alerta en las páginas destino. Por tanto **sí hay datos funcionales visibles en el flujo completo**, pero el POST devuelve una redirección y el texto solo se presenta durante el GET posterior. Para conservar M005, M010, M019, M024, M033, M044, M064, M069, M079 y M096 se necesita definir como frontera de la operación funcional el flujo completo de la acción del usuario y evitar contar nuevamente el mismo resultado en un GET autónomo. Si la definición COSMIC adoptada trata ese GET como otro proceso, estas diez X deben reasignarse o excluirse del POST, y habrá que revisar el resto de las salidas del GET. **No se las declara validadas ahora.**

## 4. Comparación de magnitudes y condiciones para fijar un total

| Indicador | Modelo de trabajo actual | Escenario P14/P19/P21 |
|---|---:|---:|
| E | 21 | 21 |
| X | 21 | 27 |
| R | 41 | 41 |
| W | 28 | 28 |
| Total candidato | 111 CFP | 117 CFP |
| Carácter | Preliminar | Hipótesis de sensibilidad, no definitiva |

El escenario de 117 **no reemplaza** ni el CSV maestro ni el libro de 37 hojas. Tampoco prueba que 117 sea la respuesta correcta. Los 111 movimientos actuales y los seis nuevos son candidatos. Para un total final hay que contrastar *todos* contra una edición identificada del manual COSMIC, incluido el resultado de los diez POST, la frontera de las consultas GET y las 69 R/W previamente documentadas.

## 5. Cierre operativo comprobable

- Script `06_Memoria_Calculo/G04_20_Generar_Escenario_117.py`: comprueba que hay exactamente M001–M111, tipos 21/21/41/28 y estados originales; añade M112–M117 **solo en un CSV de escenario**; produce un informe con hashes reales del equipo. No altera archivos base.
- `04_Comparaciones/G04_20_Decisiones_22_COSMIC.csv`: 22 propuestas individuales con clase, evidencia y condición de cierre. No contiene falsos estados `Validado`.
- **No cambiar ahora** `COSMIC_Movimientos!K/M` ni el CSV maestro. No sumar 117 al resumen como valor validado sin una decisión posterior y una auditoría de sus fórmulas.
- Si el escenario muestra 117 filas y la diferencia +6 X, publicarlo como **análisis de sensibilidad de medición**, no como informe final certificado.

**Estado:** Paso 1.20 documentado como *decisiones metodológicas propuestas*; hay dos escenarios con trazabilidad. La validación funcional definitiva sigue condicionada a la norma y conciliación integral.
