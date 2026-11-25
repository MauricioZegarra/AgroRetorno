# G04 — Paso 1.1. Precátalogo para COSMIC
**Responsable:** Cielo Cristal Meza Vizcarra  
**Estado:** catálogo de procesos candidatos; sin tamaño CFP calculado.

## Paso 1. Definir el mismo límite de software
AgroRetorno procesa eventos de PRODUCTOR y ENCARGADO, mantiene datos internamente y devuelve respuestas por su interfaz web.

## Paso 2. Identificar procesos disparados por eventos
Consultar la hoja **Procesos** del Excel. No se parte del número de pantallas sino de objetivos reconocibles:
- Registrar una solicitud.
- Aprobar o rechazar una solicitud.
- Registrar una entrega.
- Registrar una devolución.
- Mantener y consultar productores/materiales/inventario.
- Consultar solicitudes, préstamos, historial y reportes.

## Paso 3. Medir posteriormente movimientos de datos
Para cada proceso funcional válido se identificarán:
- **E — Entry:** grupo de datos que cruza desde el usuario hacia el software.
- **X — Exit:** grupo de datos enviado desde el software hacia el usuario.
- **R — Read:** grupo leído desde almacenamiento persistente.
- **W — Write:** grupo escrito en almacenamiento persistente.

Estos movimientos serán verificados contra el código de servicios y repositorios, evitando contarlos por atributo, tabla o endpoint.

## Paso 4. Reconciliar con NESMA
El alcance funcional y la línea base deben coincidir, pero los resultados finales serán **CFP** en COSMIC y **FP** en NESMA: no se suman ni se convierten directamente.
