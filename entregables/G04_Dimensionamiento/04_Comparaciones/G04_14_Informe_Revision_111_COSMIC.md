# G04 — Paso 1.14 | Estado verificable de los 111 movimientos COSMIC

**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**Commit GitHub inspeccionado:** `4f068797a39b595082a305fa45e3b32f3cb09a5e`  
**Linea base funcional de trabajo:** RF01–RF15 de G03; no se atribuye conformidad a G01.  
**Fuentes:** CSV de 111 movimientos y dictámenes G04_11 a G04_13 publicados en GitHub, contrastados con la estructura del Excel de 37 hojas.

## 1. Dictamen

- **111/111 referencias clase.método:** localizadas en el análisis de trazabilidad 1.12.
- **0 referencias Java faltantes:** las cuatro alertas M080–M083 fueron falsos negativos por dos controladores alternativos.
- **111 CFP:** total de la hipótesis existente, no resultado COSMIC funcionalmente certificado.
- **37 movimientos:** requieren comprobar criterios funcionales específicos; las restantes 74 filas sin foco NO equivalen a una aprobación metodológica.
- **Ninguna fila cambia de estado** en el Excel original: hacerlo antes de la revisión semántica convertiría una verificación textual en una afirmación funcional falsa.

## 2. Aspectos con revisión especial

| ID | Proceso | Tipo | Decisión de trabajo | Requisito de evidencia funcional |
|---|---|---|---|---|
| M004 | P03 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M005 | P03 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M009 | P04 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M010 | P04 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M011 | P05 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M018 | P06 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M019 | P06 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M023 | P07 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M024 | P07 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M025 | P08 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M032 | P09 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M033 | P09 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M034 | P10 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M043 | P11 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M044 | P11 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M045 | P12 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M046 | P12 | R | Conservar como candidato, no declarar validado funcionalmente | Lectura D01: distinguir consulta funcional de identidad ya disponible por sesión o asociación |
| M049 | P13 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M051 | P13 | R | Conservar como candidato, no declarar validado funcionalmente | Lectura D01: distinguir consulta funcional de identidad ya disponible por sesión o asociación |
| M053 | P14 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M063 | P15 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M064 | P15 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M068 | P16 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M069 | P16 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M078 | P17 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M079 | P17 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M080 | P18 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M082 | P18 | R | Conservar como candidato, no declarar validado funcionalmente | R D04 en P18: JOIN y DTO no son suficientes por sí solos; acreditar lectura de grupo lógico independiente |
| M084 | P19 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M095 | P20 | W | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M096 | P20 | X | Conservar como candidato, no declarar validado funcionalmente | Salida tras POST: revisar si transmite datos funcionales o sólo redirección/acuse |
| M097 | P21 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M102 | P22 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M103 | P22 | R | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M104 | P22 | X | Conservar como candidato, no declarar validado funcionalmente | Historial D08: verificar requisito funcional de consulta de auditoría; no asumir logging técnico |
| M105 | P23 | E | Conservar como candidato, no declarar validado funcionalmente | Entrada de consulta: demostrar que cruza datos/criterios, no solamente un evento vacío |
| M111 | P23 | X | Conservar como candidato, no declarar validado funcionalmente | Salida derivada de reporte: comprobar datos funcionales y transformación |

## 3. Procedimiento de cierre verificable

1. Revisar los **37 focos** contra el código de servicio y las entradas/salidas efectivas, no solo contra el nombre de método. Elegir para cada uno: conservar (con evidencia), corregir tipo/grupo, o excluir (con justificación).
2. Documentar el criterio de aceptación para las **74 filas restantes** y confirmar si representan realmente un único movimiento del grupo de datos por proceso, sin repetir SQL, joins o acuses.
3. Adoptar explícitamente una versión de la línea base RF de G03 como alcance medido si G01 no está disponible; no presentarla como ERS G01 aprobada.
4. Actualizar el CSV de decisiones, luego la hoja `COSMIC_Movimientos` en el libro **actual** y recalcular `COSMIC_Procesos` y `COSMIC_Resumen`. El total puede variar respecto de 111 CFP.
5. Usar `Validado` únicamente para decisiones de medición ya revisadas; para exclusiones usar `Excluido con sustento`, sin contar 1 CFP para esa fila.
6. La revisión académica externa y las horas observadas no se infieren del código y no deben certificarse sin fuente.

## 4. Interpretación de la matriz anexa

- **Columna J:** `VALIDADA - referencia localizada` significa solo que el código de la referencia existe.
- **Columna K:** propuesta técnica; no sustituye el juicio de medición COSMIC.
- **Columna L:** prueba semántica que debe completarse para cada movimiento.
- **Columna M:** refleja que esta auditoría no proporciona conformidad funcional para la fila.

**No se han modificado archivos del repositorio ni el libro fuente.**
