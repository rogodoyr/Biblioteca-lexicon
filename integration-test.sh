#!/usr/bin/env bash

# Prueba de integracion simple para validar el flujo completo por API Gateway.
# Valida:
# 1) /login responde 200 y entrega token.
# 2) Sin token, endpoint protegido responde 401.
# 3) Con token, product y store responden 200.
# 4) Levanta servicios al inicio y los baja automaticamente al finalizar.

set -euo pipefail

# Configurable por variables de entorno.
GATEWAY_URL="${GATEWAY_URL:-http://localhost:8080}"
LOGIN_USER="${LOGIN_USER:-suer}"
LOGIN_PASS="${LOGIN_PASS:-1234}"
WAIT_SECONDS="${WAIT_SECONDS:-120}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
START_SCRIPT="$ROOT_DIR/start-services.sh"
STOP_SCRIPT="$ROOT_DIR/stop-services.sh"

# Colores para lectura rapida.
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${YELLOW}[INFO]${NC} $1"; }
ok() { echo -e "${GREEN}[OK]${NC} $1"; }
fail() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

cleanup() {
  # Siempre intentamos bajar servicios al terminar (exito o error).
  if [[ -x "$STOP_SCRIPT" ]]; then
    info "Bajando servicios..."
    "$STOP_SCRIPT" || true
  fi
}
trap cleanup EXIT

# Ejecuta request HTTP y deja resultado en variables globales:
# HTTP_CODE y HTTP_BODY.
api_call() {
  local method="$1"
  local url="$2"
  local body="${3:-}"
  local token="${4:-}"

  local response
  local curl_exit_code=0
  if [[ -n "$token" ]]; then
    if [[ -n "$body" ]]; then
      response="$(curl -sS -X "$method" "$url" -H 'Content-Type: application/json' -H "Authorization: Bearer $token" -d "$body" -w $'\n%{http_code}')" || curl_exit_code=$?
    else
      response="$(curl -sS -X "$method" "$url" -H "Authorization: Bearer $token" -w $'\n%{http_code}')" || curl_exit_code=$?
    fi
  else
    if [[ -n "$body" ]]; then
      response="$(curl -sS -X "$method" "$url" -H 'Content-Type: application/json' -d "$body" -w $'\n%{http_code}')" || curl_exit_code=$?
    else
      response="$(curl -sS -X "$method" "$url" -w $'\n%{http_code}')" || curl_exit_code=$?
    fi
  fi

  # Treat temporary curl/network errors as HTTP 000 so callers can retry.
  if [[ "$curl_exit_code" -ne 0 ]]; then
    HTTP_CODE="000"
    HTTP_BODY=""
    return 0
  fi

  HTTP_CODE="${response##*$'\n'}"
  HTTP_BODY="${response%$'\n'*}"
}

# Extrae token desde JSON de login sin depender de jq.
extraer_token() {
  echo "$1" | sed -n 's/.*"token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p'
}

esperar_gateway() {
  local max_wait="$1"
  local waited=0

  while (( waited < max_wait )); do
    if curl -sS "$GATEWAY_URL" >/dev/null 2>&1; then
      return 0
    fi
    sleep 2
    waited=$((waited + 2))
  done

  return 1
}

esperar_login_operativo() {
  local max_wait="$1"
  local waited=0
  local login_json="$2"

  while (( waited < max_wait )); do
    api_call POST "$GATEWAY_URL/login" "$login_json"
    if [[ "$HTTP_CODE" == "200" ]]; then
      return 0
    fi
    info "Esperando /login... ${waited}s/${max_wait}s (ultimo HTTP $HTTP_CODE)"
    sleep 2
    waited=$((waited + 2))
  done

  return 1
}

esperar_endpoint_protegido() {
  local max_wait="$1"
  local endpoint_url="$2"
  local token="$3"
  local waited=0

  while (( waited < max_wait )); do
    api_call GET "$endpoint_url" "" "$token"
    if [[ "$HTTP_CODE" == "200" ]]; then
      return 0
    fi
    info "Esperando endpoint $endpoint_url... ${waited}s/${max_wait}s (ultimo HTTP $HTTP_CODE)"
    sleep 2
    waited=$((waited + 2))
  done

  return 1
}

info "Iniciando prueba de integracion en $GATEWAY_URL"

info "Paso 1: levantar servicios"
[[ -x "$START_SCRIPT" ]] || fail "No existe o no es ejecutable: $START_SCRIPT"
"$START_SCRIPT"
ok "Servicios iniciados"

info "Paso 2: esperar disponibilidad del API Gateway"
if ! esperar_gateway "$WAIT_SECONDS"; then
  fail "Gateway no disponible despues de $WAIT_SECONDS segundos. Revisa logs en .run/logs"
fi
ok "Gateway accesible"

login_json="{\"username\":\"$LOGIN_USER\",\"password\":\"$LOGIN_PASS\"}"

info "Paso 3: esperar que /login este operativo en el gateway"
if ! esperar_login_operativo "$WAIT_SECONDS" "$login_json"; then
  fail "Se esperaba 200 en /login y no llego dentro de $WAIT_SECONDS segundos. Ultima respuesta: HTTP $HTTP_CODE - $HTTP_BODY"
fi
ok "/login operativo"

TOKEN="$(extraer_token "$HTTP_BODY")"
[[ -n "$TOKEN" ]] || fail "No se encontro token en la respuesta de /login: $HTTP_BODY"
ok "Login correcto y token generado"

info "Paso 4: probar ms-customer sin token (debe ser 401)"
api_call GET "$GATEWAY_URL/api/v1/customers"
[[ "$HTTP_CODE" == "401" ]] || fail "Se esperaba 401 sin token y llego $HTTP_CODE. Respuesta: $HTTP_BODY"
ok "ms-customer sin token se bloquea correctamente"

info "Paso 5: probar ms-customer con token (debe ser 200)"
if ! esperar_endpoint_protegido "$WAIT_SECONDS" "$GATEWAY_URL/api/v1/customers" "$TOKEN"; then
  fail "ms-customer no estuvo listo dentro de $WAIT_SECONDS segundos. Ultima respuesta: HTTP $HTTP_CODE - $HTTP_BODY"
fi
ok "ms-customer responde correctamente con token"

info "Paso 6: probar ms-book con token (debe ser 200)"
if ! esperar_endpoint_protegido "$WAIT_SECONDS" "$GATEWAY_URL/api/v1/books" "$TOKEN"; then
  fail "ms-book no estuvo listo dentro de $WAIT_SECONDS segundos. Ultima respuesta: HTTP $HTTP_CODE - $HTTP_BODY"
fi
ok "ms-book responde correctamente con token"

echo
ok "Integracion OK: login + validacion JWT + acceso a servicios protegidos funcionando"
ok "El apagado automatico se ejecutara al salir"
