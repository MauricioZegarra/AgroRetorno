# G04 — Paso 1.20 | Escenario alternativo de salidas COSMIC

**Generado:** 2026-11-24T23:28:00-05:00  
**Rama:** `feature/cielo-g04-dimensionamiento-estimacion`  
**HEAD local:** `11e92eeab7aaadfc45f77f34ee0f4a2ce5d37f94`  
**SHA-256 maestro:** `2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a`  
**SHA-256 escenario:** `686d5f12baaf4f5d7f908323b464050f0229925a2ded3dddc9ab49f9310ea60a`

## Control

| Componente | Maestro original | Escenario técnico |
|---|---:|---:|
| Entry E | 21 | 21 |
| Exit X | 21 | 27 |
| Read R | 41 | 41 |
| Write W | 28 | 28 |
| **Total** | **111 CFP candidatos** | **117 CFP hipotéticos** |

Se agregaron solo M112-M117 en un CSV **alternativo**. Se conservaron los 111 candidatos,
sin cambiar ninguna decisión original. D01/D03/D05 adicionales en P14; D06 en P19;
D05/D06 en P21. El escenario modela grupos de datos de salida potencialmente independientes,
según evidencias del código y las plantillas Thymeleaf.

**No es conteo final certificado ni recomendación automática para el XLSX:** faltan
comprobar la versión exacta de la regla COSMIC, el límite POST → redirect → GET,
los movimientos E sin parámetros, la separación de grupos y el examen funcional
individual de las otras 89 propuestas. La presencia de un grupo en pantalla no
acredita necesariamente un X medible.

**Control de integridad:** El hash del maestro coincide con el registrado en G04_19.  
**CSV de salida:** `04_Comparaciones/G04_20_Escenario_117_COSMIC.csv`.

Este script **no modifica** `02_COSMIC/G04_03_Movimientos_COSMIC.csv` ni el libro maestro de 37 hojas.
