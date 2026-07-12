#!/usr/bin/env bash

# Stops all microservices started by start-services.sh using tracked PID files.
# It first tries graceful stop (SIGTERM) and then force stop (SIGKILL) if needed.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="$ROOT_DIR/.run"
PID_DIR="$RUN_DIR/pids"

modules=("apigateway" "bff" "ms-report" "ms-penalty" "ms-notification" "ms-reservation" "ms-category" "ms-customer" "ms-loan" "ms-book" "auth" "eureka")

is_running() {
  local pid="$1"
  kill -0 "$pid" 2>/dev/null
}

stop_module() {
  local module="$1"
  local pid_file="$PID_DIR/$module.pid"

  if [[ ! -f "$pid_file" ]]; then
    echo "[INFO] No PID file for $module (already stopped or not started by script)"
    return 0
  fi

  local pid
  pid="$(cat "$pid_file")"

  if [[ -z "$pid" ]]; then
    echo "[WARN] Empty PID file for $module, removing it"
    rm -f "$pid_file"
    return 0
  fi

  if ! is_running "$pid"; then
    echo "[INFO] $module PID $pid is not running, cleaning stale PID file"
    rm -f "$pid_file"
    return 0
  fi

  echo "[INFO] Stopping $module (PID $pid) with SIGTERM..."
  kill "$pid" 2>/dev/null || true

  # Wait up to ~10s for graceful shutdown.
  for _ in {1..10}; do
    if ! is_running "$pid"; then
      break
    fi
    sleep 1
  done

  if is_running "$pid"; then
    echo "[WARN] $module still running, forcing SIGKILL on PID $pid"
    kill -9 "$pid" 2>/dev/null || true
  fi

  if is_running "$pid"; then
    echo "[ERROR] Could not stop $module (PID $pid)"
    return 1
  fi

  rm -f "$pid_file"
  echo "[OK] $module stopped"
}

if [[ ! -d "$PID_DIR" ]]; then
  echo "[INFO] PID directory not found: $PID_DIR"
  echo "[INFO] Nothing to stop."
  exit 0
fi

echo "[INFO] Root directory: $ROOT_DIR"
for module in "${modules[@]}"; do
  stop_module "$module"
done

echo ""
echo "[OK] Stop routine finished."
