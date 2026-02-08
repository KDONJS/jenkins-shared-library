#!/bin/bash
set -e

if [ "$#" -ne 6 ]; then
  echo "Uso:"
  echo "$0 SERVICE_NAME IMAGE_TAG ENV_FILE SERVICE_DNS TRAEFIK_ENTRYPOINT SERVICE_PORT"
  exit 1
fi

SERVICE_NAME=$1
IMAGE_TAG=$2
ENV_FILE=$3
SERVICE_DNS=$4
TRAEFIK_ENTRYPOINT=$5
SERVICE_PORT=$6

TEMPLATE="docker-compose.template.yml"
OUTPUT="docker-compose.yml"

cp "$TEMPLATE" "$OUTPUT"

safe_replace() {
  local var_name="$1"
  local file="$2"

  if [[ -v "$var_name" && -n "${!var_name}" ]]; then
    local value="${!var_name}"
    sed -i "s|\${$var_name}|$value|g" "$file"
    echo "✔ Reemplazado \${$var_name} -> $value"
  else
    echo "⏭  \${$var_name} no existe, se deja intacto"
  fi
}

safe_replace SERVICE_NAME "$OUTPUT"
safe_replace IMAGE_TAG "$OUTPUT"
safe_replace ENV_FILE "$OUTPUT"
safe_replace SERVICE_DNS "$OUTPUT"
safe_replace TRAEFIK_ENTRYPOINT "$OUTPUT"
safe_replace SERVICE_PORT "$OUTPUT"

echo "Renderizado OK:"
cat "$OUTPUT"