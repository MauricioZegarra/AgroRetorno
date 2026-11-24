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

