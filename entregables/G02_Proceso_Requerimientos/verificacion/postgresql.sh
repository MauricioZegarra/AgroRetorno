#!/usr/bin/env bash
set -euo pipefail

# Usa PostgreSQL y la aplicación en puertos efímeros limitados a localhost.
if [[ $# -ne 1 || -z "$1" ]]; then
  printf 'Uso: bash postgresql.sh DIRECTORIO_DE_RESULTADOS\n' >&2
  exit 2
fi
root=$(git rev-parse --show-toplevel)
image=postgres:16.11
container="agroretorno-verificacion-$$"
restore_container="${container}-restaurado"
work=$(mktemp -d)
output=$(realpath -m -- "$1")
mkdir -p "$output"
app_pid=
container_started=false
restore_started=false

stop_app() {
  if [[ -n "$app_pid" ]]; then
    kill "$app_pid" 2>/dev/null || true
    wait "$app_pid" 2>/dev/null || true
    app_pid=
  fi
}

cleanup() {
  stop_app
  if [[ "$container_started" == true ]]; then
    docker rm -fv "$container" >/dev/null
  fi
  if [[ "$restore_started" == true ]]; then
    docker rm -fv "$restore_container" >/dev/null
  fi
  rm -rf -- "$work"
}
trap cleanup EXIT

wait_db() {
  local name=$1 intent
  for ((intent=0; intent<60; intent++)); do
    if docker exec "$name" pg_isready -h 127.0.0.1 -U ar_lab -d agroretorno >/dev/null 2>&1; then
      return
    fi
    sleep 1
  done
  printf 'PostgreSQL no está disponible.\n' >&2
  return 1
}

start_app() {
  local mode=$1 port intent
  local -a demo=()
  if [[ "$mode" == demo ]]; then
    demo=(DEMO_DATA=true)
  fi
  env -u DEMO_DATA -u SPRING_PROFILES_ACTIVE -u AGRORETORNO_DATOS_DEMO_HABILITADO \
    "${demo[@]}" DB_HOST=127.0.0.1 DB_PORT="$db_port" DB_NAME=agroretorno \
    DB_USER=ar_lab DB_PASSWORD="$db_password" SERVER_PORT=0 SERVER_ADDRESS=127.0.0.1 \
    java -jar "$root/target/agroretorno.jar" >"$work/app.log" 2>&1 &
  app_pid=$!
  for ((intent=0; intent<60; intent++)); do
    port=$(sed -nE 's/.*Tomcat started on port ([0-9]+).*/\1/p' "$work/app.log" | tail -1)
    if [[ -n "$port" ]] && curl -fsS --max-time 2 "http://127.0.0.1:$port/login" >/dev/null 2>&1; then
      if [[ "$mode" != demo ]] || [[ $(sql 'SELECT count(*) FROM usuarios') == 6 ]]; then
        return
      fi
    fi
    if ! kill -0 "$app_pid" 2>/dev/null; then
      break
    fi
    sleep 1
  done
  tail -30 "$work/app.log" >&2
  return 1
}

sql() {
  docker exec "${2:-$container}" psql -X -qAt -v ON_ERROR_STOP=1 -U ar_lab -d agroretorno -F ',' -c "$1"
}

snapshot() {
  local file=$1 name=$2 table
  local -a tables=(usuarios productores materiales inventario solicitudes prestamos devoluciones
                   detalles_devolucion movimientos_inventario historial_operaciones)
  printf 'tabla,filas,huella_md5\n' >"$file"
  for table in "${tables[@]}"; do
    sql "SELECT '$table', count(*), coalesce(md5(string_agg(row_to_json(fila)::text, E'\\n' ORDER BY id)), '-') FROM $table fila" "$name" >>"$file"
  done
}

cd "$root"
"${MAVEN:-./mvnw}" -B -ntp clean verify >"$output/maven.log"
db_password=$(openssl rand -hex 20)
docker run --detach --name "$container" --publish 127.0.0.1::5432 \
  --env POSTGRES_DB=agroretorno --env POSTGRES_USER=ar_lab \
  --env POSTGRES_PASSWORD="$db_password" "$image" >/dev/null
container_started=true
wait_db "$container"
db_port=$(docker port "$container" 5432/tcp)
db_port=${db_port##*:}

start_app normal
snapshot "$output/01-vacio.csv" "$container"
awk -F, 'NR > 1 && $2 != 0 { exit 1 } END { if (NR != 11) exit 1 }' "$output/01-vacio.csv"
stop_app

start_app demo
snapshot "$output/02-demo.csv" "$container"
test "$(sql 'SELECT count(*) FROM usuarios')" = 6
stop_app

docker restart "$container" >/dev/null
wait_db "$container"
db_port=$(docker port "$container" 5432/tcp)
db_port=${db_port##*:}
start_app demo
snapshot "$output/03-reinicio.csv" "$container"
cmp "$output/02-demo.csv" "$output/03-reinicio.csv"
stop_app

docker exec "$container" pg_dump -U ar_lab -Fc agroretorno >"$work/agroretorno.dump"
docker run --detach --name "$restore_container" \
  --env POSTGRES_DB=agroretorno --env POSTGRES_USER=ar_lab \
  --env POSTGRES_PASSWORD="$db_password" "$image" >/dev/null
restore_started=true
wait_db "$restore_container"
docker exec -i "$restore_container" pg_restore -U ar_lab --exit-on-error --no-owner \
  -d agroretorno <"$work/agroretorno.dump"
snapshot "$output/04-restaurado.csv" "$restore_container"
cmp "$output/02-demo.csv" "$output/04-restaurado.csv"

{
  printf 'Fecha UTC: %s\n' "$(date -u +'%FT%TZ')"
  printf 'Revisión Git del código: %s\n' "$(git log -1 --format=%H -- src)"
  printf 'PostgreSQL: %s\n' "$(sql 'SHOW server_version')"
  printf 'Imagen: %s\n' "$(docker image inspect "$image" --format '{{index .RepoDigests 0}}')"
  printf 'Java: %s\n' "$(java -version 2>&1 | sed -n '1p')"
  printf 'Flyway aplicó el esquema. Login HTTP 200 en los tres arranques.\n'
  printf 'Sin DEMO_DATA: 0 filas en 10 tablas de la aplicación.\n'
  printf 'Con DEMO_DATA=true: 6 usuarios sintéticos creados.\n'
  printf 'Tras reiniciar PostgreSQL y la aplicación: las 10 tablas coinciden en conteos y huellas.\n'
  printf 'Tras restaurar en otra instancia: las 10 tablas coinciden en conteos y huellas.\n'
  grep -E 'Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$|All coverage checks|BUILD SUCCESS' "$output/maven.log" | tail -3
} >"$output/resumen.txt"
cat "$output/resumen.txt"
