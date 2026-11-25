# Bitacora G04

Responsable: Cielo Cristal Meza Vizcarra

## G04 - Paso 1.1

Actividad:
Definicion de frontera y linea base de AgroRetorno.

Procedimiento:
Se revisaron el SDD de G03, los requisitos
funcionales, las entidades, los controladores
y los servicios del sistema.

Resultados:
- Frontera funcional documentada.
- Catalogo RF01-RF15 de referencia.
- Ocho grupos de datos candidatos.
- Veintitres procesos candidatos.
- Excel inicial con formulas.
- Documentacion preparatoria NESMA y COSMIC.

Estado:
Analisis preliminar elaborado.
Pendiente validar contra ERS de G01
y realizar conteos funcionales definitivos.


## G04 - Paso 1.2: Conteo NESMA

Actividad:
Aplicacion del metodo NESMA a AgroRetorno.

Procedimiento:
Clasificacion preliminar de archivos logicos
y transacciones elementales.
Registro de DET, RET y FTR.
Aplicacion de matrices de complejidad
y formulas de puntos de funcion.

Resultados:
- Libro Excel editable.
- Memoria de calculo NESMA.
- Checklist de validacion.
- Estimacion preliminar: 131 UFP.

Estado:
Conteo preliminar, NO APROBADO.
Pendiente contrastar con ERS de G01
y verificar las clasificaciones funcionales.


## G04 - Paso 1.3: Medicion COSMIC

Actividad:
Aplicacion preliminar del metodo COSMIC.

Procedimiento:
Se analizaron los 23 procesos candidatos.
Se mantuvieron 21 procesos incluidos
y dos pendientes de validacion.

Se identificaron movimientos de datos:
Entry, Exit, Read y Write.

Resultados:
- Libro Excel editable y recalculable.
- Registro individual de 111 movimientos candidatos.
- Memoria de calculo y checklist.
- Resumen y comparacion con NESMA.

Resultado preliminar:
111 CFP. NO APROBADO.

Pendiente:
Verificar movimientos, grupos de datos,
salidas de control y requisitos de G01.


## G04 - Paso 1.4: Estimacion COCOMO

Actividad:
Estimacion de esfuerzo y duracion de AgroRetorno.

Procedimiento:
Se aplico COCOMO 81 basico, modo organico.
Se realizo un conteo aproximado del codigo Java.
Se construyeron escenarios de 1 y 3 personas.
Se documento sensibilidad del tamaño +/-20 %.

Resultados:
- Tamano tecnico: 3.491 KLOC aproximadas.
- Esfuerzo nominal: 8.92 persona-meses.
- Duracion nominal: 5.74 meses.
- Comparacion de costos y plazos.
- Excel con formulas y graficos.
- Script reproducible para contar lineas Java.

Limitaciones:
Se excluyen otros lenguajes y recursos.
COCOMO no esta calibrado para este proyecto.
Las tarifas, dedicacion y coordinacion son supuestos.

Estado:
Estimacion ilustrativa elaborada.
Pendiente validacion y horas reales del equipo.


## G04 - Paso 1.5: Costos y cronogramas

Actividad:
Elaboracion de escenarios de costos y planificacion.

Procedimiento:
Se utilizaron las estimaciones de COCOMO 81.
Se prepararon escenarios de 1 y 3 personas.
Se modelaron tarifas, dedicacion, coordinacion,
horas productivas y capacidad disponible.
Se distribuyo el cronograma en seis fases.

Resultados:
- Excel acumulativo de 30 hojas.
- Dos escenarios de personal.
- Dos modalidades de costo.
- Cronogramas relativos con formulas.
- Graficos y memoria tecnica.
- Checklist de revision.

Estado:
Material preparado. Pendiente revision personal,
validacion de supuestos y costos reales.


## G04 - Paso 1.6: Sensibilidad y comparacion

Actividad:
Analisis de sensibilidad de las estimaciones
y comparacion de NESMA, COSMIC y COCOMO.

Procedimiento:
Se analizaron escenarios de variacion +/-20 %
para KLOC, tarifa, dedicacion y coordinacion.

Se compararon las unidades y alcances de
los tres metodos sin convertirlas entre si.

Resultados:
- Excel acumulativo de 34 hojas.
- Doce escenarios de sensibilidad.
- Tablero comparativo.
- Graficos en PNG.
- Memoria tecnica.
- Checklist de revision.

Estado:
Analisis preparado.
Pendiente validacion de la ERS,
revision local y horas reales del equipo.


## G04 - Paso 1.7: Registro de horas reales

Actividad:
Preparacion del registro y comparacion de
horas reales frente a COCOMO.

Procedimiento:
Se estructuro un registro por integrante,
fecha, actividad, fase y evidencia.

Se incorporaron formulas para:
- Horas declaradas.
- Horas validadas.
- Persona-meses observados.
- Comparacion con el esfuerzo COCOMO.

Resultados:
- Excel acumulativo de 37 hojas.
- Memoria metodologica.
- Checklist de revision.
- Panel de seguimiento.

Estado:
Instrumento preparado.
Pendiente completar y verificar horas reales
de los integrantes.
Corte PARCIAL.

<!-- G04-PASO19-AUDITORIA-REAL -->
## 2026-11-24 - G04 Paso 1.9 (registro de resultado real)
- Script auditado: G04_09_Validar_Libro.py.
- Libro con SHA256 2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472.
- Resultado: 11 controles (10 OK, 1 OBSERVACION, 0 REVISAR).
- NESMA: 131 UFP preliminares; COSMIC: 111 CFP preliminares.
- COCOMO: 3491 LOC Java, 8.91884 PM, 5.74192 meses.
- REALES_Registro sin horas; corte PARCIAL.
- Commit base al documentar: cb7e69dcdb85070d9acaac9fa1f0455499964212.
- Verificacion externa y ERS G01: pendientes. Sin merge.
- Informes en 04_Comparaciones/G04_09_*.

## G04 - Cierre tecnico interno (25/11/2026)

Actividad:
Consolidacion y cierre tecnico de dimensionamiento y estimacion.

Resultados:
- NESMA: 131 UFP del modelo de trabajo.
- COSMIC: 117 CFP del modelo interno G03.
- COCOMO: 3.491 KLOC Java, 8.91884 PM y 5.74192 meses.
- Costos directos estimados: S/ 28540.30 (1 persona)
  y S/ 33576.83 (3 personas).
- Auditoria independiente: 32 OK, 0 REVISAR.
- Libro actualizado de 37 hojas.
- Informe final G04_24 publicado en Word, PDF y Markdown.
- Horas reales: sin registros; corte PARCIAL.

Evidencias:
- G04_21: decisiones internas COSMIC.
- G04_22: integracion del Excel.
- G04_23: auditoria independiente.
- G04_24: informe de cierre tecnico.

Estado:
CIERRE TECNICO INTERNO AUDITADO.
Sin conformidad formal de G01, certificacion COSMIC
ni revision externa. No se realizo merge a main.
## G04 - Paso 1.25: Actualizacion de vistas de revision

Se regeneraron 14 imagenes desde el libro G04_22 de 117 CFP,
131 UFP y 3.491 KLOC, usando Excel en modo de solo lectura.
Imagenes vigentes: 05_Capturas/Revisiones_Actualizadas_117/.
Origen: visualizacion automatizada de valores locales, no captura manual de pantalla.
Las carpetas anteriores se respaldaron fuera del repositorio.
El estado de horas reales y los limites de G01 no fueron modificados.
