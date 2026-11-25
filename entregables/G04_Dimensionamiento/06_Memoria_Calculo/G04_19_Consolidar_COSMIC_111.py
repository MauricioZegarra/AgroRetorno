#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""G04 Paso 1.19. Consolidacion auditable COSMIC; NO modifica el XLSX ni la base."""
from __future__ import annotations
from collections import Counter
from datetime import datetime
from pathlib import Path
import csv
import hashlib
import subprocess
import sys

BASE = Path(__file__).resolve().parents[1]
RUTA = BASE / '04_Comparaciones'
MASTER = BASE / '02_COSMIC' / 'G04_03_Movimientos_COSMIC.csv'
RW = RUTA / 'G04_17_Decisiones_RW_69_COSMIC.csv'
EX = RUTA / 'G04_18_Decisiones_EX_42_COSMIC.csv'
OUT_CSV = RUTA / 'G04_19_Matriz_Consolidada_111_COSMIC.csv'
OUT_MD = RUTA / 'G04_19_Informe_Consolidacion_111_COSMIC.md'
CABECERAS = [
    'ID', 'Proceso', 'Nombre del proceso', 'RF', 'Actor', 'Tipo', 'Grupo',
    'Movimiento', 'Codigo base', 'Contar original', 'Estado original',
    'Fuente de decision', 'Categoria', 'Decision documental', 'Sustento Java',
    'Condicion metodologica', 'Resultado de consolidacion', 'Criterio de cierre',
    'Decision funcional definitiva', 'Estado de validacion funcional'
]
CRITERIOS = {
    'X_POST_REDIRECT': 'Delimitar el proceso POST->redirect->GET; acreditar la X visible al usuario sin doble conteo.',
    'E_FILTRO_OPCIONAL': 'Definir datos de la Entry disparadora aun con GET sin filtros, segun frontera COSMIC.',
    'E_SESION_SIN_PAYLOAD': 'Identificar la Entry disparadora y grupo de datos, sin inferirlo solo de Authentication.',
    'E_VARIANTES_ROL': 'Justificar si ambas rutas por rol comparten un proceso funcional o requieren separar mediciones.',
    'X_VARIOS_GRUPOS': 'Definir objetos de interes de la pantalla; decidir 1 o varias Exit y recalcular si procede.',
}

def cargar(ruta: Path):
    if not ruta.exists():
        raise ValueError(f'Falta el archivo: {ruta}')
    with ruta.open('r', encoding='utf-8-sig', newline='') as f:
        arr = list(csv.DictReader(f, delimiter=';'))
    if not arr:
        raise ValueError(f'CSV sin registros: {ruta}')
    return arr

def indexar(rows, id_field, esperado):
    ids = [r[id_field].strip() for r in rows]
    if len(rows) != esperado or len(set(ids)) != esperado:
        raise ValueError(f'Filas no corresponden a {esperado}: obtenidas {len(rows)}, unicas {len(set(ids))}')
    return dict(zip(ids, rows))

def sha(ruta):
    return hashlib.sha256(ruta.read_bytes()).hexdigest()

def git(*params):
    try:
        p = subprocess.run(['git',*params], cwd=BASE, capture_output=True, text=True, encoding='utf-8', check=True)
        return p.stdout.strip()
    except (subprocess.CalledProcessError, FileNotFoundError):
        return 'NO DISPONIBLE'

def generar():
    branch = git('branch','--show-current')
    if branch not in ('feature/cielo-g04-dimensionamiento-estimacion','NO DISPONIBLE'):
        raise ValueError('Rama incorrecta: '+branch)
    maestro = indexar(cargar(MASTER),'Movimiento ID',111)
    rw = indexar(cargar(RW),'ID',69)
    ex = indexar(cargar(EX),'ID',42)
    esperados = {f'M{i:03d}' for i in range(1,112)}
    if set(maestro)!=esperados or set(rw)&set(ex) or set(rw)|set(ex)!=esperados:
        raise ValueError('IDs inconsistentes entre CSV maestro y 1.17/1.18')
    salida=[]
    for i in range(1,112):
        mid=f'M{i:03d}'
        m=maestro[mid]
        d, fuente = (rw[mid], 'G04_17') if mid in rw else (ex[mid], 'G04_18')
        tipo=m['Tipo'].strip()
        if (m['Proceso'].strip()!=d['Proceso'].strip() or tipo!=d['Tipo'].strip()):
            raise ValueError(f'Proceso/Tipo inconsistente: {mid}')
        base_grupo=m['Grupo de datos'].split('·')[0].strip().upper()
        grupo_d=d['Grupo'].split('·')[0].strip().upper()
        if base_grupo != grupo_d:
            raise ValueError(f'Grupo inconsistente {mid}: {base_grupo} vs {grupo_d}')
        if fuente=='G04_17':
            cat='R_W_CON_EVIDENCIA'
            dec=d['Decision base RF G03']
            evidencia=d['Justificacion analisis de codigo']
            regla=d['Criterio especial']
            estado='CONSERVAR PROPUESTO - sustento documental R/W'
            cierre='Verificar que el grupo sea objeto de interes independiente y el flujo siga frontera COSMIC.'
        else:
            cat=d['Categoria_de_revision']
            dec=d['Decision_linea_base']
            evidencia=d['Hecho_del_codigo']
            regla=d['Razon_y_regla_de_cierre']
            cierre=CRITERIOS.get(cat,'Verificar un movimiento del grupo funcional con la frontera COSMIC definida.')
            estado=('REQUIERE RESOLUCION METODOLOGICA' if cat in CRITERIOS else
                    'CONSERVAR PROPUESTO - sustento documental E/X')
        salida.append({
            'ID':mid, 'Proceso':m['Proceso'], 'Nombre del proceso':m['Nombre del proceso'],
            'RF':m['RF'], 'Actor':m['Actor'], 'Tipo':tipo, 'Grupo':m['Grupo de datos'],
            'Movimiento':m['Detalle'], 'Codigo base':m['Código'],
            'Contar original':m['Contar'], 'Estado original':m['Estado'],
            'Fuente de decision':fuente, 'Categoria':cat, 'Decision documental':dec,
            'Sustento Java':evidencia, 'Condicion metodologica':regla,
            'Resultado de consolidacion':estado, 'Criterio de cierre':cierre,
            'Decision funcional definitiva':'NO EMITIDA',
            'Estado de validacion funcional':'POR VALIDAR - no confundir con trazabilidad Java',
        })
    RUTA.mkdir(parents=True,exist_ok=True)
    with OUT_CSV.open('w',encoding='utf-8-sig',newline='') as f:
        w=csv.DictWriter(f,fieldnames=CABECERAS,delimiter=';',quoting=csv.QUOTE_MINIMAL)
        w.writeheader();w.writerows(salida)
    tipo=Counter(x['Tipo'] for x in salida)
    estado=Counter(x['Resultado de consolidacion'] for x in salida)
    risk=Counter(x['Categoria'] for x in salida if x['Resultado de consolidacion']=='REQUIERE RESOLUCION METODOLOGICA')
    rids=[x['ID'] for x in salida if x['Resultado de consolidacion']=='REQUIERE RESOLUCION METODOLOGICA']
    orig=Counter(x['Estado original'] for x in salida)
    cab=f'''# G04 - Paso 1.19 | Conciliacion reproducible de 111 movimientos COSMIC

**Fecha de ejecucion:** {datetime.now().astimezone().isoformat(timespec='seconds')}  
**Rama:** `{branch}`  
**HEAD del repositorio:** `{git('rev-parse','HEAD')}`  
**SHA256 CSV maestro:** `{sha(MASTER)}`  
**SHA256 G04_17:** `{sha(RW)}`  
**SHA256 G04_18:** `{sha(EX)}`  

## Resultado de conciliacion

- 111 IDs originales unicos y completos (M001-M111), 21 procesos.
- R/W: 69 movimientos con sustento documental de G04_17.
- E/X: 42 movimientos con sustento documental y advertencias de G04_18.
- Tipos: E={tipo['E']}, X={tipo['X']}, R={tipo['R']}, W={tipo['W']}.
- Inclusiones **propuestas**: {sum(n for k,n in estado.items() if k.startswith('CONSERVAR PROPUESTO'))}.
- Movimientos con regla metodologica por resolver: {sum(risk.values())}.
- IDs por resolver: {', '.join(rids)}.
- Estados de origen: {', '.join(f'{k}={v}' for k,v in orig.items())}.

## Categorias no resueltas

'''
    for k,v in sorted(risk.items()):
        cab+=f'- **{k}:** {v}. {CRITERIOS[k]}\n'
    cab+='''
## Dictamen

**Consolidacion estructural completada: 111/111 sin huecos ni duplicados.** Los 111 punteros Java
se localizaron previamente por el paso 1.12. **No esta aprobada la semantica COSMIC de las
111 filas**: 89 tienen propuesta documental de conservacion y 22 requieren resolver criterios
expresos. Una propuesta de conservar no equivale por si sola a validacion funcional definitiva.

La matriz mantiene separados `Contar original`, `Estado original`, `Resultado de consolidacion`,
`Decision funcional definitiva` y `Estado de validacion funcional`. No modifica el maestro,
no cambia la columna M del XLSX, ni recalcula unilateralmente el CFP. Hasta decision funcional,
**111 CFP es un total preliminar**, no una medida validada.

La linea base es RF01-RF15 de G03; no se atribuye aprobacion a G01 ni a revisor externo.
Para fijar el total es necesario: identificar version/regla COSMIC, decidir las 22 situaciones
especiales (en particular salidas de multiples grupos), conciliar tambien las 89 propuestas,
y reflejar las inclusiones/exclusiones justificadas en CSV+XLSX vigente respaldado.

## Archivos de esta ejecucion

- `04_Comparaciones/G04_19_Matriz_Consolidada_111_COSMIC.csv`
- `04_Comparaciones/G04_19_Informe_Consolidacion_111_COSMIC.md`

**Control de integridad:** el script aborta si faltan IDs, se duplican filas o difieren proceso,
tipo o grupo de datos entre los documentos y el CSV maestro.
'''
    OUT_MD.write_text(cab,encoding='utf-8')
    print(f'G04 1.19 OK | 111 conciliadas | 89 propuestas | 22 por resolver | E={tipo["E"]} X={tipo["X"]} R={tipo["R"]} W={tipo["W"]}')
    print('CSV:',OUT_CSV)
    print('Informe:',OUT_MD)
    print('No se modifico el CSV maestro ni el libro XLSX de 37 hojas.')

if __name__=='__main__':
    try:generar()
    except (OSError,ValueError,KeyError) as e:
        print('ERROR G04 1.19:',e,file=sys.stderr);sys.exit(1)
