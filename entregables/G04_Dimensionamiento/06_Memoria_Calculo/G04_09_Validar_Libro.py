#!/usr/bin/env python3
"""Read-only numerical consistency audit for G04 AgroRetorno workbook.
Uses Python standard library only; does not edit Excel, infer hours, or approve scope.
"""
import collections
import hashlib
import math
import posixpath
import re
import sys
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

NS = {"m": "http://schemas.openxmlformats.org/spreadsheetml/2006/main",
      "r": "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
      "p": "http://schemas.openxmlformats.org/package/2006/relationships"}

def one_text(node):
    return "".join(t.text or "" for t in node.findall(".//m:t", NS))

class ExcelReader:
    def __init__(self, xlsx):
        self.archive = zipfile.ZipFile(xlsx)
        wb = ET.fromstring(self.archive.read("xl/workbook.xml"))
        rels = ET.fromstring(self.archive.read("xl/_rels/workbook.xml.rels"))
        paths = {}
        for rel in rels:
            rid = rel.get("Id")
            target = rel.get("Target", "")
            if target.startswith("/"):
                path = target.lstrip("/")
            elif target.startswith("xl/"):
                path = target
            else:
                path = posixpath.normpath(posixpath.join("xl", target))
            paths[rid] = path
        self.sheetpaths = {}
        for s in wb.findall(".//m:sheets/m:sheet", NS):
            self.sheetpaths[s.get("name")] = paths[s.get("{%s}id" % NS["r"])]
        self.strings = []
        if "xl/sharedStrings.xml" in self.archive.namelist():
            sst = ET.fromstring(self.archive.read("xl/sharedStrings.xml"))
            self.strings = [one_text(si) for si in sst.findall("m:si", NS)]
        self.cache = {}

    def sheet(self, name):
        if name in self.cache:
            return self.cache[name]
        root = ET.fromstring(self.archive.read(self.sheetpaths[name]))
        data = {}
        for c in root.findall(".//m:sheetData/m:row/m:c", NS):
            ref = c.get("r", "")
            typ = c.get("t")
            v = c.find("m:v", NS)
            form = c.find("m:f", NS)
            inline = c.find("m:is", NS)
            if typ == "inlineStr" and inline is not None:
                val = one_text(inline)
            elif v is None or v.text is None:
                val = None
            elif typ == "s":
                val = self.strings[int(v.text)]
            elif typ in ("str", "e"):
                val = v.text
            elif typ == "b":
                val = v.text == "1"
            else:
                try:
                    val = float(v.text)
                except ValueError:
                    val = v.text
            data[ref] = {"v": val, "f": form.text if form is not None else None}
        self.cache[name] = data
        return data

    def val(self, sheet, address, default=None):
        return self.sheet(sheet).get(address, {}).get("v", default)

    def formula(self, sheet, address):
        return self.sheet(sheet).get(address, {}).get("f")

    def rows(self, sheet, start, end, columns):
        return [{c: self.val(sheet, "%s%d" % (c, row)) for c in columns}
                for row in range(start, end + 1)]


def same(a, b, eps=1e-5):
    return isinstance(a, (int, float)) and abs(float(a) - float(b)) <= eps


def main():
    script = Path(__file__).resolve()
    root = script.parents[1]
    xlsx = script.parent / "G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx"
    if len(sys.argv) > 1:
        xlsx = Path(sys.argv[1]).resolve()
    output_dir = root / "04_Comparaciones"
    output_dir.mkdir(parents=True, exist_ok=True)
    out = output_dir / "G04_09_Informe_Validacion_Automatica.md"
    if not xlsx.exists():
        raise FileNotFoundError("No se encontro el libro Excel: %s" % xlsx)
    file_hash = hashlib.sha256(xlsx.read_bytes()).hexdigest()
    x = ExcelReader(xlsx)
    results = []
    notes = []

    def chk(name, condition, details, warning=False):
        status = "OBSERVACION" if warning else ("OK" if condition else "REVISAR")
        results.append((status, name, details))

    chk("Estructura del libro", len(x.sheetpaths) == 37,
        "%d hojas detectadas (modelo esperado: 37)" % len(x.sheetpaths))

    data = x.rows("NESMA_Datos", 6, 13, "ADIJ")
    data_count = sum(1 for r in data if str(r["A"] or "").startswith("D"))
    data_sum = sum(float(r["J"] or 0) for r in data)
    ilf = sum(1 for r in data if r["D"] == "ILF" and r["I"] == "Si")
    # Case/accent-insensitive comparison on cell text; do not assume exact encoding.
    yes = lambda value: str(value or "").strip().casefold() in ("si", "s\u00ed")
    ilf = sum(1 for r in data if r["D"] == "ILF" and yes(r["I"]))
    chk("NESMA archivos logicos", data_count == 8 and ilf == 8 and same(data_sum, 56),
        "%d candidatos; %d ILF incluidos; %.0f puntos preliminares de datos" % (data_count, ilf, data_sum))
    trx = x.rows("NESMA_Transacciones", 6, 28, "ACHI")
    trx_count = sum(1 for r in trx if str(r["A"] or "").startswith("P"))
    trx_yes = sum(1 for r in trx if yes(r["H"]))
    trx_sum = sum(float(r["I"] or 0) for r in trx)
    types = collections.Counter(r["C"] for r in trx if yes(r["H"]))
    chk("NESMA transacciones", trx_count == 23 and trx_yes == 21 and same(trx_sum, 75) and types == {"EI": 10, "EQ": 10, "EO": 1},
        "%d procesos candidatos; %d incluidos; EI=%d, EO=%d, EQ=%d; subtotal %.0f UFP" %
        (trx_count, trx_yes, types["EI"], types["EO"], types["EQ"], trx_sum))
    nesma_total = x.val("NESMA_Resumen", "B16")
    chk("NESMA total", same(nesma_total, data_sum + trx_sum),
        "Libro=%s UFP; comprobacion independiente=%.0f UFP" % (nesma_total, data_sum + trx_sum))
    notes.append("NESMA sigue siendo preliminar: validar DET, RET, FTR, D01/D07/D08, EIF y procesos P01/P02 con G01.")

    moves = x.rows("COSMIC_Movimientos", 6, 116, "ABFKLM")
    moves = [r for r in moves if str(r["A"] or "").startswith("M")]
    active = [r for r in moves if yes(r["K"]) ]
    counts = collections.Counter(r["F"] for r in active)
    total_cfp = sum(float(r["L"] or 0) for r in active)
    processes = {r["B"] for r in active}
    chk("COSMIC movimientos", len(moves) == 111 and len(active) == 111 and counts == {"E": 21, "X": 21, "R": 41, "W": 28} and same(total_cfp, 111),
        "%d candidatos, %d incluidos, %d procesos; E=%d X=%d R=%d W=%d, %.0f CFP" %
        (len(moves), len(active), len(processes), counts["E"], counts["X"], counts["R"], counts["W"], total_cfp))
    states = collections.Counter(str(r["M"] or "").strip() for r in moves)
    chk("COSMIC revision funcional pendiente", False,
        "Estados declarados en el libro: %s. No aprobar movimientos sin decisiones documentadas." % dict(states), warning=True)
    cfp = x.val("COSMIC_Resumen", "A7")
    chk("COSMIC resumen", same(cfp, total_cfp),
        "Resumen=%s CFP; suma de filas incluidas=%.0f CFP" % (cfp, total_cfp))

    kloc = x.val("COCOMO_Modelo", "B6")
    aa = x.val("COCOMO_Modelo", "B7")
    bb = x.val("COCOMO_Modelo", "B8")
    effort = x.val("COCOMO_Modelo", "B9")
    cc = x.val("COCOMO_Modelo", "B10")
    dd = x.val("COCOMO_Modelo", "B11")
    tdev = x.val("COCOMO_Modelo", "B12")
    source_lines = x.val("COCOMO_FuenteLOC", "D17")
    chk("COCOMO base Java", same(kloc, source_lines / 1000.0) and same(kloc, 3.491),
        "Líneas de codigo en libro=%s; KLOC=%s; fuente ZIP historica, no HEAD de GitHub" % (source_lines, kloc))
    independent_effort = aa * (kloc ** bb)
    independent_tdev = cc * (independent_effort ** dd)
    chk("COCOMO esfuerzo y duracion", same(effort, independent_effort) and same(tdev, independent_tdev),
        "E_libro=%s PM, E_recalculado=%.5f PM; T_libro=%s meses, T_recalculado=%.5f" %
        (effort, independent_effort, tdev, independent_tdev))
    chk("COCOMO formulas", bool(x.formula("COCOMO_Modelo", "B9")) and bool(x.formula("COCOMO_Modelo", "B12")),
        "B9=%s ; B12=%s" % (x.formula("COCOMO_Modelo", "B9"), x.formula("COCOMO_Modelo", "B12")))
    notes.append("COCOMO estima una base de Java productivo (80 archivos, 3491 lineas de codigo estimadas). Se excluyen HTML/CSS/JS, SQL, configuraciones y pruebas.")

    month_status = x.val("REALES_Resumen", "B5")
    hrs = x.rows("REALES_Registro", 7, 86, "ABCFHI")
    real_rows = [r for r in hrs if any(r[k] is not None for k in ("A", "B", "F"))]
    deviation_absolute = x.val("REALES_Resumen", "B13")
    deviation_relative = x.val("REALES_Resumen", "B14")
    chk("Horas y cobertura", month_status == "PARCIAL" and deviation_absolute is None and deviation_relative is None,
        "Estado del corte=%s; filas con datos=%d; desviaciones finales sin publicar=%s" %
        (month_status, len(real_rows), deviation_absolute is None and deviation_relative is None))
    if not real_rows:
        notes.append("No hay horas efectivas registradas en REALES_Registro. No deducir duracion de fechas de commits.")
    else:
        notes.append("Hay %d filas de registro de horas: revisar fechas, evidencia y estado VALIDADO antes de citar el total." % len(real_rows))
    notes.extend([
        "Los 131 UFP y los 111 CFP son unidades diferentes y no se convierten ni suman entre si.",
        "Todas las cifras son de un modelo de trabajo. La aprobacion funcional requiere G01 y revision del equipo.",
        "Esta auditoria solo comprueba estructura, valores cacheados y coherencia aritmetica; no ejecuta macros ni valida requisitos de usuario.",
    ])
    bad = sum(1 for status, _, _ in results if status == "REVISAR")
    warning_count = sum(1 for status, _, _ in results if status == "OBSERVACION")
    text = ["# G04 - Paso 1.9 | Auditoria automatica del libro",
            "", "**Proyecto:** AgroRetorno", "**Responsable:** Cielo Cristal Meza Vizcarra",
            "**Origen:** `%s`" % xlsx.name,
            "**SHA256 del Excel comprobado:** `%s`" % file_hash,
            "**Resultado:** %d controles REVISAR y %d observaciones de validacion." % (bad, warning_count),
            "", "| Estado | Comprobacion | Hallazgo aritmetico |", "|---|---|---|"]
    for status, name, detail in results:
        text.append("| %s | %s | %s |" % (status, name, detail.replace("|", "/")))
    text.extend(["", "## Limitaciones y pendientes"] + ["- " + n for n in notes] + [
        "", "## Dictamen", "",
        "**Consistencia aritmetica:** %s." % ("REQUIERE REVISION" if bad else "Sin discrepancias en los controles aplicados"),
        "**Validacion funcional definitiva:** PENDIENTE G01, decisiones NESMA/COSMIC y revision externa.",
        "**Horas reales del equipo:** registrar solo tiempo realmente trabajado y documentado.",
        "", "**Nota:** Esta salida automatica no es una certificacion del docente ni reemplaza la inspeccion manual de formulas.", ""])
    out.write_text("\n".join(text), encoding="utf-8")
    print("G04 Paso 1.9: %d comprobaciones | %d REVISAR | %d observaciones" % (len(results), bad, warning_count))
    print("Libro: %s" % xlsx)
    print("Reporte: %s" % out)
    for status, name, detail in results:
        print("[%s] %s: %s" % (status, name, detail))
    return 0 if bad == 0 else 2

if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as exc:
        print("ERROR: %s" % exc, file=sys.stderr)
        sys.exit(1)
