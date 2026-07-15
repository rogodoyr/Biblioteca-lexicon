#!/usr/bin/env bash

# Starts all microservices in background using each module's Gradle wrapper.
# Services started (in order): eureka, auth, product, store, apigateway.
# PIDs are stored in .run/pids and logs in .run/logs.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="$ROOT_DIR/.run"
PID_DIR="$RUN_DIR/pids"
LOG_DIR="$RUN_DIR/logs"

mkdir -p "$PID_DIR" "$LOG_DIR"

modules=("eureka" "auth" "ms-book" "ms-loan" "ms-customer" "bff" "apigateway")

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

  # Give the JVM a brief time to initialize and fail fast if process exits.
  sleep 2
  if is_running "$new_pid"; then
    echo "[OK] $module started (PID $new_pid)"
    echo "      log: $log_file"
  else
    echo "[ERROR] $module failed to stay running. Check log: $log_file"
    return 1
  fi
}

echo "[INFO] Root directory: $ROOT_DIR"
for module in "${modules[@]}"; do
  start_module "$module"
done

echo ""
echo "[OK] All start commands executed."
echo "[INFO] To stop all services: ./stop-services.sh"
