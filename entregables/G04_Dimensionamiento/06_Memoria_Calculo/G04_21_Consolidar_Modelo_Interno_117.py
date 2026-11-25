#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""G04 1.21: acuerdo interno de medicion 117 candidatos; NO modifica maestro/XLSX.

Desde la raiz de AgroRetorno:
  py -3 entregables/G04_Dimensionamiento/06_Memoria_Calculo/G04_21_Consolidar_Modelo_Interno_117.py
Solo requiere Python 3 (stdlib) y Git.
"""
from __future__ import annotations
import csv, hashlib, subprocess
from collections import Counter, defaultdict
from datetime import datetime
from pathlib import Path

BRANCH = 'feature/cielo-g04-dimensionamiento-estimacion'
MASTER_SHA = '2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a'
EXTRA = {'M112':('M058','P14','D01'), 'M113':('M058','P14','D03'),
         'M114':('M058','P14','D05'), 'M115':('M087','P19','D06'),
         'M116':('M101','P21','D05'), 'M117':('M101','P21','D06')}
SPECIAL_RULE = {
    'E_FILTRO_OPCIONAL': 'R21-E1',
    'E_SESION_SIN_PAYLOAD': 'R21-E2',
    'E_VARIANTES_ROL': 'R21-E3',
    'X_POST_REDIRECT': 'R21-X1',
    'X_VARIOS_GRUPOS': 'R21-X2',
}
RULE_DESCRIPTIONS = {
    'R21-E1': 'Una peticion funcional de consulta dispara una Entry, aun con filtros opcionales; NO sumar una E por atributo.',
    'R21-E2': 'Una peticion explicita de consulta propia dispara una Entry; Authentication define alcance y NO crea otra E.',
    'R21-E3': 'P18 comparte objetivo de consulta para dos roles y conserva una Entry en este modelo; diferencias de acceso se anotan.',
    'R21-X1': 'Se atribuye al evento del usuario una salida de resultado de negocio por el flujo POST->redirect->GET; no duplicar su flash.',
    'R21-X2': 'En vistas con objetos de interes distintos se propone un X por grupo reconocible (incluye las seis adiciones).',
    'R21-E0': 'La operacion de negocio recibe datos o identificador de entrada que disparan el proceso.',
    'R21-X0': 'La consulta o reporte devuelve datos funcionales reconocibles como grupo de salida.',
    'R21-RW': 'La lectura/escritura de grupo persistente se conserva segun G04_17, una vez por proceso, tipo y grupo.',
}

def fail(s): raise SystemExit('ERROR G04 1.21: ' + s)

def getrepo(script):
    try:
        repo=Path(subprocess.check_output(['git','-C',str(script.parent),'rev-parse','--show-toplevel'],text=True,stderr=subprocess.DEVNULL).strip())
        branch=subprocess.check_output(['git','-C',str(repo),'branch','--show-current'],text=True).strip()
        head=subprocess.check_output(['git','-C',str(repo),'rev-parse','HEAD'],text=True).strip()
    except (OSError,subprocess.CalledProcessError): fail('No se encuentra repositorio Git. Ejecute en AgroRetorno.')
    if branch!=BRANCH: fail(f'Rama no autorizada: {branch}; esperada {BRANCH}')
    return repo,head

def readcsv(path):
    if not path.is_file(): fail('Falta archivo: '+str(path))
    with path.open('r',encoding='utf-8-sig',newline='') as f:
        rdr=csv.DictReader(f,delimiter=';')
        if rdr.fieldnames is None: fail('CSV sin encabezado: '+str(path))
        rows=list(rdr)
    return rows

def guard_ids(rows,col,start,end,source):
    ids=[r.get(col,'') for r in rows]
    exp={f'M{i:03}' for i in range(start,end+1)}
    if len(ids)!=end-start+1 or len(set(ids))!=len(ids) or set(ids)!=exp:
        fail(f'{source}: IDs incompletos/duplicados: {len(ids)} filas, {len(set(ids))} unicos')

def main():
    repo,head=getrepo(Path(__file__).resolve())
    base=repo/'entregables'/'G04_Dimensionamiento'
    master=base/'02_COSMIC'/'G04_03_Movimientos_COSMIC.csv'
    sha=hashlib.sha256(master.read_bytes()).hexdigest() if master.is_file() else ''
    if sha!=MASTER_SHA: fail(f'Maestro distinto del auditado: SHA256={sha}. No modificar nada.')
    original=readcsv(master)
    guard_ids(original,'Movimiento ID',1,111,'Maestro')
    if any(r.get('Contar')!='Sí' or r.get('Estado')!='Por validar' for r in original):
        fail('El CSV maestro ya tiene otras decisiones: concilie cambios antes de este paso.')
    d=base/'04_Comparaciones'
    scenario=readcsv(d/'G04_20_Escenario_117_COSMIC.csv')
    guard_ids(scenario,'Movimiento ID',1,117,'Escenario 1.20')
    prior=readcsv(d/'G04_19_Matriz_Consolidada_111_COSMIC.csv')
    guard_ids(prior,'ID',1,111,'Matriz 1.19')
    special=readcsv(d/'G04_20_Decisiones_22_COSMIC.csv')
    if len(special)!=22 or len({r['ID'] for r in special})!=22:
        fail('Se esperan las 22 decisiones unicas del paso 1.20.')
    special_by={r['ID']:r for r in special}
    if Counter(r['Categoria'] for r in special)!=Counter({
        'E_FILTRO_OPCIONAL':6,'E_SESION_SIN_PAYLOAD':2,'E_VARIANTES_ROL':1,
        'X_POST_REDIRECT':10,'X_VARIOS_GRUPOS':3}):
        fail('Cambiaron las categorias del paso 1.20; no aplicar reglas a ciegas.')
    before={r['Movimiento ID']:r for r in original}
    by_prior={r['ID']:r for r in prior}
    for row in scenario:
        id=row['Movimiento ID']
        if id in before:
            for field in ('Movimiento ID','Proceso','Nombre del proceso','RF','Actor','Tipo','Grupo de datos','Detalle','Código'):
                if row[field]!=before[id][field]: fail(f'Escenario difiere del maestro en {id}/{field}')
            p=by_prior[id]
            for a,b in [('Proceso','Proceso'),('Tipo','Tipo'),('Grupo','Grupo de datos'),('Movimiento','Detalle')]:
                if p[a]!=row[b]: fail(f'Matriz 1.19 difiere del maestro en {id}/{a}')
        else:
            parent,process,group=EXTRA[id]
            if row['Proceso']!=process or row['Tipo']!='X' or not row['Grupo de datos'].startswith(group):
                fail(f'Adicion {id} no corresponde al grupo esperado {group}')
            if row['Código']!=before[parent]['Código']: fail(f'Código heredado de {id} no coincide con {parent}')
    counts=Counter(r['Tipo'] for r in scenario)
    if counts!=Counter({'E':21,'X':27,'R':41,'W':28}): fail(f'Distribucion inesperada: {counts}')
    if len({(r['Proceso'],r['Tipo'],r['Grupo de datos']) for r in scenario})!=117:
        fail('Se detectaron ternas proceso-tipo-grupo duplicadas; revisar clasificación.')
    out=[]
    for r in scenario:
        rid=r['Movimiento ID']; typ=r['Tipo']
        if rid in EXTRA:
            rule='R21-X2';sustento=r['Soporte / dependencia'];fuente='G04_20: salida adicional, derivada de '+EXTRA[rid][0]
            category='X_NUEVO_GRUPO';baseid=EXTRA[rid][0]
        elif rid in special_by:
            info=special_by[rid];category=info['Categoria']
            rule=SPECIAL_RULE[category];sustento=info['Evidencia código/interfaz']
            fuente='G04_20: '+info['Regla/condición de cierre'];baseid=''
        else:
            p=by_prior[rid];category=p['Categoria']
            rule='R21-RW' if typ in ('R','W') else ('R21-E0' if typ=='E' else 'R21-X0')
            sustento=p['Sustento Java']
            fuente='G04_17' if typ in ('R','W') else 'G04_18';baseid=''
        if not sustento.strip(): fail('Evidencia vacia para '+rid)
        out.append({'ID':rid,'Proceso':r['Proceso'],'RF':r['RF'],'Actor':r['Actor'],
          'Tipo':typ,'Grupo de datos':r['Grupo de datos'],'Movimiento':r['Detalle'],
          'Referencia Java':r['Código'],'Regla G04':rule,'Categoria':category,
          'Criterio aplicado':RULE_DESCRIPTIONS[rule],
          'Sustento de codigo/interfaz':sustento,'Fuente/condicion':fuente,
          'Fila origen':('Adicion del escenario 1.20 desde '+baseid) if baseid else 'Movimiento del maestro',
          'Decision interna G04':'INCLUIR EN MODELO DE TRABAJO G03',
          'Estado de trazabilidad':'REFERENCIA LOCALIZADA (1.12)',
          'Conformidad metodologica externa':'NO ACREDITADA - no equivale a Validado COSMIC'})
    if len(out)!=117: fail('Matriz construida incompleta.')
    out_csv=d/'G04_21_Decisiones_Internas_117_COSMIC.csv'
    out_md=d/'G04_21_Informe_Decisiones_Internas_117.md'
    if out_csv.exists() or out_md.exists(): fail('Ya existen resultados 1.21. Reviselos; no sobrescribir sin inspeccion.')
    try:
        with out_csv.open('w',encoding='utf-8-sig',newline='') as handle:
            writer=csv.DictWriter(handle,delimiter=';',fieldnames=list(out[0]));writer.writeheader();writer.writerows(out)
        outsha=hashlib.sha256(out_csv.read_bytes()).hexdigest()
        groups=defaultdict(Counter)
        for r in out: groups[r['Proceso']][r['Tipo']]+=1
        now=datetime.now().astimezone().isoformat(timespec='seconds')
        by_rules=Counter(r['Regla G04'] for r in out)
        report=f'''# G04 — Paso 1.21 | Modelo interno de medición COSMIC G03 (117 movimientos)

**Generado:** {now}  
**Git HEAD:** `{head}`  
**Rama:** `{BRANCH}`  
**SHA-256 CSV maestro (SIN MODIFICAR):** `{sha}`  
**SHA-256 matriz interna:** `{outsha}`  
**Fuente:** G04_19, G04_20, referencia Java G04_12 y reglas R21 especificadas en G04_21.

## Resolución interna

Se adopta para la **estimación interna de la línea base RF01–RF15 de G03** la opción
que incluye las seis salidas adicionales de P14/P19/P21, trata una Entry funcional
por petición y atribuye la salida de negocio de POST→redirect→GET una vez al evento
que la originó. Se mantienen las R/W documentadas en G04_17.

| Tipo | Movimientos en este modelo interno |
|---|---:|
| E | {counts['E']} |
| X | {counts['X']} |
| R | {counts['R']} |
| W | {counts['W']} |
| **Total de decisión interna** | **{len(out)} CFP** |

**117 = 111 originales + 6 salidas nuevas.** Las 22 categorías especiales tienen
una regla interna explicita en la matriz; hay **0 filas sin decisión interna**.
Esto NO demuestra conformidad con una edición identificada del manual COSMIC y
NO supone aprobación por G01, docente ni auditor externo.

## Reglas aplicadas
'''
        for rule,label in RULE_DESCRIPTIONS.items(): report+=f'- **{rule}** ({by_rules[rule]} filas): {label}\n'
        report+='\n## Totales por proceso (modelo adoptado de trabajo)\n\n| Proceso | E | X | R | W | CFP |\n|---|---:|---:|---:|---:|---:|\n'
        for pr in sorted(groups,key=lambda z:int(z[1:])):
            C=groups[pr];report+=f"| {pr} | {C['E']} | {C['X']} | {C['R']} | {C['W']} | {sum(C.values())} |\n"
        report+='''
## Separación de responsabilidades / no alterar fuente

- **Decisión interna G04:** 117 movimientos incluidos por reglas definidas en la documentación (NO por buscar una cifra deseada).
- **Validación de trazabilidad:** 111 referencias Java del maestro localizadas por el paso 1.12; las seis nuevas heredan controlador y tienen evidencia de vistas.
- **No acreditado:** validación COSMIC frente a edición del manual, ERS G01 aceptada, pruebas en navegador y dictamen externo.
- **Archivos que NO cambia este script:** `02_COSMIC/G04_03_Movimientos_COSMIC.csv` y `06_Memoria_Calculo/G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx`.
- **Siguiente integración:** conciliar el archivo Excel de 37 hojas original de la rama, agregando los nuevos movimientos con formulas y revisando validacion automatica. Mantener respaldo y no sustituir el informe de los 111/117 escenarios.

**Nota metodológica:** la elección de 117 es una decisión **de estimación académica interna** con criterios explicitados. Es distinto a afirmar que el manual COSMIC garantiza 117 CFP. En particular, una solicitud GET sin payload y los resultados en POST→redirect requieren confrontación normativa antes de denominar el total “COSMIC validado formalmente”.
'''
        out_md.write_text(report,encoding='utf-8')
    except Exception:
        out_csv.unlink(missing_ok=True)
        raise
    print('G04 1.21 OK | decisiones internas=117/117 | E=21 X=27 R=41 W=28 | +6 X')
    print('Maestro intacto. Matriz:',out_csv)
    print('Informe:',out_md)
    print('AVISO: modelo interno G03, no certificacion COSMIC ni conformidad G01.')

if __name__=='__main__':main()
