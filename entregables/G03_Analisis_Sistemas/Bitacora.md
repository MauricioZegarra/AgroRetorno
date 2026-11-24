# Bitacora G03

Responsable: Cielo Cristal Meza Vizcarra

## 2026-11-23 - G03 Paso 1.1

Actividad: Analisis de arquitectura de AgroRetorno.

Procedimiento:
Revision de controladores, servicios, repositorios,
seguridad y migracion SQL del proyecto.

Resultado:
Se identificaron las capas y componentes principales.
Se elaboro el analisis y el diagrama de arquitectura.

Evidencias:
- 01_SDD/G03_01_Analisis_Arquitectura.md
- 02_Diagramas_UML/G03_Arquitectura_Componentes.puml
- 02_Diagramas_UML/G03_Arquitectura_Componentes.png

Estado: Borrador preparado para revision.

## 2026-11-23 - G03 Paso 1.2

Actividad: Elaboracion del diagrama de casos de uso.

Procedimiento:
Se revisaron los controladores web de AgroRetorno.
Se identificaron dos actores y 16 casos de uso.
Se preparo el diagrama UML y su catalogo de trazabilidad.

Archivos:
- 02_Diagramas_UML/G03_02_Casos_de_Uso.puml
- 02_Diagramas_UML/G03_02_Casos_de_Uso.svg
- 02_Diagramas_UML/G03_02_Casos_de_Uso.png
- 03_Casos_de_Uso/G03_02_Analisis_Casos_de_Uso.md

Estado: Modelo preparado para revision.

## 2026-11-23 - G03 Paso 1.3

Actividad:
Elaboracion del diagrama de clases UML.

Procedimiento:
Se analizaron las entidades Java de AgroRetorno,
sus atributos, metodos y relaciones JPA.

Se identificaron diez entidades persistentes.
Se prepararon diagramas del nucleo del sistema
y de los componentes de soporte.

Resultados:
- Modelo de clases del dominio.
- Identificacion de multiplicidades.
- Documentacion de relaciones y restricciones.
- Fuentes editables PlantUML y Graphviz.
- Diagramas PNG y SVG.
- Analisis tecnico y lista de revision.

Estado:
Modelo preparado, pendiente de revision final.


## 2026-11-23 - G03 Paso 1.4

Actividad:
Modelado UML de los procesos de solicitud,
aprobacion y entrega de materiales.

Procedimiento:
Se revisaron los metodos de ProductorController,
EncargadoSolicitudController, SolicitudService,
PrestamoService e InventarioService.

Se prepararon diagramas de secuencia para:
- RF06: registro de solicitudes.
- RF07: aprobacion de solicitudes.
- RF08: entrega fisica de materiales.

Resultados:
- Fuentes editables PlantUML.
- Vistas PNG y SVG.
- Analisis tecnico de interacciones.
- Matriz de correspondencia con codigo Java.
- Checklist de revision.

Hallazgo principal:
La aprobacion no modifica inventario.
El descuento ocurre en la entrega fisica.

Estado:
Modelos preparados. Pendiente revision visual
y verificacion final de las fuentes PlantUML.


## 2026-11-23 - G03 Paso 1.5

Actividad:
Especificacion de casos de uso y elaboracion
de mockups de AgroRetorno.

Procedimiento:
Se revisaron controladores, servicios, DTO
y plantillas Thymeleaf.

Se documentaron tres casos de uso:
- CU-01: Registrar solicitud de prestamo.
- CU-02: Evaluar solicitud y registrar entrega.
- CU-03: Registrar devolucion y cerrar prestamo.

Se prepararon cuatro prototipos visuales:
- M01: Nueva solicitud.
- M02: Revision y aprobacion.
- M03: Entrega fisica.
- M04: Devolucion.

Entregables:
- Casos de uso en Word y Markdown.
- Matriz de trazabilidad CSV.
- Mockups HTML, CSS y PNG.
- Analisis de correspondencia con el codigo.
- Checklist de revision.

Estado:
Material preparado, pendiente de revision
personal y consolidacion con el SDD.


## 2026-11-23 - G03 Paso 1.6

Actividad:
Consolidacion del documento de diseno de software.

Procedimiento:
Se integraron las vistas arquitectonicas,
diagramas UML, modelo de datos, especificaciones
de casos de uso, mockups y decisiones de diseno.

Se reviso la trazabilidad con la linea base
ampliada de requisitos de AgroRetorno.

Resultados:
- SDD consolidado en Word.
- Matriz de trazabilidad corregida.
- Identificadores RF y ED normalizados.
- Mockups y fuentes UML actualizados.
- Checklist para revision del SDD.

Estado:
Borrador consolidado.
Pendiente revision formal y confirmacion de ERS.


## 2026-11-23 - Exportacion del SDD a PDF

Actividad:
Normalizacion y exportacion del SDD de G03.

Procedimiento:
Se eliminaron los encabezados y pies de pagina.
Se conservaron las figuras, tablas y estructura del documento.
Se genero una version PDF de 15 paginas.

Entregables:
- G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx
- G03_06_SDD_IEEE1016_AgroRetorno_Cielo.pdf

Estado:
Versiones DOCX y PDF preparadas para revision.

