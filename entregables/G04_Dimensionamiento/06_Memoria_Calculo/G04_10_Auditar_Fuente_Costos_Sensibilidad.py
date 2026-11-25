#!/usr/bin/env python3
"""G04 Paso 1.10: auditoria independiente de fuente Java, costos y sensibilidad.
No modifica el libro XLSX. Usa solo bibliotecas estandar Python >= 3.9.
Ejecutar desde raiz del repositorio de AgroRetorno.
"""
import csv
import hashlib
import math
import pathlib
import re
import subprocess
import sys
from collections import Counter, defaultdict
from datetime import datetime, timezone

ROOT = pathlib.Path.cwd().resolve()
BASE = ROOT / 'entregables' / 'G04_Dimensionamiento'
OUT = BASE / '04_Comparaciones' / 'G04_10_Informe_Validacion_Tecnica.md'
SOURCE = ROOT / 'src' / 'main' / 'java'
SENS = BASE / '04_Comparaciones' / 'G04_06_Resultados_Sensibilidad.csv'
COST = BASE / '04_Comparaciones' / 'G04_05_Escenarios_Resumen.csv'
PHASE = BASE / '04_Comparaciones' / 'G04_05_Cronograma_Fases.csv'
BASE_KLOC = 3.491
HOURS_PM = 160.0
TARIFF = 20.0
DEDICATION = 0.8
COORDINATION = 0.15
EPS = 1e-6
checks = []
errors = []


def get_git(*args):
    x = subprocess.run(['git', *args], cwd=ROOT, capture_output=True, text=True)
    if x.returncode:
        raise RuntimeError('Git error: ' + x.stderr.strip())
    return x.stdout.strip()


def check(label, status, detail):
    checks.append((label, status, detail))
    if status == 'ERROR':
        errors.append(label + ': ' + detail)


def near(a, b, tol=EPS):
    return math.isclose(float(a), float(b), rel_tol=tol, abs_tol=tol)


def read_csv(path, sep):
    if not path.exists():
        raise FileNotFoundError(str(path))
    with path.open('r', encoding='utf-8-sig', newline='') as f:
        return list(csv.DictReader(f, delimiter=sep))


def cval(row, key):
    return float(row[key])


def effort(kloc):
    return 2.4 * kloc ** 1.05


def tdev(e):
    return 2.5 * e ** 0.38


def plan(k, tariff, ded, coord):
    e = effort(k)
    t = tdev(e)
    return {
        'Esfuerzo_PM': e,
        'TDEV_meses': t,
        'Costo_directo_1p_S': e * HOURS_PM * tariff,
        'Costo_directo_3p_S': e * HOURS_PM * tariff / (1.0 - coord),
        'Plazo_1p_meses': e / ded,
        'Plazo_3p_meses': max(t, e / (3.0 * ded * (1.0 - coord))),
    }


def count_source():
    files = sorted(SOURCE.rglob('*.java'))
    if not files:
        raise RuntimeError('No se encontro codigo Java en src/main/java')
    totals = defaultdict(lambda: [0, 0, 0])
    fingerprints = []
    for file in files:
        b = file.read_bytes()
        txt = b.decode('utf-8-sig')
        physical = len(txt.splitlines())
        noblock = re.sub(r'(?s)/\*.*?\*/', '', txt)
        noline = re.sub(r'(?m)^\s*//.*$', '', noblock)
        noinline = re.sub(r'(?m)\s+//.*$', '', noline)
        approx = sum(bool(x.strip()) for x in noinline.splitlines())
        grp = file.parent.name
        totals[grp][0] += 1
        totals[grp][1] += physical
        totals[grp][2] += approx
        digest = hashlib.sha256(b).hexdigest()
        fingerprints.append((file.relative_to(ROOT).as_posix(), digest))
    n = sum(x[0] for x in totals.values())
    physical = sum(x[1] for x in totals.values())
    approx = sum(x[2] for x in totals.values())
    return n, physical, approx, totals, fingerprints


def main():
    if not (ROOT / '.git').exists():
        raise RuntimeError('Abra PowerShell desde la raiz del repositorio AgroRetorno')
    branch = get_git('branch', '--show-current')
    if branch != 'feature/cielo-g04-dimensionamiento-estimacion':
        raise RuntimeError('Rama incorrecta: ' + branch + '. No se modifico ningun archivo.')
    sha = get_git('rev-parse', 'HEAD')

    files, phys, loc, pkgs, fingerprints = count_source()
    check('Java productivo / archivos', 'OK' if files == 80 else 'REVISAR',
          '%d archivos Java actuales (referencia historica 80)' % files)
    check('Java productivo / KLOC', 'OK',
          'Conteo actual %d LOC aproximadas = %.3f KLOC; modelo historico 3491 LOC = 3.491 KLOC' % (loc, loc / 1000))

    base = plan(BASE_KLOC, TARIFF, DEDICATION, COORDINATION)
    sensitivity = read_csv(SENS, ';')
    groups = Counter(r['Variable'] for r in sensitivity)
    expected_groups = {'Tamaño Java (KLOC)', 'Tarifa por hora', 'Dedicación por persona', 'Coordinación equipo 3'}
    if len(sensitivity) != 12 or set(groups) != expected_groups or any(v != 3 for v in groups.values()):
        check('Sensibilidad / estructura', 'ERROR',
              'Esperados 12 escenarios (4 variables x 3 cambios), encontrados %d, grupos %s' % (len(sensitivity), dict(groups)))
    else:
        check('Sensibilidad / estructura', 'OK', '12 escenarios; cada variable tiene 3 puntos')
    sensitivity_issues = []
    changes = {-0.2, 0.0, 0.2}
    for i, row in enumerate(sensitivity, 2):
        try:
            variable = row['Variable']
            change = cval(row, 'Cambio')
            if not any(near(change, v) for v in changes):
                sensitivity_issues.append('fila %d: cambio invalido' % i)
            k = cval(row, 'KLOC')
            tariff = cval(row, 'Tarifa_S_h')
            ded = cval(row, 'Dedicacion')
            coord = cval(row, 'Coordinacion')
            vals = plan(k, tariff, ded, coord)
            for name, val in vals.items():
                if not near(cval(row, name), val):
                    sensitivity_issues.append('fila %d, %s: archivo %.8f vs recomputado %.8f' % (i, name, cval(row, name), val))
            target = {
                'Tamaño Java (KLOC)': ('KLOC', BASE_KLOC * (1+change)),
                'Tarifa por hora': ('Tarifa_S_h', TARIFF * (1+change)),
                'Dedicación por persona': ('Dedicacion', DEDICATION * (1+change)),
                'Coordinación equipo 3': ('Coordinacion', COORDINATION * (1+change)),
            }.get(variable)
            if not target or not near(cval(row, target[0]), target[1]):
                sensitivity_issues.append('fila %d: variacion +/-20 no corresponde a %s' % (i, variable))
            deltas = {
                'Delta_Esfuerzo': vals['Esfuerzo_PM'] / base['Esfuerzo_PM'] - 1,
                'Delta_Costo_3p': vals['Costo_directo_3p_S'] / base['Costo_directo_3p_S'] - 1,
                'Delta_Plazo_1p': vals['Plazo_1p_meses'] / base['Plazo_1p_meses'] - 1,
            }
            for name, val in deltas.items():
                if not near(cval(row, name), val):
                    sensitivity_issues.append('fila %d: %s inconsistente' % (i, name))
        except (KeyError, ValueError, OverflowError, ZeroDivisionError) as e:
            sensitivity_issues.append('fila %d: %s' % (i, str(e)))
    check('Sensibilidad / 12 recalculos', 'ERROR' if sensitivity_issues else 'OK',
          '; '.join(sensitivity_issues[:8]) if sensitivity_issues else 'Ecuaciones y deltas reproducidos sin diferencias')

    costs = read_csv(COST, ',')
    cost_issues = []
    if len(costs) != 2:
        cost_issues.append('Debe haber exactamente 2 escenarios')
    for row in costs:
        try:
            n = int(float(row['Personas']))
            if n not in (1,3):
                cost_issues.append('Personas fuera de 1/3')
                continue
            months = base['Plazo_1p_meses'] if n == 1 else base['Plazo_3p_meses']
            direct = base['Costo_directo_1p_S'] if n == 1 else base['Costo_directo_3p_S']
            hours_prod = base['Esfuerzo_PM'] * HOURS_PM * (1.0 if n == 1 else 1/(1-COORDINATION))
            available = months*n*DEDICATION*HOURS_PM
            for field, v in [
                ('Plazo_meses',months), ('Costo_directo_S/',direct),
                ('Horas_productivas',hours_prod), ('Horas_disponibles',available),
                ('Costo_disponibilidad_S/',available*TARIFF),
                ('Utilizacion',hours_prod/available), ('Reserva_10pct_S/',direct*0.10),
                ('Directo_mas_reserva_S/',direct*1.10),
            ]:
                if not near(cval(row,field),v,2e-4):
                    cost_issues.append('%dp %s: archivo %s vs esperado %.4f' % (n,field,row[field],v))
        except (KeyError,ValueError,ZeroDivisionError) as e:
            cost_issues.append(str(e))
    check('Costos / escenarios 1p y 3p','ERROR' if cost_issues else 'OK',
          '; '.join(cost_issues[:8]) if cost_issues else 'Costos, horas, utilizacion y reserva al 10% verificados')

    phases = read_csv(PHASE, ',')
    phase_issues = []
    if len(phases) != 6:
        phase_issues.append('Se esperaban 6 fases; %d encontradas' % len(phases))
    total_weight = sum(cval(r,'Peso') for r in phases)
    if not near(total_weight,1.0):
        phase_issues.append('Suma de pesos %.8f, no 1.0' % total_weight)
    for suffix,months in [('1p',base['Plazo_1p_meses']),('3p',base['Plazo_3p_meses'])]:
        prev_end = 0.0
        for row in phases:
            start = cval(row,'Inicio_'+suffix+'_mes')
            end = cval(row,'Fin_'+suffix+'_mes')
            if not near(start,prev_end,1e-4):
                phase_issues.append('%s %s: fases no consecutivas' % (suffix,row['ID']))
            duration = (cval(row,'Peso')*months)
            if not near(end-start,duration,2e-4):
                phase_issues.append('%s %s: duracion inconsistente' % (suffix,row['ID']))
            prev_end = end
        if not near(prev_end,months,2e-4):
            phase_issues.append('%s no termina en mes %.4f' % (suffix,months))
    check('Cronograma / fases y pesos','ERROR' if phase_issues else 'OK',
          '; '.join(phase_issues[:8]) if phase_issues else '6 fases; pesos=100%; plazos y continuidad correctos')

    # A period cannot be declared complete merely because this script ran.
    report = [
        '# G04 - Paso 1.10 | Auditoria independiente de fuente, costos y sensibilidad',
        '',
        '**Rama:** `%s`  ' % branch,
        '**Commit base:** `%s`  ' % sha,
        '**Fecha local de ejecucion:** `%s`  ' % datetime.now().astimezone().isoformat(timespec='seconds'),
        '**Alcance:** Codigo Java productivo de `src/main/java` y CSV de escenarios de la rama G04.  ',
        '**Herramienta:** Python estandar; no se modifican los datos del XLSX.',
        '',
        '## Resultado verificable', '',
        '| Comprobacion | Estado | Evidencia / conclusion |',
        '|---|---|---|',
    ]
    for name,status,detail in checks:
        report.append('| %s | %s | %s |' % (name,status,detail.replace('|','/')))
    report.extend(['','## Recuento actual de codigo Java','',
      '| Paquete | Archivos | Lineas fisicas | LOC aproximadas |',
      '|---|---:|---:|---:|'])
    for name,v in sorted(pkgs.items()):
        report.append('| %s | %d | %d | %d |' % (name,*v))
    report.extend(['| **TOTAL** | **%d** | **%d** | **%d** |' %(files,phys,loc),''])
    if loc != 3491:
        report.extend([
          '**DECISION DE ALCANCE:** El modelo publicado de 3.491 KLOC queda anclado a `AgroRetorno-v1.0.0.zip` como linea base historica; el conteo actual es %d LOC (%.3f KLOC). No sustituir el valor en XLSX sin reestimar COCOMO y escenarios, documentar el cambio de version y actualizar las exportaciones.' % (loc,loc/1000),
          ''
        ])
    else:
        report.extend(['**DECISION DE ALCANCE:** El recuento actual coincide con las 3.491 LOC aproximadas del modelo historico. No se demuestra identidad de cada archivo por este solo resultado.',''])
    report.extend([
      '## Escenario nominal (hipotesis documentadas)', '',
      '- Tarifa: S/ 20 por hora; 160 h por persona-mes; dedicacion del 80%; coordinacion del 15% para tres personas.',
      '- COCOMO 81 basico, modo organico: esfuerzo `E=2.4*KLOC^1.05`; duracion `TDEV=2.5*E^0.38`.',
      '- El plazo para tres personas usa `max(TDEV, E/(3*dedicacion*(1-coordinacion)))`; **TDEV no se divide entre tres**.',
      '- Costo directo 1p = `E*160*tarifa`; 3p = `E*160*tarifa/(1-coordinacion)`.',
      '- Resultado nominal 1p: S/ %.2f y %.4f meses; 3p: S/ %.2f y %.4f meses.' % (
          base['Costo_directo_1p_S'],base['Plazo_1p_meses'],base['Costo_directo_3p_S'],base['Plazo_3p_meses']),
      '', '## Limites del dictamen', '',
      '- Las cifras de costo son **escenarios academicos**, no gastos observados ni cotizaciones.',
      '- La prueba de codigo mide Java productivo, no SQL, HTML, CSS, JavaScript, configuraciones ni tests.',
      '- Esta auditoria no valida los 8 ILF de NESMA ni los 111 movimientos funcionales de COSMIC; dichas decisiones requieren evidencia funcional.',
      '- No aporta horas reales ni firma de un revisor externo. Se mantiene el corte de observaciones real en PARCIAL.',
      '- La ERS definitiva G01 no esta publicada en el repositorio al generar esta version; la medicion funcional es de la linea base provisional identificada.',
      '', '## Dictamen', '',
      'Resultado de controles de este script: **%d OK, %d REVISAR, %d ERROR**.' % (
        sum(s=='OK' for _,s,_ in checks), sum(s=='REVISAR' for _,s,_ in checks), len(errors)),
      'Se consideran cerradas solo las comprobaciones aritmeticas cuyos estados son OK.',
      'No se declara aprobacion academica ni integracion a `main`.',
    ])
    OUT.parent.mkdir(parents=True,exist_ok=True)
    OUT.write_text('\n'.join(report)+'\n', encoding='utf-8-sig')
    for name,status,detail in checks:
        print('[%s] %s: %s' %(status,name,detail))
    print('Informe:',OUT)
    print('Resultado:',sum(s=='OK' for _,s,_ in checks),'OK /',len(errors),'ERROR')
    return 2 if errors else 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except Exception as exc:
        print('ERROR: ' + str(exc),file=sys.stderr)
        sys.exit(1)
