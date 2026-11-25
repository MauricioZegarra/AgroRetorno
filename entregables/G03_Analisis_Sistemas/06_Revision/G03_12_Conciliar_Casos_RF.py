#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""G03 Paso 1.12. Verifica y alinea CU detallados, RF y versiones Word.

--aplicar: edita dos textos existentes, el Word de casos detallados, Bitacora.md
           y genera un acta; respalda archivos existentes fuera del repositorio.
Por defecto solo verifica, sin escritura. Requiere python-docx para revisar Word.
No modifica SDD, PDF, G04 ni crea PR/merge.
"""
from __future__ import annotations

import argparse
import csv
import hashlib
import io
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path
from datetime import datetime
from docx import Document

B = 'feature/cielo-g03-analisis-de-sistemas'
# Etiquetas esperadas por caso: admitimos versiones antiguas, intermedias y corregidas.
# NO se cambian los CU del catálogo general, solo encabezados de los tres ED.
TITLES = (
    ('01', 'Registrar solicitud de préstamo', 'RF07'),
    ('02', 'Evaluar solicitud y registrar entrega física', 'RF09, RF10, RF11'),
    ('03', 'Registrar devolución y cerrar préstamo', 'RF13'),
)


def normalize_doc_titles(doc):
    """Corrige solo índice y títulos de ED-01/02/03, con control estricto.

    Permite índices intermedios p.ej. ED-02 · ... · RF09, RF11.
    Si el título no corresponde exactamente al caso conocido, falla y
    muestra su texto, sin guardar ningún archivo.
    """
    modifications = []
    for num, title, reqs in TITLES:
        expected_index = f'ED-{num} · {title} · {reqs}'
        expected_heading = f'ED-{num} — {title}'
        index_pattern = re.compile(
            r'^(?:CU|ED)-' + num + r'\s*[·•]\s*' + re.escape(title)
            + r'\s*[·•]\s*RF\d+(?:\s*,\s*RF\d+)*\s*$'
        )
        heading_pattern = re.compile(
            r'^(?:CU|ED)-' + num + r'\s*[—–-]\s*' + re.escape(title) + r'\s*$'
        )
        indexes = [p for p in doc.paragraphs if index_pattern.fullmatch(p.text.strip())]
        headings = [p for p in doc.paragraphs if heading_pattern.fullmatch(p.text.strip())]
        if len(indexes) != 1 or len(headings) != 1:
            candidates = [p.text for p in doc.paragraphs if title.casefold() in p.text.casefold()]
            die(f'Caso {num}: se esperaba un índice y un título inequívocos; '
                f'índices={len(indexes)} títulos={len(headings)}; encontrados={candidates!r}')
        for para, required in ((indexes[0], expected_index), (headings[0], expected_heading)):
            before = para.text
            if before.strip() == required:
                continue
            if len(para.runs) == 1:
                para.runs[0].text = required
            else:
                # La etiqueta se compone de varios runs: preservar estilo del
                # primer segmento y no asumir que los demás son iguales.
                para.runs[0].text = required
                for extra in para.runs[1:]:
                    extra.text = ''
            modifications.append(f'{before} -> {required}')
    for num, title, reqs in TITLES:
        for expected in (f'ED-{num} · {title} · {reqs}', f'ED-{num} — {title}'):
            if sum(p.text == expected for p in doc.paragraphs) != 1:
                die('DOCX no quedo con una etiqueta única y correcta: ' + expected)
    return modifications

# Admitir etapas documentadas de migracion, incluida la tabla 2 RF09, RF11.
TABLE_REQS = [
    (('RF06',), 'RF07'),
    (('RF07, RF08', 'RF09, RF11'), 'RF09, RF10, RF11'),
    (('RF09',), 'RF13'),
]
ENTRY = '''\n## G03 - Paso 1.12: Conciliacion de trazabilidad interna (25/11/2026)\n\n- Matriz de los tres casos detallados alineada con la linea base RF01-RF15 de G03.\n- ED-02 incluye explicitamente RF09 (aprobar), RF10 (rechazar) y RF11 (entregar).\n- Encabezados/requisitos del DOCX de casos detallados normalizados a ED-01/ED-02/ED-03.\n- Mantiene la convencion de catalogo CU-01..CU-16; los ED son especificaciones detalladas.\n- Evidencia: 06_Revision/G03_12_Acta_Conciliacion_Interna.md.\n- Revision local de G03 ya documentada; ERS G01 definitiva y revision externa no acreditadas.\n- No se toco G04, el SDD ni su PDF; sin merge.\n'''


def die(message):
    raise RuntimeError('G03 1.12: ' + message)


def sha(path):
    return hashlib.sha256(Path(path).read_bytes()).hexdigest()


def git(repo, *args):
    return subprocess.check_output(['git', '-C', str(repo), *args], text=True, stderr=subprocess.DEVNULL).strip()


def readutf(path):
    return path.read_text(encoding='utf-8-sig')


def writeutf(path, text, has_bom=True):
    path.write_text(text, encoding='utf-8-sig' if has_bom else 'utf-8', newline='\n')


def valrow(t):
    return ' '.join(x.strip() for x in t)


def main():
    p=argparse.ArgumentParser(description=__doc__)
    p.add_argument('--aplicar', action='store_true', help='Modificar versiones locales y generar acta')
    a=p.parse_args()
    script=Path(__file__).resolve()
    root=script.parents[1]  # G03_Analisis_Sistemas
    repo=Path(git(root,'rev-parse','--show-toplevel'))
    branch=git(repo,'branch','--show-current')
    head=git(repo,'rev-parse','HEAD')
    if branch!=B:die('Rama incorrecta: '+branch+' (primero cambiar a '+B+')')
    case=root/'03_Casos_de_Uso'/'G03_05_CU02_Evaluar_solicitud_y_registrar_entrega_física.md'
    csvpath=root/'03_Casos_de_Uso'/'G03_05_Matriz_Trazabilidad.csv'
    full=root/'03_Casos_de_Uso'/'G03_06_Matriz_RF_CU_Codigo_Interfaz.csv'
    docpath=root/'03_Casos_de_Uso'/'G03_05_Casos_de_Uso_Detallados.docx'
    sd=root/'01_SDD'/'G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx'
    bitacora=root/'Bitacora.md'
    out=root/'06_Revision'/'G03_12_Acta_Conciliacion_Interna.md'
    for f in [case,csvpath,full,docpath,sd,bitacora]:
        if not f.is_file():die('Archivo obligatorio ausente: '+str(f))
    # No cambiar nada si la matriz general RF no contiene la regla de negocio a conciliar.
    allcsv=list(csv.DictReader(io.StringIO(readutf(full)),delimiter=';'))
    rf10=[x for x in allcsv if x.get('RF (propuesto G01)')=='RF10']
    if len(rf10)!=1 or 'ED-02' not in rf10[0].get('CU catálogo / especificación',''):
        die('La matriz general no confirma la correspondencia RF10 / ED-02.')
    lines=readutf(case)
    old='**Requisitos vinculados:** RF09, RF11'
    new='**Requisitos vinculados:** RF09, RF10, RF11'
    if old in lines:updated_case=lines.replace(old,new,1); needs_md=True
    elif new in lines:updated_case=lines;needs_md=False
    else:die('No se encontro el encabezado de requisitos esperado en ED-02.')
    rawcsv=readutf(csvpath)
    reader=csv.DictReader(io.StringIO(rawcsv),delimiter=';')
    fields=reader.fieldnames
    rows=list(reader)
    if not fields or not all(f in fields for f in ['Caso de uso','RF','Actor','Pantalla real Thymeleaf','Método/servicio','Mockup','Estado']):
        die('La matriz detallada tiene columnas imprevistas.')
    hits=[i for i,x in enumerate(rows) if x.get('Caso de uso')=='ED-02' and x.get('RF')=='RF10']
    if len(hits)>1:die('RF10 esta duplicado para ED-02.')
    if not any(x.get('Caso de uso')=='ED-02' and x.get('RF')=='RF09' for x in rows) or not any(x.get('Caso de uso')=='ED-02' and x.get('RF')=='RF11' for x in rows):
        die('Faltan filas ED-02/RF09 o ED-02/RF11; no insertar a ciegas.')
    need_csv=len(hits)==0
    if need_csv:
        ix=next(i for i,x in enumerate(rows) if x.get('Caso de uso')=='ED-02' and x.get('RF')=='RF09')
        r={k:'' for k in fields}
        r.update({'Caso de uso':'ED-02','RF':'RF10','Actor':'ENCARGADO','Pantalla real Thymeleaf':'encargado/solicitud-detalle.html','Método/servicio':'SolicitudService.rechazar','Mockup':'G03_05_M02_Revision.html','Estado':'Documentado; no probado'})
        rows.insert(ix+1,r)
    # Leer versiones Word; reemplazar SOLO titulos/index y 3 celdas de requisitos.
    # Acepta tabla ED02 intermedia RF09, RF11 como condicion a corregir.
    doc=Document(str(docpath))
    changes = normalize_doc_titles(doc)
    reqcells=[]
    for table in doc.tables:
        if len(table.columns)<2 or len(table.rows)<2:continue
        for row in table.rows:
            if row.cells[0].text.strip()=='Requisitos':reqcells.append(row.cells[1])
    if len(reqcells)!=3:die('Se esperaban 3 tablas de requisitos en DOCX; hay '+str(len(reqcells)))
    for j,(before,after) in enumerate(TABLE_REQS):
        current=reqcells[j].text.strip()
        if current==after:
            continue
        if current in before:
            para=reqcells[j].paragraphs[0]
            if len(para.runs)==1:para.runs[0].text=after
            else:para.text=after
            changes.append('tabla '+str(j+1)+': '+current+' -> '+after)
        else:
            die(f'Requisito inesperado en tabla {j+1}: {current!r}; admitidos: {before!r} o {after!r}')
    # Visibilidad de otras lineas base.
    sdd_bytes=sd.stat().st_size
    btext=readutf(bitacora)
    need_log='## G03 - Paso 1.12: Conciliacion de trazabilidad interna (25/11/2026)' not in btext
    print(f'G03 1.12 PREVIO OK | rama={branch} | HEAD={head[:12]}')
    print(f'RF10 en matriz general: OK | ED02 MD: {"CORREGIR" if needs_md else "OK"} | CSV: {"AGREGAR" if need_csv else "OK"}')
    print(f'DOCX de casos: {len(changes)} correcciones de rotulos/requisitos | SDD protegido ({sdd_bytes} bytes)')
    if not a.aplicar:
        print('No se ha modificado ningun archivo. Ejecuta con --aplicar para generar versiones alineadas.')
        return 0
    # Respaldo fuera del repo. Si falla una escritura, los originales quedan en esta carpeta.
    backup=Path(tempfile.mkdtemp(prefix='AgroRetorno_G03_12_backup_'))
    for f in [case,csvpath,docpath,bitacora]:
        shutil.copy2(f,backup/f.name)
    backup_doc=sha(docpath)
    if needs_md:writeutf(case,updated_case,True)
    if need_csv:
        buffer=io.StringIO(newline='')
        writer=csv.DictWriter(buffer,fieldnames=fields,delimiter=';',lineterminator='\n')
        writer.writeheader();writer.writerows(rows)
        writeutf(csvpath,buffer.getvalue(),True)
    if changes:
        temp_doc=docpath.with_name(docpath.stem+'.g03_12_tmp.docx')
        try:
            doc.save(str(temp_doc))
            # Verificacion estructural minima previa a reemplazar.
            recheck=Document(str(temp_doc))
            if len(recheck.tables)!=len(doc.tables):die('Word modificado perdio tablas')
            temp_doc.replace(docpath)
        finally:
            if temp_doc.exists():temp_doc.unlink()
    if need_log:writeutf(bitacora,btext.rstrip('\r\n')+'\n'+ENTRY,True)
    report=f'''# G03 — Paso 1.12 | Conciliacion interna de los tres casos detallados\n\n**Rama:** `{branch}`  \n**Commit de base:** `{head}`  \n**Condicion:** revision de trazabilidad contra la linea base G03, sin aprobacion G01.\n\n## Resultado\n\n- Matriz general: RF10 corresponde a **CU-12 / ED-02**.\n- Markdown ED-02: **RF09, RF10, RF11** (aprobar/rechazar/entregar).\n- Matriz detallada ED-02: hay **3 filas explicitas**, una para cada RF09/RF10/RF11.\n- Word de casos de uso: indices y rotulos **ED-01/ED-02/ED-03** y requisitos **RF07 / RF09,RF10,RF11 / RF13**.\n- DOCX de casos: {len(changes)} cambios de etiquetas/requisitos en esta ejecucion. Hash previo `{backup_doc}`; hash actualizado `{sha(docpath)}`.\n- DOCX del SDD `01_SDD/G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx` **no modificado**.\n- Respaldo de los cuatro archivos de trabajo (fuera del repositorio): `{backup}`.\n\n## Alcance de la validacion\n\nSe ha alineado la numeracion **interna** ED/RF en las fuentes citadas. No se ha comparado con una ERS final G01, ya que no consta en el repositorio. Tampoco se ha hecho una revision visual de las paginas Word despues de esta operacion; abrir el DOCX y revisar el indice, los 3 encabezados y las tablas antes de commit.\n\nEl documento Word de SDD y el PDF **no se sustituyen** sin una verificacion de necesidad. Los historiales anteriores en Bitacora.md conservan IDs originales como registro cronologico. No se modificaron datos del G04 ni el codigo Java.\n\n**Estado:** Alineacion interna completada; conformidad formal G01 y revision externa no acreditadas.\n'''
    out.write_text(report,encoding='utf-8')
    print('G03 1.12 OK | matriz ED02=3 RF | Word etiquetas coherentes | respaldo='+str(backup))
    print('Acta: '+str(out))
    return 0

if __name__=='__main__':
    try:sys.exit(main())
    except Exception as exc:
        print('ERROR:',exc,file=sys.stderr)
        sys.exit(1)
