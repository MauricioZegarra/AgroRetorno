# Verificación de laboratorio | G02

La prueba ejecuta el mismo binario de AgroRetorno contra PostgreSQL 16.11 con una base sintética aislada y una instancia de restauración separada. El objetivo es comprobar el efecto de CR-01 sobre los datos y distinguir persistencia operativa de recuperación.

| Caso | Operación | Esperado |
|---|---|---|
| LAB-01 | Arrancar con una base vacía y sin `DEMO_DATA`. | Flyway aplica el esquema, la pantalla de login responde y las 10 tablas de aplicación quedan vacías. |
| LAB-02 | Arrancar la misma aplicación con `DEMO_DATA=true`. | Se registran seis usuarios ficticios y sus datos de demostración. |
| LAB-03 · RNF04 | Reiniciar PostgreSQL y arrancar otra vez con demo habilitado. | Conteo y huella por tabla coinciden con la carga inicial, sin registros demo duplicados. |
| LAB-04 · RNF10 | Restaurar un `pg_dump` en otra instancia y medir las mismas tablas. | Conteo y huella por tabla coinciden con el origen. |

Las huellas se calculan sobre las filas ordenadas por ID. Se publican conteos y huellas agregadas de 10 tablas con datos ficticios. El respaldo PostgreSQL se genera dentro del laboratorio y el respaldo SVN se conserva en G05.

## Reproducción

Desde la raíz de la rama G02 en Linux, con JDK 21, Bash, Docker, Git, curl y OpenSSL disponibles, ejecutar:

```bash
bash entregables/G02_Proceso_Requerimientos/verificacion/postgresql.sh /tmp/agroretorno-resultados
```

El procedimiento usa puertos efímeros de localhost para el origen y una segunda instancia para restauración. Ejecuta Maven, inicia la aplicación, toma cuatro instantáneas CSV, compara las filas de la muestra y elimina los contenedores de prueba al salir.

## Resultado registrado

La ejecución con PostgreSQL 16.11 terminó el 25 de noviembre de 2026 a las 21:02 UTC. La regresión Maven obtuvo **76 pruebas aprobadas**, sin fallos, errores ni omisiones. El login respondió HTTP 200 en los tres arranques del laboratorio.

| Caso | Resultado observado | Evidencia |
|---|---|---|
| LAB-01 | 10 tablas migradas, todas con cero filas. | [Base sin modo demo](evidencia/01-vacio.csv) |
| LAB-02 | Seis usuarios y datos ficticios de negocio. | [Después de habilitar demo](evidencia/02-demo.csv) |
| LAB-03 | Reinicio de base y aplicación con la misma identidad de registros en las 10 tablas. | [Después del reinicio](evidencia/03-reinicio.csv) |
| LAB-04 | Restauración en otra instancia con las mismas 10 tablas, conteos y huellas. | [Después de la restauración](evidencia/04-restaurado.csv) |

[Consola resumida, versión de Java, cobertura y revisión Git](evidencia/resumen.txt).
