# G03 — Nota de alineación de identificadores (revisión 1.6)

Responsable: Cielo Cristal Meza Vizcarra · 23/11/2026

**Origen de las discrepancias:** los pasos 1.4 y 1.5 fueron redactados con identificadores usados en comentarios del código de una línea base anterior. La propuesta ampliada del primer avance asigna otros IDs. La ERS formal de G01 debe confirmar su adopción.

| Función | Etiqueta anterior en G03 | Línea base ampliada del primer avance |
|---|---|---|
| Registrar solicitud | RF06 | **RF07** |
| Aprobar solicitud | RF07 | **RF09** |
| Rechazar solicitud | (no diferenciado) | **RF10** |
| Entrega física | RF08 | **RF11** |
| Registrar devolución | RF09 | **RF13** |

## Segundo problema: numeración de casos de uso

El catálogo general G03 Paso 1.2 contiene CU-01 a CU-16. Las tres especificaciones detalladas del Paso 1.5 reutilizaron CU-01 a CU-03 para otros casos, creando una colisión.

La convención corregida conserva el catálogo general y renombra solamente las **especificaciones detalladas**:
- **ED-01**: registrar solicitud; corresponde al caso del catálogo **CU-03**.
- **ED-02**: evaluar, aprobar/rechazar y entregar; corresponde a **CU-10, CU-11, CU-12 y CU-13**.
- **ED-03**: devolución/cierre; corresponde a **CU-15**.

## Procedimiento de corrección

1. Sustituir los documentos anteriores de 01_SDD, 02_Diagramas_UML, 03_Casos_de_Uso y 04_Mockups por las versiones corregidas de este paquete.
2. Para los PNG de secuencia, la actualización se hizo sobre el rótulo del resumen visual; las fuentes `.puml` contienen los identificadores corregidos, con lo que podrán regenerarse.
3. Los archivos SVG anteriores de secuencia no se incluyen corregidos: volver a exportarlos desde los `.puml` corregidos antes de una entrega que los necesite.
4. Confirmar los IDs en la ERS final de G01. Si cambia la ERS, actualizar la matriz y volver a revisar G03.
5. El código Java original **no se modifica**: la corrección afecta solamente a artefactos de análisis.

**Estado:** normalización documental preparada. La aceptación de la línea base por el equipo queda pendiente.
