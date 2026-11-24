# G04 — Paso 1.4: estimación de esfuerzo y duración con COCOMO

**Responsable:** Cielo Cristal Meza Vizcarra  
**Producto:** AgroRetorno · v1.0.0  
**Estado:** cálculo ilustrativo para revisión; NO calibrado ni validado como presupuesto.

## 1. Referencias y alcance

La Guía 04 del trabajo final exige estimar esfuerzo y duración con COCOMO, declarar variante, tamaño de entrada, parámetros y fuentes; calcular posteriormente escenarios de una y tres personas y analizar sensibilidad ±20%. El cálculo emplea el **COCOMO 81 básico, modo orgánico** como hipótesis metodológica. Los coeficientes históricos del modelo se atribuyen a Barry W. Boehm, *Software Engineering Economics* (1981). El modelo no se ha calibrado para Java/Spring Boot moderno.

El tamaño fue calculado sobre **`AgroRetorno-v1.0.0.zip`** (archivo disponible en el trabajo) bajo `AgroRetorno/src/main/java/`, **80 archivos Java de producción**. Suma SHA-256 del ZIP:
`eeee040a24dbfdbf44a43654f0cd8cebca9b01795b44ba844380e03cb2994a92`.

La heurística quita comentarios en bloque, líneas de comentario y espacios vacíos; no es un parser Java, puede tratar cadenas como código. Por ello las cifras son **aproximaciones**:

| Métrica | Valor |
|---|---:|
| Archivos Java productivos | 80 |
| Líneas físicas | 4 267 |
| Líneas de código aproximadas | 3 491 |
| Tamaño de entrada | 3.491 KLOC |

**Alcance excluido del tamaño técnico:** pruebas, Thymeleaf/HTML, JS, CSS, SQL, scripts, archivos de configuración, infraestructura y componentes reutilizados no escritos por el equipo. El conteo no representa todo el producto y debe cotejarse con el commit de código definitivo. El código fuente está en el repositorio, pero el conteo se calculó sobre el ZIP v1.0.0 por reproducibilidad.

## 2. Variante, parámetros y fórmulas

- Modelo: COCOMO 81 **básico / orgánico**.
- `a=2.4`, `b=1.05`, `c=2.5`, `d=0.38`.
- `E = a × KLOC^b` (persona-mes).
- `TDEV = c × E^d` (meses calendario).
- Persona-mes de referencia: 160 horas (supuesto ajustable).
- Dedicación: 80 % por integrante (hipótesis).
- Tarifa: S/ 20 por hora (hipótesis ilustrativa, **no cotización**).
- Coordinación adicional equipo de 3: 15 % (hipótesis).

## 3. Resultados del cálculo base

| Magnitud | Valor calculado |
|---|---:|
| Tamaño | 3.491 KLOC |
| Esfuerzo nominal | 8.92 PM |
| Duración nominal | 5.74 meses calendario |
| Equipo medio implícito | 1.55 personas |
| Horas técnicas (E × 160) | 1,427.02 h |
| Costo técnico base (S/ 20/h) | S/ 28,540.30 |

**Importante:** el resultado es *teórico e ilustrativo*. No constituye plazo contractual, presupuesto ni medición de horas efectivamente trabajadas.

## 4. Escenarios de dotación

Para mostrar capacidad se propone una regla de escenario didáctico:

- `PM_bruto = E / (1 - porcentaje_coordinación)`
- `Meses_capacidad = PM_bruto / (número_personas × dedicación)`
- `Plazo_escenario = MAX(Meses_capacidad, TDEV)`
- `Horas_pagadas = PM_bruto × 160`
- `Costo = Horas_pagadas × S/20`

| Supuesto | 1 persona | 3 personas |
|---|---:|---:|
| Dedicación | 80 % | 80 % |
| Sobrecarga coordinación | 0 % | 15 % |
| Plazo por escenario | 11.15 meses | 5.74 meses |
| Horas remuneradas | 1,427.02 | 1,678.84 |
| Costo ilustrativo | S/ 28,540.30 | S/ 33,576.83 |

**No se divide TDEV entre tres.** Una mayor dotación puede ayudar a aumentar la capacidad, pero añade coordinación y no elimina actividades secuenciales. La regla MAX es una **hipótesis de planificación** independiente de la ecuación original de COCOMO; se debe declarar como tal.

## 5. Sensibilidad

En el Excel, el tamaño en KLOC puede variar ±20 %. Dado que `E = 2.4 × KLOC^1.05`, un cambio de tamaño no produce una respuesta perfectamente lineal. La hoja **COCOMO_Sensibilidad** recalcula E, TDEV, horas y costo de los tres casos.

## 6. Separación de magnitudes

- **NESMA:** 131 UFP, conteo preliminar por validar; valor funcional con pesos de complejidad.
- **COSMIC:** 111 CFP, conteo preliminar por validar; movimientos de datos.
- **COCOMO:** 3.491 KLOC de código Java aproximado, entrada de un modelo de esfuerzo.

No sumar UFP con CFP ni inferir KLOC directamente de ellos sin un factor justificado. Evitamos usar conversiones no fundamentadas.

## 7. Limitaciones y pendientes

1. Contrastar el ZIP con la rama GitHub que se presente como versión definitiva.
2. Verificar el conteo Java y completar una estimación de otros componentes si el alcance del docente lo exige.
3. Validar coeficientes y modo con el ingeniero, pues COCOMO 81 no contempla directamente reutilización moderna.
4. Fundamentar tarifa, 160 horas/mes, dedicación y 15 % de coordinación con una fuente o un supuesto académico aprobado.
5. Solicitar a los cuatro integrantes **horas reales** con actividad, fecha y commit/bitácora de respaldo. El Excel las deja vacías hasta contar con datos.
6. Revisar el conteo funcional NESMA y COSMIC contra la ERS definitiva (G01).
7. Comparar, en el Paso 1.6–1.8, tamaños, esfuerzos, plazos, costos y sensibilidad dejando claras sus unidades.

## 8. Cómo reproducir el tamaño con la copia local

Desde la raíz de AgroRetorno:

```powershell
python "entregables\G04_Dimensionamiento\03_COCOMO\G04_04_ContarLOC.py" --root .
```

El script **no modifica el código**, solo muestra el conteo. Si difiere del ZIP, registrar la diferencia y actualizar las celdas de **COCOMO_FuenteLOC** con el código elegido como línea base.

## 9. Archivos para auditoría

- `G04_04_NESMA_COSMIC_COCOMO.xlsx`: libro integrado con fórmulas, escenarios, gráficos y espacios para horas reales.
- `G04_04_Conteo_Java_Por_Paquete.csv`: datos originales del conteo.
- `G04_04_ContarLOC.py`: script reproducible.
- `G04_04_Checklist_COCOMO.md`: criterios pendientes.
