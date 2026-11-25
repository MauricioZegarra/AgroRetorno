PRAGMA user_version = 1;
BEGIN TRANSACTION;
CREATE TABLE requisitos (
            id TEXT PRIMARY KEY,
            nombre TEXT NOT NULL
        );
INSERT INTO "requisitos" VALUES('RF01','Inicio y cierre de sesión');
INSERT INTO "requisitos" VALUES('RF02','Acceso por roles');
INSERT INTO "requisitos" VALUES('RF05','Ajustes de existencias');
INSERT INTO "requisitos" VALUES('RF07','Registro de solicitudes');
INSERT INTO "requisitos" VALUES('RF13','Devoluciones');
CREATE TABLE eventos (
            secuencia INTEGER PRIMARY KEY,
            fecha_utc TEXT NOT NULL,
            id TEXT NOT NULL REFERENCES requisitos (id),
            estado TEXT NOT NULL CHECK (estado IN (
                'registrado', 'analizado', 'aprobado', 'rechazado', 'implementado',
                'implementacion_observada', 'verificado', 'cerrado'
            )),
            responsable TEXT NOT NULL,
            referencia TEXT NOT NULL,
            evidencia TEXT NOT NULL,
            aceptacion_funcional TEXT NOT NULL
        );
INSERT INTO "eventos" VALUES(1,'2026-11-25T20:08:16.943559Z','RF01','registrado','Codex','r3','ERS.md y primer avance §3.4','pendiente');
INSERT INTO "eventos" VALUES(2,'2026-11-25T20:08:16.943559Z','RF02','registrado','Codex','r3','ERS.md y primer avance §3.4','pendiente');
INSERT INTO "eventos" VALUES(3,'2026-11-25T20:08:16.943559Z','RF05','registrado','Codex','r3','ERS.md y primer avance §3.4','pendiente');
INSERT INTO "eventos" VALUES(4,'2026-11-25T20:08:16.943559Z','RF07','registrado','Codex','r3','ERS.md y primer avance §3.4','pendiente');
INSERT INTO "eventos" VALUES(5,'2026-11-25T20:08:16.943559Z','RF13','registrado','Codex','r3','ERS.md y primer avance §3.4','pendiente');
INSERT INTO "eventos" VALUES(6,'2026-11-25T20:10:24.006935Z','RF01','analizado','Codex','r4','Criterio de aceptación y selección de casos de RF01','pendiente');
INSERT INTO "eventos" VALUES(7,'2026-11-25T20:10:24.006935Z','RF02','analizado','Codex','r4','Criterio de aceptación y selección de casos de RF02','pendiente');
INSERT INTO "eventos" VALUES(8,'2026-11-25T20:10:24.006935Z','RF05','analizado','Codex','r4','Criterio de aceptación y selección de casos de RF05','pendiente');
INSERT INTO "eventos" VALUES(9,'2026-11-25T20:10:24.006935Z','RF07','analizado','Codex','r4','Criterio de aceptación y selección de casos de RF07','pendiente');
INSERT INTO "eventos" VALUES(10,'2026-11-25T20:10:24.006935Z','RF13','analizado','Codex','r4','Criterio de aceptación y selección de casos de RF13','pendiente');
INSERT INTO "eventos" VALUES(11,'2026-11-25T20:11:58.980902Z','RF01','implementacion_observada','Codex','Git a28b7e4 / SVN r9','Código base e inspección del componente referenciado en la matriz','pendiente');
INSERT INTO "eventos" VALUES(12,'2026-11-25T20:11:58.980902Z','RF02','implementacion_observada','Codex','Git a28b7e4 / SVN r9','Código base e inspección del componente referenciado en la matriz','pendiente');
INSERT INTO "eventos" VALUES(13,'2026-11-25T20:11:58.980902Z','RF05','implementacion_observada','Codex','Git a28b7e4 / SVN r9','Código base e inspección del componente referenciado en la matriz','pendiente');
INSERT INTO "eventos" VALUES(14,'2026-11-25T20:11:58.980902Z','RF07','implementacion_observada','Codex','Git a28b7e4 / SVN r9','Código base e inspección del componente referenciado en la matriz','pendiente');
INSERT INTO "eventos" VALUES(15,'2026-11-25T20:11:58.980902Z','RF13','implementacion_observada','Codex','Git a28b7e4 / SVN r9','Código base e inspección del componente referenciado en la matriz','pendiente');
INSERT INTO "eventos" VALUES(16,'2026-11-25T20:12:59Z','RF01','verificado','Codex','r9','SEG-02 y DemoDataDefaultTest en maven-casos.csv','pendiente');
INSERT INTO "eventos" VALUES(17,'2026-11-25T20:12:59Z','RF02','verificado','Codex','r9','SEG-04 y SEG-05 en maven-casos.csv','pendiente');
INSERT INTO "eventos" VALUES(18,'2026-11-25T20:12:59Z','RF05','verificado','Codex','r9','PU-11 a PU-15 en maven-casos.csv','pendiente');
INSERT INTO "eventos" VALUES(19,'2026-11-25T20:12:59Z','RF07','verificado','Codex','r9','PU-16 a PU-21, SEG-07 y SEG-10 en maven-casos.csv','pendiente');
INSERT INTO "eventos" VALUES(20,'2026-11-25T20:12:59Z','RF13','verificado','Codex','r9','PU-34 a PU-42 e IT-01 en maven-casos.csv','pendiente');
COMMIT;
