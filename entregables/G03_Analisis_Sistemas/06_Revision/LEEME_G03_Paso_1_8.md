# G03 — Paso 1.8: corrección de exportaciones de secuencia

**Responsable:** Cielo Cristal Meza Vizcarra.
**Rama:** `feature/cielo-g03-analisis-de-sistemas`.

## Alcance
Este paquete contiene cuatro diagramas corregidos en PNG/SVG, usando como referencia los mensajes y actores del archivo `.puml` de cada secuencia. Incluimos una copia de las fuentes del paso 1.4 con únicamente la nomenclatura RF alineada a las fuentes ya presentes en GitHub.

| Diagrama | Requisito según ERS ampliada, pendiente de confirmación G01 | Mensajes representados |
|---|---|---:|
| Registro de solicitud | RF07 | 18 |
| Aprobación | RF09 | 17 |
| Entrega física | RF11 | 24 |
| Vista integral | RF07, RF09, RF11 | 24 |

**Importante sobre el renderizado:** los SVG/PNG fueron redibujados localmente de forma determinista mediante un script de lectura de participantes, mensajes, ramas `alt/else` y notas del texto PlantUML. Se trata de exportaciones derivadas, **no de una ejecución del motor oficial PlantUML**, que sigue siendo el visor recomendado para la validación final. Los archivos `.puml` son las fuentes editables de referencia.

## Revisar con tu computadora
1. Abrir cada `.puml` con el visor oficial de PlantUML (por ejemplo, VS Code con una extensión compatible) para confirmar sintaxis y legibilidad.
2. Abrir los SVG y PNG corregidos y contrastar título, actores, mensajes y códigos RF.
3. Conservar una **captura genuina** del editor/visor y una breve observación sobre el resultado.
4. Actualizar el checklist, la bitácora y el SDD solo si se detecta una corrección pertinente.
5. Al validar la ERS G01, cambiar los identificadores si el equipo adopta otra numeración.

No atribuir a estos gráficos pruebas de ejecución de la aplicación.
