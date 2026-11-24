#!/usr/bin/env python3
# G04 - COCOMO: reconteo reproducible de Java productivo (aproximación)
# Uso desde la raíz del repositorio: python entregables/G04_Dimensionamiento/03_COCOMO/G04_04_ContarLOC.py --root .
# No sube ni altera archivos de producción.
import argparse
import csv
import pathlib
import re
from collections import defaultdict

def contar_java(texto):
    fisicas = len(texto.splitlines())
    sin_bloques = re.sub(r"(?s)/\*.*?\*/", "", texto)
    sin_lineas = re.sub(r"(?m)^\s*//.*$", "", sin_bloques)
    sin_inline = re.sub(r"(?m)\s+//.*$", "", sin_lineas)
    aproximadas = sum(bool(linea.strip()) for linea in sin_inline.splitlines())
    return fisicas, aproximadas

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=".", help="raíz local de AgroRetorno")
    parser.add_argument("--output", default=None, help="CSV opcional")
    args = parser.parse_args()
    src = pathlib.Path(args.root) / "src" / "main" / "java"
    if not src.is_dir():
        raise SystemExit(f"No existe la carpeta de producción: {src}")
    grupos = defaultdict(lambda: [0,0,0])
    for archivo in sorted(src.rglob("*.java")):
        try:
            fisicas,aprox = contar_java(archivo.read_text(encoding="utf-8-sig"))
        except UnicodeDecodeError:
            raise SystemExit(f"Codificación incompatible: {archivo}")
        paquete = archivo.parent.name
        grupo = grupos[paquete]
        grupo[0]+=1;grupo[1]+=fisicas;grupo[2]+=aprox
    salida = [["Paquete","Archivos","Físicas","Líneas código aprox."]]
    salida.extend([[paquete,*datos] for paquete,datos in sorted(grupos.items())])
    total = [sum(x[i] for x in grupos.values()) for i in range(3)]
    salida.append(["TOTAL",*total])
    for fila in salida:
        print(" | ".join(map(str,fila)))
    print(f"KLOC código aproximado: {total[2]/1000:.3f}")
    print("ADVERTENCIA: heurística sin parseo Java; excluye HTML/CSS/JS/SQL y pruebas.")
    if args.output:
        dest=pathlib.Path(args.output)
        dest.parent.mkdir(parents=True,exist_ok=True)
        with dest.open("w",encoding="utf-8-sig",newline="") as f:
            csv.writer(f).writerows(salida)
        print(f"CSV guardado en: {dest}")

if __name__ == "__main__":
    main()
