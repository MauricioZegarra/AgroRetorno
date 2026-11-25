#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 || -z "$1" ]]; then
  printf 'Uso: bash restaurar.sh RUTA_NUEVA_DEL_REPOSITORIO\n' >&2
  exit 2
fi
destino=$1
if [[ -e "$destino" || -L "$destino" ]]; then
  printf 'La ruta de destino ya existe: %s\n' "$destino" >&2
  exit 2
fi

respaldo=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
archivo=agroretorno-svn-r12.dump.gz
(
  cd -- "$respaldo"
  if command -v sha256sum >/dev/null; then
    sha256sum -c SHA256SUMS
  else
    shasum -a 256 -c SHA256SUMS
  fi
)
svnadmin create "$destino"
gzip -dc -- "$respaldo/$archivo" | svnadmin load -q "$destino"
svnadmin verify -q "$destino"
test "$(svnlook youngest "$destino")" = 12
svnlook tree --full-paths --non-recursive "$destino" tags/v1.0.0
printf 'Restauración verificada hasta r12 en %s\n' "$destino"
