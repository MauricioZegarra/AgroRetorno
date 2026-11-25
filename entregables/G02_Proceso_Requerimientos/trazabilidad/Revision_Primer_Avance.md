# Revisión del primer avance y de la presentación

El primer avance define un caso de uso acotado, dos roles y tres procesos completos. Las tablas de las pp. 6–7 fijan los **15 RF y 10 RNF** con aceptación observable. La presentación pública contiene 35 diapositivas. La integración de este bloque se cotejó con G01, G02 y G05 del material del curso. El 25-11-2026 la página pública de inicio de sesión respondió HTTP 200. Esta comprobación identifica la disponibilidad de esa pantalla; el despliegue y su revisión se concilian con el equipo.

## Cambios antes de sustentar

| Prioridad | Hallazgo | Qué mostrar o corregir |
|---|---|---|
| Alta | El primer avance y las diapositivas usan `v1.0.0` para la versión desplegada. La práctica crea una etiqueta SVN del mismo nombre. | Mostrar cada referencia con origen: Git y despliegue por un lado, revisión y etiqueta SVN por el otro. Vincular la versión desplegada a un commit si el responsable de Render puede confirmarlo. |
| Alta | La diapositiva 17 presenta REM y TortoiseSVN como herramientas del plan. | Actualizar G01–G02 a gestor local configurado y documentar REM como alternativa. Para G05, mostrar SVN CLI 1.14.5 ejecutado y el estado real de TortoiseSVN. |
| Alta | Las diapositivas 18–19 enumeran entregables sin una demostración concreta del cambio. | Mostrar evidencia de una fila de la ERS, dos solicitudes, historial de cinco requisitos, diff CR-01, conflicto, etiqueta y restauración. Declarar el estado de elicitación y aprobación. |
| Alta | La diapositiva 20 mezcla indicadores históricos con el trabajo en curso. | Identificar la línea base de **72 pruebas** y la nueva regresión de **76**, junto con sus revisiones. Mantener el humo JMeter como evidencia histórica y reportar RNF01 solo cuando existan p95 y tasa de error del escenario A. Vincular la suite Selenium al navegador y versión probados. |
| Alta | La diapositiva 21 dice «alcance validado» y presenta el conjunto como verificado. El informe aún solicita validación docente. | Usar «Alcance del primer avance» hasta registrar esa aprobación. Mostrar el entorno y reporte de reinicio de PostgreSQL, y separar evidencia de implementación y evidencia de aceptación. |
| Media | Las diapositivas 25–35 desarrollan G03 y G04. G01, G02 y G05 tienen principalmente resúmenes en 17–19. | Llevar tres láminas de evidencia de Ower al recorrido principal. Dejar diagramas de detalle, cálculo y tablas completas disponibles para preguntas. |
| Media | La portada y el informe manejan un mismo proyecto en distintos soportes. | Revisar render de portada y nombre completo del producto. Identificar responsable, estado, fecha y URL de evidencia en cada sección de guía. |
| Media | El informe remite a «Anexo A» en §7.3 y sus referencias terminan en la p. 11. | Añadir el anexo al paquete o remitir directamente a §§3.4–3.5 y a la matriz de trazabilidad. |

Fuentes: [presentación pública](https://www.canva.com/design/DAHbOlVg274/H0S_E2s4QksU5J4yyBnWSA/view?embed) y primer avance, §§3–9.

## Cruce con las nueve guías

| Guía | Evidencia a preparar para la revisión del equipo |
|---|---|
| G01 | ERS, cronograma y roles disponibles. Completar las dos sesiones de elicitación y revisar la equivalencia del gestor con las opciones de la guía. |
| G02 | Flujo, cinco requisitos, dos cambios e informes. Vincular decisión, responsables y cierre. Verificar el Anexo 21 con el docente si forma parte de la entrega. |
| G03 | Relacionar casos de uso y diagramas con los RF y demostrar que el SDD corresponde a la versión del código. |
| G04 | Mostrar supuestos, fórmulas y coherencia de escenarios de estimación con el libro enlazado en la diapositiva 30. |
| G05 | Mostrar log, rama, merge, etiqueta, recuperación y dump. Completar TortoiseSVN y confirmar los ejercicios del manual adjunto. |
| G06 | Enlazar el proyecto PILAR y mostrar explicación de activos, riesgos y controles. |
| G07 | Conservar casos y cobertura por componente con revisión y fecha de ejecución. |
| G08 | Adjuntar JMX y JTL de cada escenario, el cambio de rendimiento y las mediciones antes y después. |
| G09 | Adjuntar casos Selenium, registro de defectos y dictamen con criterios de aceptación y evidencias. |

Fuentes: [carpeta de las nueve guías](https://drive.google.com/drive/folders/1GtB48kdQBdxzxL-acrpcjvnMTz-DYls0?usp=sharing).

## Tres láminas de reemplazo para el bloque de Ower

| Lámina | Visual | Texto de soporte |
|---|---|---|
| G01 · Alcance verificable | [Flujo de préstamo](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g01-requerimientos/entregables/G01_Requerimientos/visuales/ciclo-prestamo.png) | 15 RF y 10 RNF cotejados. Mostrar RF09 y RF11 como ejemplo de decisión y entrega. Informar el estado de las sesiones. |
| G02 · Un cambio rastreable | [Trazabilidad CR-01](visuales/trazabilidad-cr01.png) | Dos solicitudes y cinco requisitos seguidos. CR-01 controla la carga demo, con prueba de regresión antes y después. |
| G05 · Una versión recuperable | [Registros SVN](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g05-gestion-de-configuracion/entregables/G05_Gestion_Configuracion/README.md) | Rama, conflicto real de texto, etiqueta y respaldo restaurado. Mostrar una línea del log, un marcador y el checksum. |

Usar la exposición para explicar las decisiones y dejar matrices, protocolos y registros como anexos. G08 y G09 se sustentan con las revisiones de sus responsables.
