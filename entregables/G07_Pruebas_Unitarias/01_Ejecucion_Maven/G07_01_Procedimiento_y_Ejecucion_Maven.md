# G07 — Procedimiento, Entorno y Ejecución Reproducible en Maven (Paso 3.9)

## 1. Línea base y trazabilidad del código
- **Repositorio:** `https://github.com/MauricioZegarra/AgroRetorno.git`
- **Commit base evaluado (`main`):** `a28b7e4` (`Update README.md` sobre `6a582df v1.0`).
- **Archivos de compilación añadidos en la rama `feature/marco-g07-pruebas-unitarias`:** `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/`, `.gitignore` tomados exactamente de `AgroRetorno-v1.0.0.zip`. **No se modificó ningún archivo Java ni recurso de `src/`.**

## 2. Versiones exactas de herramientas (ver `00_entorno_y_versiones.txt`)
| Componente / Herramienta | Versión verificada |
|---|---|
| Sistema operativo | Nobara Linux 44 (kernel `7.2.11-200.nobara.fc44.x86_64`, `amd64`) |
| JDK de compilación y pruebas | Eclipse Temurin OpenJDK `17.0.17` (`/usr/lib/jvm/java-17-temurin-jdk`) |
| Apache Maven Wrapper | `3.9.16` |
| Spring Boot Parent | `3.5.16` |
| JUnit 5 (Jupiter) + AssertJ | Gestionado por Spring Boot `3.5.16` (AssertJ `3.27.7`) |
| Mockito (`MockitoExtension`) | Gestionado por `spring-boot-starter-test` `3.5.16` |
| Maven Surefire Plugin | `3.5.6` (`<runOrder>random</runOrder>`, `<trimStackTrace>false</trimStackTrace>`) |
| JaCoCo Maven Plugin | `0.8.14` (meta `LINE COVEREDRATIO >= 0.80` en `pe.edu.agroretorno.service`) |
| Base de datos de pruebas integración | H2 en memoria (`MODE=PostgreSQL`, perfil `test`) |

## 3. Comando exacto de ejecución reproducible
```bash
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw clean verify -Djava.version=17 -B
```
*(En entornos con JDK 21 instalado en `JAVA_HOME`, el comando equivalente es simplemente `./mvnw clean verify`.)*

## 4. Resultados obtenidos (2026-11-24T19:28:36-05:00)
| Clase de Prueba | Tipo | Ejecuciones | Aprobadas | Fallos | Errores | Omitidas | Tiempo (s) |
|---|---|---:|---:|---:|---:|---:|---:|
| `InventarioServiceTest` | Unitaria (Mockito) | 17 | 17 | 0 | 0 | 0 | 0.821 |
| `SolicitudServiceTest` | Unitaria (Mockito) | 21 | 21 | 0 | 0 | 0 | 0.285 |
| `PrestamoServiceTest` | Unitaria (Mockito) | 6 | 6 | 0 | 0 | 0 | 0.021 |
| `DevolucionServiceTest` | Unitaria (Mockito) | 13 | 13 | 0 | 0 | 0 | 0.299 |
| `ProcesosNegocioIntegrationTest` | Integración (H2) | 5 | 5 | 0 | 0 | 0 | 2.608 |
| `SeguridadIntegrationTest` | Integración / Seguridad | 10 | 10 | 0 | 0 | 0 | 8.883 |
| **TOTAL** | **6 clases** | **72** | **72** | **0** | **0** | **0** | **24.645 (build)** |

- **Regla `jacoco:check`:** `Analyzed bundle 'agroretorno' with 72 classes` → `All coverage checks have been met.`
- **Cobertura en `pe.edu.agroretorno.service`:** **97.6 % líneas** (`496/508`), **81.7 % ramas** (`201/246`), **88.1 % métodos** (`89/101`).
