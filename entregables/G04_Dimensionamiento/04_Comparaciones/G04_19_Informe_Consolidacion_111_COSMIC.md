# G04 - Paso 1.19 | Conciliacion reproducible de 111 movimientos COSMIC

**Fecha de ejecucion:** 2026-11-24T23:17:09-05:00  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**HEAD del repositorio:** `b46153c5f91ba6e3cba5f2f1e063019601bd5e35`  
**SHA256 CSV maestro:** `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`  
**SHA256 G04_17:** `d9df99c80956e914aca180ae02bef6799dd39151b03e41e61d5c8f0d38bc73c4`  
**SHA256 G04_18:** `df22d9eeb19d8cdeae5d4bb7cae032700fc4179f5aa960c460e32d36197560b2`  

## Resultado de conciliacion

- 111 IDs originales unicos y completos (M001-M111), 21 procesos.
- R/W: 69 movimientos con sustento documental de G04_17.
- E/X: 42 movimientos con sustento documental y advertencias de G04_18.
- Tipos: E=21, X=21, R=41, W=28.
- Inclusiones **propuestas**: 89.
- Movimientos con regla metodologica por resolver: 22.
- IDs por resolver: M005, M010, M011, M019, M024, M025, M033, M034, M044, M045, M049, M058, M064, M069, M079, M080, M087, M096, M097, M101, M102, M105.
- Estados de origen: Por validar=111.

## Categorias no resueltas

- **E_FILTRO_OPCIONAL:** 6. Definir datos de la Entry disparadora aun con GET sin filtros, segun frontera COSMIC.
- **E_SESION_SIN_PAYLOAD:** 2. Identificar la Entry disparadora y grupo de datos, sin inferirlo solo de Authentication.
- **E_VARIANTES_ROL:** 1. Justificar si ambas rutas por rol comparten un proceso funcional o requieren separar mediciones.
- **X_POST_REDIRECT:** 10. Delimitar el proceso POST->redirect->GET; acreditar la X visible al usuario sin doble conteo.
- **X_VARIOS_GRUPOS:** 3. Definir objetos de interes de la pantalla; decidir 1 o varias Exit y recalcular si procede.

## Dictamen

**Consolidacion estructural completada: 111/111 sin huecos ni duplicados.** Los 111 punteros Java
se localizaron previamente por el paso 1.12. **No esta aprobada la semantica COSMIC de las
111 filas**: 89 tienen propuesta documental de conservacion y 22 requieren resolver criterios
expresos. Una propuesta de conservar no equivale por si sola a validacion funcional definitiva.

La matriz mantiene separados `Contar original`, `Estado original`, `Resultado de consolidacion`,
`Decision funcional definitiva` y `Estado de validacion funcional`. No modifica el maestro,
no cambia la columna M del XLSX, ni recalcula unilateralmente el CFP. Hasta decision funcional,
**111 CFP es un total preliminar**, no una medida validada.

La linea base es RF01-RF15 de G03; no se atribuye aprobacion a G01 ni a revisor externo.
Para fijar el total es necesario: identificar version/regla COSMIC, decidir las 22 situaciones
especiales (en particular salidas de multiples grupos), conciliar tambien las 89 propuestas,
y reflejar las inclusiones/exclusiones justificadas en CSV+XLSX vigente respaldado.

## Archivos de esta ejecucion

- `04_Comparaciones/G04_19_Matriz_Consolidada_111_COSMIC.csv`
- `04_Comparaciones/G04_19_Informe_Consolidacion_111_COSMIC.md`

**Control de integridad:** el script aborta si faltan IDs, se duplican filas o difieren proceso,
tipo o grupo de datos entre los documentos y el CSV maestro.
