# Acta técnica de CR-01 | G02

| Campo | Registro |
|---|---|
| Cambio | Activar el modo demostración solo mediante configuración explícita. |
| Línea base | Git `a28b7e4`. La configuración anterior permitía el inicializador por omisión. |
| Alcance | Configuración de arranque, condición del inicializador, adenda ERS y pruebas. |
| Resultado técnico | Verificado con cuatro pruebas de configuración, 76 pruebas de regresión Maven y cuatro casos sobre PostgreSQL. [Resultado](verificacion/README.md#resultado-registrado). |
| Integración | Implementación SVN r6, merge r9 y etiqueta de práctica `v1.0.0` en r11. |
| Aprobación académica | Pendiente de registro por el responsable correspondiente. |
| Ejecución personal | Pendiente de evidencia del autor de la práctica. |

## Evidencias que debe revisar quien aprueba

1. El [análisis de impacto](Cambios.md#impacto) delimita el cambio de arranque y prevé el aprovisionamiento de una base vacía.
2. La [matriz](trazabilidad/Matriz_Trazabilidad.md#trazabilidad-de-cr-01) enlaza requisito, diseño, revisión, prueba e integración.
3. La [prueba de configuración](../../src/test/java/pe/edu/agroretorno/config/DemoDataDefaultTest.java) cubre la ausencia de variable, el valor falso, el valor verdadero y la ausencia de propiedad.
4. El [informe G05](https://github.com/MauricioZegarra/AgroRetorno/blob/feature/ower-g05-gestion-de-configuracion/entregables/G05_Gestion_Configuracion/README.md#regresión-de-cr-01) conserva ejecución anterior al cambio, ejecución en rama y regresión integrada.

## Registro de aprobación y cierre

| Revisión | Responsable | Fecha UTC | Decisión y motivo | Referencia de evidencia |
|---|---|---|---|---|
| Ejecución personal | Por registrar | Por registrar | Por registrar | Revisión propia, diff y salida de pruebas |
| Aprobación académica | Por registrar | Por registrar | Por registrar | Acta o constancia del responsable |
| Cierre académico | Por registrar | Por registrar | Por registrar | Aprobación y verificación aceptadas |

La cuenta que publica en GitHub identifica la entrega. El registro de laboratorio y el acta identifican la ejecución y la decisión de cada actividad.
