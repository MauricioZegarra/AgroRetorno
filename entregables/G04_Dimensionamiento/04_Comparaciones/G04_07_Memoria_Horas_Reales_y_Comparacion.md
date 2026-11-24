# G04 — Paso 1.7 | Horas reales y contraste con COCOMO

**Responsable:** Cielo Cristal Meza Vizcarra  
**Proyecto:** AgroRetorno · Calidad de Software 2026-B  
**Estado:** Instrumento de registro preparado. Datos reales pendientes de los integrantes.

## Paso 1.7.1. Alcance
Se pretende comparar el esfuerzo de desarrollo observado con el esfuerzo de referencia de COCOMO 81 básico en modo orgánico utilizado en G04.4. La estimación base, **8.9188 persona-meses**, corresponde al tamaño Java productivo empleado como *proxy* del sistema, no únicamente al trabajo de las guías G03 y G04. Por ello, **si las horas disponibles solamente documentan algunas guías o algunas jornadas, la comparación es parcial y no permite obtener una desviación final válida**.

## Paso 1.7.2. Definiciones
- **Hora declarada:** tiempo efectivamente dedicado por una persona a una actividad de AgroRetorno.
- **Hora validada:** hora declarada acompañada por evidencia revisada y estado `VALIDADO`.
- **Persona-mes (PM) observado:** horas validadas divididas entre las **160 horas/PM** del supuesto de COCOMO usado en el libro.
- **Cobertura observada:** PM observados / PM estimados. **No representa porcentaje de avance físico ni error de estimación**.
- **Desviación final:** PM observados − PM estimados, calculada **únicamente** cuando el equipo confirme que cubrió la totalidad del trabajo y marque el corte `COMPLETO`.

## Paso 1.7.3. Instrucciones
1. Abrir `G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx` y la hoja `REALES_Registro`.
2. Registrar una fila por actividad realizada: integrante, fecha real, fase, descripción concreta y horas efectivas.
3. Adjuntar un enlace o ruta verificable del repositorio, bitácora, captura auténtica, documento o informe.
4. Marcar `VALIDADO` solamente cuando se revise la evidencia correspondiente. Las horas no validadas no se suman al subtotal observado.
5. Revisar en `REALES_Resumen` las horas declaradas, validadas, por integrante y por fase.
6. Conservar `PARCIAL` hasta tener evidencia y confirmación de la cobertura total de las actividades, desde los requisitos hasta la entrega. Solo entonces marcar `COMPLETO`.
7. Interpretar el resultado teniendo en cuenta las limitaciones del conteo LOC, las tasas supuestas y el alcance real de los registros.

## Paso 1.7.4. Fórmulas utilizadas
- `Horas validadas = Σ horas de filas con estado VALIDADO`.
- `PM observados = Horas validadas / Horas por PM`.
- `Cobertura del registro frente al modelo = PM observados / PM base COCOMO`.
- `Desviación absoluta final (PM) = PM observados − PM estimados`.
- `Desviación relativa final = (PM observados / PM estimados) − 1`.

En el libro las dos últimas fórmulas **se mantienen en blanco mientras el corte sea PARCIAL**.

## Paso 1.7.5. Fuentes y trazabilidad
- Repositorio: https://github.com/MauricioZegarra/AgroRetorno
- Rama de trabajo: `feature/cielo-g04-dimensionamiento-estimacion`.
- Fuente de estimación: `COCOMO_Modelo!B9` y `COCOMO_Parametros!B12`.
- Evidencias observadas: incorporar únicamente referencias realmente revisadas por el equipo en `REALES_Registro`.
- Bibliografía metodológica: Boehm, B. W. (1981). *Software Engineering Economics*. Prentice Hall.

## Paso 1.7.6. Limitaciones y pendientes
- No se han aportado horas trabajadas verificables de los cuatro integrantes: **no se asignan horas de ejemplo a nadie**.
- Las marcas de tiempo de commits demuestran actividad en el repositorio, pero no cuánto tiempo consumió.
- El trabajo parcial de G03/G04 no es directamente comparable con el estimado de construcción de todo AgroRetorno.
- El conteo KLOC y los conteos funcionales NESMA y COSMIC continúan siendo preliminares y requieren validación.
- Si no existe una bitácora de períodos anteriores, consignar **datos no disponibles** en vez de estimarlos retroactivamente.
