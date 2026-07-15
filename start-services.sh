#!/usr/bin/env bash

# Starts all microservices in background using each module's Gradle wrapper.
# Services started (in order): eureka, auth, all ms, bff, apigateway.
# PIDs are stored in .run/pids and logs in .run/logs.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Load .env file if it exists (for GlitchTip DSN and other secrets)
if [[ -f "$ROOT_DIR/.env" ]]; then
  echo "[INFO] Loading environment variables from .env"
  set -a
  source "$ROOT_DIR/.env"
  set +a
fi
RUN_DIR="$ROOT_DIR/.run"
PID_DIR="$RUN_DIR/pids"
LOG_DIR="$RUN_DIR/logs"

mkdir -p "$PID_DIR" "$LOG_DIR"

modules=("eureka" "auth" "ms-book" "ms-loan" "ms-customer" "ms-category" "ms-reservation" "ms-notification" "ms-penalty" "ms-report" "bff" "apigateway")

is_running() {
  local pid="$1"
  kill -0 "$pid" 2>/dev/null
}

start_module() {
  local module="$1"
  local module_dir="$ROOT_DIR/$module"
  local pid_file="$PID_DIR/$module.pid"
  local log_file="$LOG_DIR/$module.log"

  if [[ ! -d "$module_dir" ]]; then
    echo "[ERROR] Module directory not found: $module_dir"
    return 1
  fi

  # If PID file exists and process is alive, skip to avoid duplicate instance.
  if [[ -f "$pid_file" ]]; then
    local existing_pid
    existing_pid="$(cat "$pid_file")"
    if [[ -n "$existing_pid" ]] && is_running "$existing_pid"; then
      echo "[INFO] $module already running (PID $existing_pid)"
      return 0
    fi
    rm -f "$pid_file"
  fi

  echo "[INFO] Starting $module..."
  (
    cd "$module_dir"
    nohup ./gradlew bootRun >"$log_file" 2>&1 &
    echo $! >"$pid_file"
  )

  local new_pid
  new_pid="$(cat "$pid_file")"

  # Spring Boot puede separar la JVM del proceso de Gradle durante el arranque.
  # Por ello, que el PID del lanzador termine no significa que el servicio falló.
  echo "[OK] $module launch command started (PID $new_pid)"
  echo "      log: $log_file"
}

echo "[INFO] Root directory: $ROOT_DIR"
for module in "${modules[@]}"; do
  start_module "$module"
done

echo ""
echo "[OK] All start commands executed."
echo "[INFO] To stop all services: ./stop-services.sh"
