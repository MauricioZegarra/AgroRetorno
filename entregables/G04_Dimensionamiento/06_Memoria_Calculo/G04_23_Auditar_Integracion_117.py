#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""G04 Paso 1.23 (FIX B7): auditoria independiente, solo lectura, del modelo COSMIC 117.

Requiere Python 3 estandar. No necesita Microsoft Excel, openpyxl ni librerias
externas. Lee el formato Open XML (XLSX) con zipfile + ElementTree; no modifica
ningun XLSX, CSV ni la historia de Git. Genera solo el informe G04_23.

Uso, desde la raiz del repositorio:
 py -3 entregables/G04_Dimensionamiento/06_Memoria_Calculo/G04_23_Auditar_Integracion_117.py
"""
from __future__ import annotations

import collections
import csv
import datetime as dt
import hashlib
import math
import posixpath
import re
import subprocess
import sys
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

NS = {
    'm': 'http://schemas.openxmlformats.org/spreadsheetml/2006/main',
    'r': 'http://schemas.openxmlformats.org/officeDocument/2006/relationships',
}
S = 'Validado internamente G03'
EXPECTED_OLD_CSV_SHA = '2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a'
EXPECTED_OLD_XLSX_SHA = '2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472'
FIELDS = ['Movimiento ID', 'Proceso', 'Nombre del proceso', 'RF', 'Actor', 'Tipo', 'Grupo de datos', 'Detalle', 'Justificación', 'Código']
EXPECTED_TYPES = {'E': 21, 'X': 27, 'R': 41, 'W': 28}
EXPECTED_EXTRA = {
    'M112': ('P14', 'X', 'D01'),
    'M113': ('P14', 'X', 'D03'),
    'M114': ('P14', 'X', 'D05'),
    'M115': ('P19', 'X', 'D06'),
    'M116': ('P21', 'X', 'D05'),
    'M117': ('P21', 'X', 'D06'),
}


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open('rb') as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b''):
            h.update(chunk)
    return h.hexdigest()


def rows_csv(path: Path) -> list[dict[str, str]]:
    with path.open('r', encoding='utf-8-sig', newline='') as f:
        return list(csv.DictReader(f, delimiter=';'))


def text_of(el: ET.Element) -> str:
    return ''.join((n.text or '') for n in el.findall('.//m:t', NS))


class WorkbookReader:
    def __init__(self, filename: Path):
        self.filename = filename
        self.z = zipfile.ZipFile(filename)
        wb = ET.fromstring(self.z.read('xl/workbook.xml'))
        rel = ET.fromstring(self.z.read('xl/_rels/workbook.xml.rels'))
        map_path = {}
        for r in rel:
            rid, target = r.get('Id'), r.get('Target', '')
            if target.startswith('/'):
                path = target.lstrip('/')
            elif target.startswith('xl/'):
                path = target
            else:
                path = posixpath.normpath(posixpath.join('xl', target))
            map_path[rid] = path
        self.paths = {}
        for sh in wb.findall('.//m:sheets/m:sheet', NS):
            self.paths[sh.get('name')] = map_path[sh.get('{' + NS['r'] + '}id')]
        self.shared = []
        if 'xl/sharedStrings.xml' in self.z.namelist():
            sst = ET.fromstring(self.z.read('xl/sharedStrings.xml'))
            self.shared = [text_of(item) for item in sst.findall('m:si', NS)]
        self._cache = {}
        self.formula_errors = []

    def cells(self, sheet: str) -> dict[str, tuple[object, str | None]]:
        if sheet not in self._cache:
            root = ET.fromstring(self.z.read(self.paths[sheet]))
            data = {}
            for cell in root.findall('.//m:sheetData/m:row/m:c', NS):
                at = cell.get('r', '')
                kind = cell.get('t', '')
                vn = cell.find('m:v', NS)
                fn = cell.find('m:f', NS)
                ins = cell.find('m:is', NS)
                raw = vn.text if vn is not None else None
                if kind == 'inlineStr' and ins is not None:
                    value = text_of(ins)
                elif raw is None:
                    value = None
                elif kind == 's':
                    value = self.shared[int(raw)]
                elif kind in ('str', 'e'):
                    value = raw
                elif kind == 'b':
                    value = raw == '1'
                else:
                    try:
                        value = float(raw)
                    except (TypeError, ValueError):
                        value = raw
                data[at] = (value, fn.text if fn is not None else None)
                if kind == 'e':
                    self.formula_errors.append(f'{sheet}!{at}: {raw}')
            self._cache[sheet] = data
        return self._cache[sheet]

    def val(self, sh: str, adr: str):
        return self.cells(sh).get(adr, (None, None))[0]

    def formula(self, sh: str, adr: str):
        return self.cells(sh).get(adr, (None, None))[1]

    def close(self):
        self.z.close()


def near(a, b, *, atol=1e-7) -> bool:
    if isinstance(a, (int, float)) and isinstance(b, (int, float)):
        return math.isclose(a, b, abs_tol=atol, rel_tol=1e-9)
    return a == b


def is_yes(v) -> bool:
    return str(v or '').strip().casefold() in {'si', 'sí'}


def parse_report_hash(text: str, label: str) -> str | None:
    m = re.search(r'^\*\*' + re.escape(label) + r':\*\*\s*`([a-f0-9]{64})`\s*$', text, re.I | re.M)
    return m.group(1).lower() if m else None


def git_info(repo: Path) -> tuple[str, str]:
    def run(args):
        r = subprocess.run(['git', '-C', str(repo), *args], text=True, capture_output=True)
        return r.stdout.strip() if r.returncode == 0 else '(no disponible)'
    return run(['branch', '--show-current']), run(['rev-parse', 'HEAD'])


def main() -> int:
    script = Path(__file__).resolve()
    base = script.parent.parent
    repo = base.parent.parent
    orig_csv = base / '02_COSMIC' / 'G04_03_Movimientos_COSMIC.csv'
    new_csv = base / '02_COSMIC' / 'G04_22_Movimientos_117_ModeloInterno.csv'
    orig_xlsx = script.parent / 'G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx'
    new_xlsx = script.parent / 'G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx'
    step22_md = base / '04_Comparaciones' / 'G04_22_Informe_Integracion_Excel.md'
    out = base / '04_Comparaciones' / 'G04_23_Informe_Auditoria_Independiente_117.md'
    necessary = [orig_csv, new_csv, orig_xlsx, new_xlsx, step22_md]
    missing = [str(p) for p in necessary if not p.is_file()]
    if missing:
        raise FileNotFoundError('Faltan archivos: ' + '; '.join(missing))

    validations: list[tuple[str, str, str]] = []
    def chk(name: str, correct: bool, detail: str) -> None:
        validations.append(('OK' if correct else 'REVISAR', name, detail))

    branch, head = git_info(repo)
    chk('Rama G04', branch == 'feature/cielo-g04-dimensionamiento-estimacion', f'{branch}; HEAD={head[:12]}')
    old_csv_hash, old_xlsx_hash = sha256(orig_csv), sha256(orig_xlsx)
    new_csv_hash, new_xlsx_hash = sha256(new_csv), sha256(new_xlsx)
    chk('Integridad CSV maestro', old_csv_hash == EXPECTED_OLD_CSV_SHA, old_csv_hash)
    chk('Integridad libro original', old_xlsx_hash == EXPECTED_OLD_XLSX_SHA, old_xlsx_hash)
    report = step22_md.read_text(encoding='utf-8-sig')
    h_new_csv = parse_report_hash(report, 'SHA-256 CSV derivado')
    h_new_xlsx = parse_report_hash(report, 'SHA-256 XLSX nuevo')
    chk('Hashes de las nuevas salidas', new_csv_hash == h_new_csv and new_xlsx_hash == h_new_xlsx,
        f'CSV={new_csv_hash}; XLSX={new_xlsx_hash}; coinciden con informe 1.22={new_csv_hash == h_new_csv and new_xlsx_hash == h_new_xlsx}')

    old_rows, new_rows = rows_csv(orig_csv), rows_csv(new_csv)
    chk('Filas del CSV', len(old_rows) == 111 and len(new_rows) == 117,
        f'maestro={len(old_rows)}; modelo interno={len(new_rows)}')
    ids = [r.get('Movimiento ID', '') for r in new_rows]
    chk('IDs completos y unicos', ids == [f'M{i:03d}' for i in range(1, 118)] and len(set(ids)) == 117,
        f'unicos={len(set(ids))}; M001...M117, secuencia={ids == [f"M{i:03d}" for i in range(1, 118)]}')
    diffs = []
    for a, b in zip(old_rows, new_rows):
        for k in FIELDS:
            if a.get(k) != b.get(k):
                diffs.append((a.get('Movimiento ID'), k))
    chk('Maestro preservado', len(diffs) == 0, f'campos modificados en M001-M111: {len(diffs)}; muestra={diffs[:4]}')
    counts = collections.Counter(r.get('Tipo') for r in new_rows)
    chk('Distribucion COSMIC CSV', counts == EXPECTED_TYPES,
        f'E={counts["E"]}, X={counts["X"]}, R={counts["R"]}, W={counts["W"]}; total={sum(counts.values())}')
    extra_bad = []
    for id_, (proc, typ, grp) in EXPECTED_EXTRA.items():
        row = next((r for r in new_rows if r.get('Movimiento ID') == id_), {})
        if row.get('Proceso') != proc or row.get('Tipo') != typ or not (row.get('Grupo de datos') or '').startswith(grp):
            extra_bad.append(id_)
    chk('Seis Exit adicionales', not extra_bad, f'M112-M117, incorrectos={extra_bad or "ninguno"}')
    excluded = [r.get('Movimiento ID') for r in new_rows if not is_yes(r.get('Contar'))]
    bad_states = [r.get('Movimiento ID') for r in new_rows if r.get('Estado') != S]
    chk('Inclusion interna y estado CSV', not excluded and not bad_states,
        f'Contar distinto de Si={excluded[:8]}; estado interno diferente={bad_states[:8]}')

    orig = WorkbookReader(orig_xlsx)
    new = WorkbookReader(new_xlsx)
    try:
        same_sheet_names = list(orig.paths) == list(new.paths)
        chk('37 hojas preservadas', len(orig.paths) == len(new.paths) == 37 and same_sheet_names,
            f'origen={len(orig.paths)}; nuevo={len(new.paths)}; nombres/orden coinciden={same_sheet_names}')
        required_sheets = {'COSMIC_Movimientos', 'COSMIC_Procesos', 'COSMIC_Resumen', 'NESMA_Datos',
                           'NESMA_Transacciones', 'NESMA_Resumen', 'COCOMO_Modelo',
                           'COCOMO_FuenteLOC', 'COMPARACION_Metodos', 'REALES_Registro', 'REALES_Resumen'}
        if not required_sheets.issubset(new.paths):
            missing_sheets = required_sheets - set(new.paths)
            chk('Hojas para auditar', False, ', '.join(sorted(missing_sheets)))
        else:
            chk('Hojas para auditar', True, 'Estan las hojas COSMIC, NESMA, COCOMO, comparacion y horas reales')

        bad_cell = []
        bad_formula = []
        bad_cache = []
        if 'COSMIC_Movimientos' in new.paths:
            for i, r in enumerate(new_rows):
                row = i + 6
                for j, field in enumerate(FIELDS):
                    col = chr(ord('A') + j)
                    actual = new.val('COSMIC_Movimientos', f'{col}{row}')
                    if ('' if actual is None else str(actual)) != r.get(field, ''):
                        bad_cell.append(f'{col}{row}/{field}')
                if not is_yes(new.val('COSMIC_Movimientos', f'K{row}')):
                    bad_cell.append(f'K{row}')
                if new.val('COSMIC_Movimientos', f'M{row}') != S:
                    bad_cell.append(f'M{row}')
                form = new.formula('COSMIC_Movimientos', f'L{row}') or ''
                if not re.search(rf'K{row}\s*=\s*"S[ií]"', form, re.I):
                    bad_formula.append(f'L{row}: {form}')
                if not near(new.val('COSMIC_Movimientos', f'L{row}'), 1):
                    bad_cache.append(f'L{row}={new.val("COSMIC_Movimientos", f"L{row}")}')
            chk('117 filas del XLSX vs CSV', not bad_cell,
                f'diferencias={len(bad_cell)}; muestra={bad_cell[:7]}')
            chk('117 formulas L de inclusion', not bad_formula and not bad_cache,
                f'formulas incorrectas={len(bad_formula)}, resultados distintos de 1={len(bad_cache)}; muestra={bad_formula[:3]+bad_cache[:3]}')
            chk('Sin fila extra M118', new.val('COSMIC_Movimientos', 'A123') is None,
                f'A123={new.val("COSMIC_Movimientos", "A123")}')

        bad_range = []
        if 'COSMIC_Procesos' in new.paths:
            for r in range(6, 29):
                for col in 'FGHI':
                    expr = new.formula('COSMIC_Procesos', f'{col}{r}') or ''
                    if '$122' not in expr or '$116' in expr:
                        bad_range.append(f'{col}{r}: {expr}')
            chk('92 formulas SUMIFS ampliadas', not bad_range,
                f'limite hasta fila 122; formulas con error={len(bad_range)}; muestra={bad_range[:4]}')
            for col, expected in [('F',21),('G',27),('H',41),('I',28),('J',117)]:
                chk(f'Total COSMIC_Procesos {col}30', near(new.val('COSMIC_Procesos', f'{col}30'), expected),
                    f'observado={new.val("COSMIC_Procesos", f"{col}30")}; esperado={expected}')
        for name, addr, expected in [
            ('COSMIC_Resumen', 'A7', 117), ('COSMIC_Resumen', 'G7', 117),
            ('COMPARACION_Metodos', 'E8', 117), ('NESMA_Resumen', 'B16', 131),
            ('COCOMO_Modelo', 'B6', 3.491), ('COCOMO_FuenteLOC', 'D17', 3491),
        ]:
            if name in new.paths:
                chk(f'{name}!{addr}', near(new.val(name, addr), expected),
                    f'valor={new.val(name, addr)}; esperado={expected}')

        if 'NESMA_Datos' in new.paths and 'NESMA_Transacciones' in new.paths:
            data = [{c: new.val('NESMA_Datos', f'{c}{n}') for c in 'ADIJ'} for n in range(6,14)]
            trx = [{c: new.val('NESMA_Transacciones', f'{c}{n}') for c in 'ACHI'} for n in range(6,29)]
            ilf_count = sum(r['D'] == 'ILF' and is_yes(r['I']) for r in data)
            ilf_points = sum(float(r['J'] or 0) for r in data)
            trx_count = sum(is_yes(r['H']) for r in trx)
            trx_points = sum(float(r['I'] or 0) for r in trx)
            chk('NESMA detalle reproducido', ilf_count == 8 and near(ilf_points, 56) and trx_count == 21 and near(trx_points, 75),
                f'ILF={ilf_count}; datos={ilf_points}; transacciones={trx_count}; puntos={trx_points}')

        # Independencia: comprobar que NO se cambiaron ni formulas ni contenidos de
        # COCOMO, NESMA, costos, cronogramas, sensibilidad y registro de horas.
        # Las caches de formulas numericas pueden diferir por recálculo; por eso se
        # distingue contenido/formula de cache y se usa tolerancia numérica.
        allowed_changed = {'COSMIC_Movimientos', 'COSMIC_Procesos', 'COSMIC_Resumen', 'COMPARACION_Metodos'}
        diff_values, diff_formulas = [], []
        recalculos_esperados = []
        compared_sheets = 0
        for sh in orig.paths:
            if sh in allowed_changed or sh not in new.paths:
                continue
            compared_sheets += 1
            old_cells = orig.cells(sh)
            new_cells = new.cells(sh)
            for address in set(old_cells) | set(new_cells):
                old_v, old_f = old_cells.get(address, (None, None))
                new_v, new_f = new_cells.get(address, (None, None))
                if old_f != new_f:
                    diff_formulas.append(f'{sh}!{address}')
                if not near(old_v, new_v):
                    # El resumen historico COCOMO_Comparacion!B7 apunta a COSMIC_Resumen!A7.
                    # Cuando COSMIC cambia de 111 a 117 CFP, Excel recalcula esta cache:
                    # NO es una edicion manual ni un cambio en el modelo de esfuerzo COCOMO.
                    # Excepcion muy restringida: celda, formula, valores e igualdad con fuente.
                    if (sh, address) == ('COCOMO_Comparacion', 'B7') and (
                        old_f == new_f == 'COSMIC_Resumen!A7'
                        and near(old_v, 111) and near(new_v, 117)
                        and near(old_v, orig.val('COSMIC_Resumen', 'A7'))
                        and near(new_v, new.val('COSMIC_Resumen', 'A7'))
                    ):
                        recalculos_esperados.append(f'{sh}!{address}: {old_v:g} -> {new_v:g} (formula sin cambios)')
                    else:
                        diff_values.append(f'{sh}!{address}: {old_v!s} -> {new_v!s}')
        chk('Hojas ajenas a COSMIC sin ediciones', not diff_formulas and not diff_values,
            f'hojas comparadas={compared_sheets}; formulas modificadas={len(diff_formulas)}; '
            f'valores inesperados={len(diff_values)}; recalculos de formula permitidos={len(recalculos_esperados)}; '
            f'muestra formulas={diff_formulas[:3]}, valores={diff_values[:3]}')
        chk('COCOMO_Comparacion B7: vinculo COSMIC recalculado', len(recalculos_esperados) == 1,
            f'esperado 111 -> 117 por =COSMIC_Resumen!A7; encontrado={recalculos_esperados or "ninguno"}')

        if 'REALES_Registro' in new.paths and 'REALES_Resumen' in new.paths:
            active_hours = sum(any(new.val('REALES_Registro', f'{c}{i}') not in (None, '') for c in 'ABF') for i in range(7,87))
            status = new.val('REALES_Resumen', 'B5')
            chk('Horas reales sin inventar', active_hours == 0 and status == 'PARCIAL',
                f'filas registradas={active_hours}; corte={status}; sin observaciones finales estimadas')

        # Revisar todas las 37 hojas por errores Excel (no usar solo el resumen).
        for sh in new.paths:
            new.cells(sh)
        chk('Sin errores de celda Excel', len(new.formula_errors) == 0,
            f'errores={len(new.formula_errors)}; ejemplos={new.formula_errors[:6]}')
    finally:
        orig.close()
        new.close()

    checks = len(validations)
    bad = sum(1 for state, *_ in validations if state == 'REVISAR')
    now = dt.datetime.now().astimezone().isoformat(timespec='seconds')
    lines = [
        '# G04 - Paso 1.23 | Auditoria independiente del libro COSMIC 117', '',
        f'**Ejecucion local:** {now}  ',
        f'**Rama:** `{branch}`  ',
        f'**HEAD:** `{head}`  ',
        f'**SHA-256 libro G04_22:** `{new_xlsx_hash}`  ',
        f'**SHA-256 CSV G04_22:** `{new_csv_hash}`  ',
        f'**Resultado:** {checks} controles; {checks-bad} OK; {bad} REVISAR.', '',
        '| Estado | Control | Evidencia |', '|---|---|---|',
    ]
    for status, name, detail in validations:
        lines.append(f'| {status} | {name} | {detail.replace("|", "/").replace(chr(10), " ")} |')
    lines.extend([
        '', '## Interpretacion y limites', '',
        '- Esta auditoria independiente lee los valores y formulas OOXML del XLSX; no ejecuta una segunda vez Excel.',
        '- Verifica integridad de CSV, rangos, caches y correspondencia entre Excel/CSV, y preservacion de otras hojas.',
        '- Los 117 CFP son **modelo interno G03**, no certificacion COSMIC ni validacion de una ERS G01 externa.',
        '- El estado `Validado internamente G03` es una decision interna, NO significa que la edicion oficial del metodo COSMIC haya sido acreditada para cada fila.',
        '- NESMA (131 UFP) y COSMIC (117 CFP) no son unidades convertibles ni deben sumarse.',
        '- No se generan horas reales. Con cero registros, no existe variacion observada de esfuerzo.',
        '- No se modifico ningun libro ni CSV. Solo se escribio este informe de auditoria.', '',
        '**Dictamen automatico:** ' + ('CONSISTENCIA TECNICA SIN DISCREPANCIAS' if bad == 0 else 'REVISAR LAS DISCREPANCIAS ANTES DE PUBLICAR'), '',
    ])
    out.write_text('\n'.join(lines), encoding='utf-8')
    print(f'G04 Paso 1.23 | {checks} controles | {checks-bad} OK | {bad} REVISAR')
    print(f'Rama={branch} | HEAD={head[:12]} | 117 CFP internos | 131 UFP NESMA')
    print(f'Informe: {out}')
    for status, name, detail in validations:
        if status == 'REVISAR':
            print(f'[REVISAR] {name}: {detail}')
    if bad:
        print('NO PUBLICAR 1.23 hasta revisar las diferencias. Los XLSX no fueron modificados.')
    return 0 if bad == 0 else 2


if __name__ == '__main__':
    try:
        sys.exit(main())
    except Exception as exc:
        print('ERROR AUDITORIA G04 1.23:', exc, file=sys.stderr)
        sys.exit(1)
