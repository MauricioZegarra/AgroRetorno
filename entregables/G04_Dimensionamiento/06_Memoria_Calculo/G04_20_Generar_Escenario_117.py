#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""G04 1.20. Produce a *non-final* COSMIC 117 CFP scenario, without editing master files.

Usage from AgroRetorno's root:
    py -3 entregables/G04_Dimensionamiento/06_Memoria_Calculo/G04_20_Generar_Escenario_117.py
"""
from __future__ import annotations
import csv
import hashlib
import io
import subprocess
from collections import Counter
from datetime import datetime
from pathlib import Path

EXPECTED_BRANCH = 'feature/cielo-g04-dimensionamiento-estimacion'
EXPECTED_MASTER_SHA = '2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a'
ADDITIONS = (
    ('M112', 'M058', 'P14', 'D01 · Cuentas y productores', 'Salida: datos del productor relacionados con la solicitud', 'encargado/solicitud-detalle.html; atributo productor'),
    ('M113', 'M058', 'P14', 'D03 · Existencias', 'Salida: disponibilidad actual del material solicitado', 'encargado/solicitud-detalle.html; atributo disponibilidad'),
    ('M114', 'M058', 'P14', 'D05 · Préstamos', 'Salida: datos del préstamo relacionado, cuando existe', 'encargado/solicitud-detalle.html; atributo prestamo'),
    ('M115', 'M087', 'P19', 'D06 · Devoluciones y detalles', 'Salida: registros de devoluciones del préstamo', 'encargado/prestamo-detalle.html; tabla devoluciones'),
    ('M116', 'M101', 'P21', 'D05 · Préstamos', 'Salida: historial de préstamos del productor', 'productor/historial.html; pestaña prestamos'),
    ('M117', 'M101', 'P21', 'D06 · Devoluciones y detalles', 'Salida: historial de devoluciones del productor', 'productor/historial.html; pestaña devoluciones'),
)


def fail(message: str) -> None:
    raise SystemExit('ERROR G04 1.20: ' + message)


def main() -> None:
    try:
        repo = Path(subprocess.check_output(['git', '-C', str(Path(__file__).resolve().parent), 'rev-parse', '--show-toplevel'], text=True, stderr=subprocess.DEVNULL).strip())
        branch = subprocess.check_output(['git', '-C', str(repo), 'branch', '--show-current'], text=True).strip()
        head = subprocess.check_output(['git', '-C', str(repo), 'rev-parse', 'HEAD'], text=True).strip()
    except (OSError, subprocess.CalledProcessError):
        fail('Ejecute el script dentro del repositorio Git de AgroRetorno.')
    if branch != EXPECTED_BRANCH:
        fail(f'Rama incorrecta: {branch}. Esperada: {EXPECTED_BRANCH}.')

    base = repo / 'entregables' / 'G04_Dimensionamiento'
    source = base / '02_COSMIC' / 'G04_03_Movimientos_COSMIC.csv'
    if not source.is_file():
        fail(f'No existe el CSV maestro: {source}')
    raw = source.read_bytes()
    sha = hashlib.sha256(raw).hexdigest()
    if sha != EXPECTED_MASTER_SHA:
        fail(f'El hash del CSV maestro difiere del auditado: {sha}. No se generan resultados.')
    with io.StringIO(raw.decode('utf-8-sig'), newline='') as handle:
        reader = csv.DictReader(handle, delimiter=';')
        original = list(reader)
        fields = list(reader.fieldnames or [])
    required = {'Movimiento ID', 'Proceso', 'Nombre del proceso', 'RF', 'Actor', 'Tipo',
                'Grupo de datos', 'Detalle', 'Justificación', 'Código', 'Contar', 'Estado'}
    if not required.issubset(fields):
        fail('El CSV maestro no contiene todas las columnas esperadas.')
    ids = [r['Movimiento ID'] for r in original]
    if len(ids) != 111 or set(ids) != {f'M{i:03d}' for i in range(1,112)}:
        fail('El CSV de origen debe contener exactamente M001-M111 sin huecos ni duplicados.')
    types = Counter(r['Tipo'] for r in original if r['Contar'].strip().lower() in ('sí','si','s'))
    if types != Counter({'E':21,'X':21,'R':41,'W':28}):
        fail(f'Conteo de base inesperado: {dict(types)}; no generar escenario.')
    if any(r['Estado'].strip() != 'Por validar' for r in original):
        fail('Hay cambios en estados del CSV maestro; revise antes de generar un escenario.')

    by_id = {r['Movimiento ID']:r for r in original}
    output_rows = []
    extra_cols = ['Origen de fila', 'Operacion propuesta', 'Soporte / dependencia']
    for r in original:
        row = dict(r)
        row.update({'Origen de fila':'CSV maestro', 'Operacion propuesta':'Conservar sin cambiar en escenario de 117',
                    'Soporte / dependencia':'Sigue siendo candidato: revisar frontera y regla COSMIC'})
        if row['Movimiento ID'] in ('M058','M087','M101'):
            row['Operacion propuesta'] = 'Conservar X original y proponer X adicionales por grupo distinto'
        output_rows.append(row)
    for new_id, parent, proc, group, detail, support in ADDITIONS:
        row = dict(by_id[parent])
        if row['Proceso'] != proc or row['Tipo'] != 'X':
            fail(f'El origen del movimiento {parent} cambió inesperadamente.')
        row.update({'Movimiento ID':new_id,'Grupo de datos':group,'Detalle':detail,
                    'Justificación':'HIPOTESIS de Exit de grupo de datos distinto; desdoblamiento sujeto a norma COSMIC.',
                    'Contar':'Sí', 'Estado':'Por validar',
                    'Origen de fila':f'PROPUESTO desde {parent}',
                    'Operacion propuesta':'NUEVO X - únicamente en escenario alternativo',
                    'Soporte / dependencia':support})
        output_rows.append(row)
    totals = Counter(r['Tipo'] for r in output_rows if r['Contar'] == 'Sí')
    if len(output_rows) != 117 or totals != Counter({'E':21,'X':27,'R':41,'W':28}):
        fail('Fallo de control interno de los resultados de 117 CFP.')
    if len({r['Movimiento ID'] for r in output_rows}) != len(output_rows):
        fail('IDs duplicados detectados en el escenario.')
    output_dir = base / '04_Comparaciones'
    output_dir.mkdir(parents=True, exist_ok=True)
    destination = output_dir / 'G04_20_Escenario_117_COSMIC.csv'
    # Do not silently overwrite any scenario that a teammate may have edited.
    if destination.exists():
        fail('El escenario ya existe. Renómbrelo o elimínelo tras comprobar que no tiene cambios propios.')
    with destination.open('w', encoding='utf-8-sig', newline='') as handle:
        writer = csv.DictWriter(handle, fieldnames=fields + extra_cols, delimiter=';', extrasaction='ignore')
        writer.writeheader()
        writer.writerows(output_rows)
    out_sha = hashlib.sha256(destination.read_bytes()).hexdigest()
    report = output_dir / 'G04_20_Informe_Escenario_117.md'
    if report.exists():
        destination.unlink()
        fail('El informe ya existe. No se sobrescribe un archivo de revisión previo.')
    now = datetime.now().astimezone().isoformat(timespec='seconds')
    report.write_text(f'''# G04 — Paso 1.20 | Escenario alternativo de salidas COSMIC

**Generado:** {now}  
**Rama:** `{branch}`  
**HEAD local:** `{head}`  
**SHA-256 maestro:** `{sha}`  
**SHA-256 escenario:** `{out_sha}`

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

**Control de integridad:** {('El hash del maestro coincide con el registrado en G04_19.' if sha==EXPECTED_MASTER_SHA else 'ADVERTENCIA: el hash del maestro difiere del histórico; comprobar antes de usar la hipótesis.')}  
**CSV de salida:** `04_Comparaciones/G04_20_Escenario_117_COSMIC.csv`.

Este script **no modifica** `02_COSMIC/G04_03_Movimientos_COSMIC.csv` ni el libro maestro de 37 hojas.
''',encoding='utf-8')
    print('G04 Paso 1.20 OK | maestro=111 candidatos | escenario=117 candidatos | +6 X')
    print(f'SHA maestro: {sha}')
    print(f'Hash histórico coincide: {sha == EXPECTED_MASTER_SHA}')
    print('CSV escenario:', destination)
    print('Informe:', report)
    print('AVISO: escenario no definitivo. No se modificó el CSV maestro ni el XLSX.')


if __name__ == '__main__':
    main()
