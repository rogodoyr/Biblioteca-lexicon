#!/usr/bin/env bash

# Prueba de integracion para validar el flujo completo por API Gateway.
# Valida:
# 1) /login responde 200 y entrega token.
# 2) Sin token, endpoint protegido responde 401.
# 3) Con token, todos los microservicios responden 200.
# 4) BFF responde 200 con datos consolidados.
# 5) Levanta servicios al inicio y los baja automaticamente al finalizar.

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
  if [[ -x "$STOP_SCRIPT" ]]; then
    info "Bajando servicios..."
    "$STOP_SCRIPT" || true
  fi
}
trap cleanup EXIT

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

  if [[ "$curl_exit_code" -ne 0 ]]; then
    HTTP_CODE="000"
    HTTP_BODY=""
    return 0
  fi

  HTTP_CODE="${response##*$'\n'}"
  HTTP_BODY="${response%$'\n'*}"
}

extraer_token() {
  echo "$1" | sed -n 's/.*"token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p'
}

esperar_endpoint() {
  local max_wait="$1"
  local endpoint_url="$2"
  local token="$3"
  local method="${4:-GET}"
  local body="${5:-}"
  local waited=0

  while (( waited < max_wait )); do
    api_call "$method" "$endpoint_url" "$body" "$token"
    if [[ "$HTTP_CODE" == "200" ]]; then
      return 0
    fi
    info "Esperando $endpoint_url... ${waited}s/${max_wait}s (HTTP $HTTP_CODE)"
    sleep 2
    waited=$((waited + 2))
  done

  return 1
}

PASSED=0
FAILED=0

test_endpoint() {
  local name="$1"
  local method="$2"
  local url="$3"
  local body="${4:-}"

  api_call "$method" "$url" "$body" "$TOKEN"
  if [[ "$HTTP_CODE" == "200" ]]; then
    ok "$name (HTTP $HTTP_CODE)"
    PASSED=$((PASSED + 1))
  else
    fail "$name - Se esperaba 200, llego $HTTP_CODE"
    FAILED=$((FAILED + 1))
  fi
}

echo "============================================"
echo "  LEXICON - Prueba de Integracion"
echo "============================================"
echo

info "Paso 1: levantar servicios"
[[ -x "$START_SCRIPT" ]] || fail "No existe o no es ejecutable: $START_SCRIPT"
"$START_SCRIPT"
ok "Servicios iniciados"

info "Paso 2: esperar disponibilidad del API Gateway"
if ! esperar_endpoint "$WAIT_SECONDS" "$GATEWAY_URL" "" "GET"; then
  fail "Gateway no disponible despues de $WAIT_SECONDS segundos"
fi
ok "Gateway accesible"

login_json="{\"username\":\"$LOGIN_USER\",\"password\":\"$LOGIN_PASS\"}"

info "Paso 3: esperar que /login este operativo"
if ! esperar_endpoint "$WAIT_SECONDS" "$GATEWAY_URL/login" "" "POST" "$login_json"; then
  fail "Login no operativo despues de $WAIT_SECONDS segundos"
fi
ok "/login operativo"

TOKEN="$(extraer_token "$HTTP_BODY")"
[[ -n "$TOKEN" ]] || fail "No se encontro token en la respuesta: $HTTP_BODY"
ok "Token JWT obtenido"

info "Paso 4: verificar que sin token se bloquea (401)"
api_call GET "$GATEWAY_URL/api/v1/customers"
[[ "$HTTP_CODE" == "401" ]] || fail "Se esperaba 401 sin token, llego $HTTP_CODE"
ok "Auth correctamente bloquea sin token"

echo
info "Paso 5: probando todos los microservicios"
echo "--------------------------------------------"

test_endpoint "ms-book      GET /api/v1/books"        GET  "$GATEWAY_URL/api/v1/books"
test_endpoint "ms-loan      GET /api/v1/loans"        GET  "$GATEWAY_URL/api/v1/loans"
test_endpoint "ms-customer  GET /api/v1/customers"    GET  "$GATEWAY_URL/api/v1/customers"
test_endpoint "ms-category  GET /api/v1/categories"   GET  "$GATEWAY_URL/api/v1/categories"
test_endpoint "ms-reservation GET /api/v1/reservations" GET "$GATEWAY_URL/api/v1/reservations"
test_endpoint "ms-notification GET /api/v1/notifications" GET "$GATEWAY_URL/api/v1/notifications"
test_endpoint "ms-penalty   GET /api/v1/penalties"    GET  "$GATEWAY_URL/api/v1/penalties"
test_endpoint "ms-report    GET /api/v1/reports"      GET  "$GATEWAY_URL/api/v1/reports"
test_endpoint "bff          GET /api/v1/bff/loans/1"  GET  "$GATEWAY_URL/api/v1/bff/loans/1"

echo
echo "============================================"
echo "  RESULTADO: $PASSED pruebas pasaron"
echo "============================================"
ok "Todas las pruebas de integracion completadas exitosamente"
ok "El apagado automatico se ejecutara al salir"
