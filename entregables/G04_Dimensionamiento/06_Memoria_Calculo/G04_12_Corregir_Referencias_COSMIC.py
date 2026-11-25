#!/usr/bin/env python3
"""G04 Paso 1.12 - Verificacion de trazabilidad de COSMIC contra Java.

No modifica XLSX, CSV de origen ni estados de aprobacion funcional.
Compatible con Python 3.12 en Windows.
Ejecutar desde la raiz del repositorio: py -3 ruta/al/script.py
"""
import csv
import datetime as dt
import hashlib
import pathlib
import re
import subprocess
import sys
from collections import Counter, defaultdict

ROOT = pathlib.Path.cwd()
BASE = ROOT / 'entregables' / 'G04_Dimensionamiento'
SOURCE = BASE / '02_COSMIC' / 'G04_03_Movimientos_COSMIC.csv'
OUTPUT = BASE / '04_Comparaciones'
CSV_OUT = OUTPUT / 'G04_12_Control_COSMIC_Filas.csv'
REPORT = OUTPUT / 'G04_12_Auditoria_Trazabilidad_COSMIC.md'
FIELDS = ['Movimiento ID', 'Proceso', 'Nombre del proceso', 'RF', 'Actor',
          'Tipo', 'Grupo de datos', 'Detalle', 'Justificacion original',
          'Codigo declarado', 'Fuente Java', 'Referencia detectada',
          'Repeticion potencial', 'Foco metodologico', 'Estado de origen',
          'Contar origen', 'Decision tecnica', 'Justificacion de decision']

def fatal(message):
    sys.exit('ERROR: ' + message)

def git_value(*args):
    try:
        return subprocess.check_output(['git', *args], cwd=ROOT,
                                       text=True, stderr=subprocess.DEVNULL).strip()
    except (OSError, subprocess.CalledProcessError):
        return 'no disponible'

def clean(text):
    return str(text or '').strip()

def append_unique(parts, item):
    if item not in parts:
        parts.append(item)

def main():
    if not (ROOT / '.git').exists() or not (ROOT / 'src' / 'main' / 'java').is_dir():
        fatal('Ejecutar desde la raiz de AgroRetorno (con carpeta src/main/java).')
    branch = git_value('branch', '--show-current')
    if branch != 'feature/cielo-g04-dimensionamiento-estimacion':
        fatal('Rama incorrecta: ' + branch)
    if not SOURCE.is_file():
        fatal('No existe ' + str(SOURCE))
    with SOURCE.open('r', encoding='utf-8-sig', newline='') as inp:
        source_rows = list(csv.DictReader(inp, delimiter=';'))
    required = ['Movimiento ID', 'Proceso', 'RF', 'Tipo', 'Grupo de datos',
                'Detalle', 'Codigo', 'Contar', 'Estado']
    if not source_rows or not all(x in source_rows[0] for x in required):
        # The source header uses a non-ascii accented spelling in Codigo.
        required = [x if x != 'Codigo' else 'C\u00f3digo' for x in required]
        if not source_rows or not all(x in source_rows[0] for x in required):
            fatal('Cabeceras COSMIC inesperadas: revisar archivo CSV.')
    java_index = defaultdict(list)
    for path in (ROOT / 'src' / 'main' / 'java').rglob('*.java'):
        java_index[path.stem].append(path)
    counts = Counter((clean(r['Proceso']), clean(r['Tipo']), clean(r['Grupo de datos']))
                     for r in source_rows)
    output = []
    observed = Counter()
    for r in source_rows:
        identifier = clean(r.get('Movimiento ID'))
        pointer = clean(r.get('C\u00f3digo') or r.get('Codigo'))
        # Some movements name two alternative controllers separated by /.
        # Check EACH class.method independently instead of treating the pair
        # as one impossible Java method. This is traceability, not COSMIC approval.
        alternatives = [x.strip() for x in pointer.split(' / ') if x.strip()]
        matching = []
        unresolved = []
        for alias in alternatives:
            parts = alias.rsplit('.', 1)
            if len(parts) != 2:
                unresolved.append(alias)
                continue
            class_name, method_name = parts
            candidates = java_index.get(class_name, [])
            hits = []
            for java_path in candidates:
                txt = java_path.read_text(encoding='utf-8-sig')
                if method_name and re.search(r'\b' + re.escape(method_name) + r'\s*\(', txt):
                    hits.append(java_path)
            if hits:
                matching.append(hits[0])
            else:
                unresolved.append(alias)
        pointer_ok = bool(alternatives) and not unresolved
        source_path = '; '.join(str(p.relative_to(ROOT)).replace('\\','/') for p in matching)
        if len(alternatives) > 1:
            observed['multi_alias'] += 1
            if pointer_ok:
                observed['multi_resolved'] += 1
        observed['found' if pointer_ok else 'missing'] += 1
        proc, typ, grp = clean(r['Proceso']), clean(r['Tipo']), clean(r['Grupo de datos'])
        same_tuple = counts[(proc,typ,grp)]
        focus = []
        if not pointer_ok:
            append_unique(focus, 'CODIGO_NO_LOCALIZADO')
        if same_tuple > 1:
            append_unique(focus, 'MISMO_PROCESO_TIPO_GRUPO')
        if grp.startswith('D08'):
            append_unique(focus, 'HISTORIAL_FUNCIONAL_O_TECNICO')
            observed['d08'] += 1
        if typ == 'X' and re.search(r'confirmaci[o\u00f3]n|mensaje|exito|\u00e9xito|acuse', clean(r['Detalle']), re.I):
            append_unique(focus, 'X_PODRIA_SER_CONTROL')
        if grp.startswith('FILTRO') or grp.startswith('REPORTE'):
            append_unique(focus, 'GRUPO_TRANSITORIO_O_DERIVADO')
        observed['risk' if focus else 'regular'] += 1
        output.append({
            'Movimiento ID':identifier, 'Proceso':proc,
            'Nombre del proceso':clean(r.get('Nombre del proceso')),
            'RF':clean(r.get('RF')), 'Actor':clean(r.get('Actor')),
            'Tipo':typ, 'Grupo de datos':grp, 'Detalle':clean(r.get('Detalle')),
            'Justificacion original':clean(r.get('Justificaci\u00f3n')),
            'Codigo declarado':pointer, 'Fuente Java':source_path,
            'Referencia detectada':'SI (todos los metodos alternativos)' if pointer_ok and len(alternatives)>1 else ('SI (nombre de metodo)' if pointer_ok else 'NO'),
            'Repeticion potencial':'SI' if same_tuple > 1 else 'NO',
            'Foco metodologico':'; '.join(focus) if focus else 'SIN_ALERTA_ESTRUCTURAL',
            'Estado de origen':clean(r.get('Estado')),
            'Contar origen':clean(r.get('Contar')),
            'Decision tecnica':'SIN_DECISION_FUNCIONAL',
            'Justificacion de decision':'No se marca validado automaticamente'
        })
    OUTPUT.mkdir(parents=True, exist_ok=True)
    with CSV_OUT.open('w', encoding='utf-8-sig', newline='') as out:
        writer=csv.DictWriter(out, fieldnames=FIELDS, delimiter=';')
        writer.writeheader();writer.writerows(output)
    movement_counts=Counter(x['Tipo'] for x in output if x['Contar origen'].lower() in ('si','s\u00ed','yes'))
    d08=[x for x in output if x['Grupo de datos'].startswith('D08')]
    missed=[x for x in output if x['Referencia detectada'].startswith('NO')]
    duplicates=[x for x in output if x['Repeticion potencial']=='SI']
    sha=hashlib.sha256(SOURCE.read_bytes()).hexdigest()
    today=dt.datetime.now().astimezone().isoformat(timespec='seconds')
    lines=[
        '# G04 - Paso 1.12 | Correccion de referencias COSMIC vs Java', '',
        '**Fecha de ejecucion:** '+today+'  ',
        '**Rama:** `'+branch+'`  ',
        '**Commit examinado:** `'+git_value('rev-parse','--short','HEAD')+'`  ',
        '**Archivo base:** `'+str(SOURCE.relative_to(ROOT)).replace('\\','/')+'`  ',
        '**SHA256 del CSV base:** `'+sha+'`', '',
        '## Resultado', '',
        '| Control | Resultado |', '|---|---:|',
        '| Movimientos leidos | '+str(len(output))+' |',
        '| Procesos funcionales distintos | '+str(len(set(x['Proceso'] for x in output)))+' |',
        '| Punteros clase.metodo detectados en Java | '+str(observed['found'])+' |',
        '| Punteros no localizados | '+str(len(missed))+' |',
        '| Referencias con controladores alternativos | '+str(observed['multi_alias'])+' |',
        '| Referencias alternativas resueltas | '+str(observed['multi_resolved'])+' |',
        '| Filas D08 (historial) | '+str(len(d08))+' |',
        '| Filas con misma terna proceso/tipo/grupo | '+str(len(duplicates))+' |',
        '| Filas sin alertas estructurales | '+str(observed['regular'])+' |',
        '| Entradas E incluidas | '+str(movement_counts['E'])+' |',
        '| Salidas X incluidas | '+str(movement_counts['X'])+' |',
        '| Lecturas R incluidas | '+str(movement_counts['R'])+' |',
        '| Escrituras W incluidas | '+str(movement_counts['W'])+' |',
        '', '## Foco: historial D08', '',
        'D08 cuenta con consulta visible de historial (EncargadoReporteController.historial y HistorialService.consultar). ',
        'Esto impide clasificar automaticamente todos sus movimientos como logging tecnico. ',
        'A su vez, la existencia de una llamada historialService.registrar no valida por si sola un movimiento funcional COSMIC.', '',
        '| ID | Proceso | Tipo | Detalle | Codigo declarado |', '|---|---|---|---|---|']
    for x in d08:
        lines.append('| '+' | '.join(x[k].replace('|','/') for k in ('Movimiento ID','Proceso','Tipo','Detalle','Codigo declarado'))+' |')
    lines.extend(['', '## Resolucion P18: referencias a dos controladores', '',
        'M080-M083 identifican la misma operacion funcional candidata por dos roles; las referencias',
        '`ProductorController.misPrestamos` y `EncargadoPrestamoController.listar` se verifican por separado.',
        'En el codigo, ambos controladores llaman a `PrestamoService`, que consulta `PrestamoRepository`.',
        'La ruta de Productor filtra por identidad y la de Encargado por estado.',
        '',
        '**M082:** `PrestamoRepository` utiliza JOIN FETCH de Prestamo con Solicitud para construir',
        '`PrestamoResponse`. Esto demuestra dependencia de datos, pero NO demuestra por si solo',
        'que D04 deba contarse como un R independiente en COSMIC. Registrar su razonamiento',
        'funcional antes de modificar las decisiones del libro.',
        '', '## Movimientos con referencia Java no localizada', ''])
    if missed:
        lines.extend(['| ID | Codigo declarado |','|---|---|'])
        lines.extend('| '+x['Movimiento ID']+' | `'+x['Codigo declarado']+'` |' for x in missed)
    else:
        lines.append('Ninguno segun busqueda textual. Esto NO prueba correspondencia semantica.')
    lines.extend(['', '## Limites y decision', '',
        '- El informe identifica referencias textuales, alertas de duplicacion potencial y focos de revision; **no aprueba** movimientos por si solo.',
        '- El mismo proceso/tipo/grupo puede necesitar justificacion o eliminacion del duplicado; decidirlo con el codigo y los requisitos.',
        '- Cada fila de `G04_12_Control_COSMIC_Filas.csv` conserva su estado original y exige decision funcional documentada.',
        '- La ERS definitiva de G01 y las horas reales no se infieren ni se fabrican.',
        '- No se modifica el Excel de 37 hojas ni los 111 estados originales.',
        '- Se preservan los reportes anteriores del Paso 1.11 como evidencia historica. La correccion queda documentada por separado.'])
    REPORT.write_text('\n'.join(lines)+'\n',encoding='utf-8')
    print('G04 Paso 1.12 - referencias alternativas COSMIC')
    print(f'Filas={len(output)} | Java localizado={observed["found"]} | no localizado={len(missed)} | D08={len(d08)}')
    print(f'Referencias alternativas={observed["multi_alias"]} | resueltas={observed["multi_resolved"]}')
    print(f'Movimientos incluidos: {dict(sorted(movement_counts.items()))}')
    print('CSV:',CSV_OUT)
    print('Informe:',REPORT)
    print('ATENCION: no equivale a validacion funcional ni modifica estados del XLSX.')

if __name__=='__main__':
    main()
