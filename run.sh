#!/usr/bin/env bash
# ==============================================================================
# Duralap Modular Monolith — Universal Multi-Mode Startup Script
#
# Supported Modes:
#   1. Local Native (WITHOUT Docker): Runs directly on JVM via Gradle or JAR
#   2. Docker (WITH Docker): Runs containerized via Docker Compose
#
# Supported Platforms:
#   macOS (Apple Silicon M-Series / Intel), Linux, Windows (Git Bash / WSL)
#
# Usage:
#   ./run.sh                  Auto-detects or prompts mode
#   ./run.sh --local, local   Run locally on JVM without Docker
#   ./run.sh --docker, docker Run in Docker Compose
#   ./run.sh --jar, jar       Build & run executable JAR directly
#   ./run.sh --build, build   Compile and build bootJar
#   ./run.sh --test, test     Run test suite
#   ./run.sh --stop, stop     Stop Docker containers and local instances
#   ./run.sh --status, status Check application health status
#   ./run.sh --help, -h       Display help menu
# ==============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -f "${SCRIPT_DIR}/build.gradle.kts" ] && [ -d "${SCRIPT_DIR}/app" ]; then
  BACKEND_DIR="${SCRIPT_DIR}"
elif [ -f "${SCRIPT_DIR}/DuralapBackend/build.gradle.kts" ]; then
  BACKEND_DIR="${SCRIPT_DIR}/DuralapBackend"
else
  echo "Error: DuralapBackend directory not found relative to ${SCRIPT_DIR}" >&2
  exit 1
fi

cd "$BACKEND_DIR"

# UI Colors (disabled if not connected to a terminal)
if [ -t 1 ]; then
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[1;33m'
  BLUE='\033[0;34m'
  MAGENTA='\033[0;35m'
  CYAN='\033[0;36m'
  BOLD='\033[1m'
  NC='\033[0m'
else
  RED=''
  GREEN=''
  YELLOW=''
  BLUE=''
  MAGENTA=''
  CYAN=''
  BOLD=''
  NC=''
fi

info()  { echo -e "${CYAN}${BOLD}[INFO]${NC} $*"; }
ok()    { echo -e "${GREEN}${BOLD}[OK]${NC} $*"; }
warn()  { echo -e "${YELLOW}${BOLD}[WARN]${NC} $*"; }
fail()  { echo -e "${RED}${BOLD}[ERROR]${NC} $*" >&2; exit 1; }

# Detect Local Machine IP
detect_local_ip() {
  local ip=""
  local os
  os="$(uname -s 2>/dev/null || echo unknown)"

  case "$os" in
    Darwin)
      ip="$(ipconfig getifaddr en0 2>/dev/null || true)"
      if [ -z "$ip" ]; then
        ip="$(ipconfig getifaddr en1 2>/dev/null || true)"
      fi
      ;;
    MINGW*|MSYS*|CYGWIN*)
      ip="$(ipconfig 2>/dev/null \
        | grep -Eo 'IPv4[^:]*:[[:space:]]*[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+' \
        | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+' \
        | grep -Ev '^(127\.|169\.254\.)' \
        | head -n 1 || true)"
      ;;
    Linux|*)
      ip="$(hostname -I 2>/dev/null | awk '{print $1}' || true)"
      if [ -z "$ip" ]; then
        ip="$(ip -4 route get 1.1.1.1 2>/dev/null | awk '{for (i=1;i<=NF;i++) if ($i=="src") {print $(i+1); exit}}' || true)"
      fi
      ;;
  esac

  if [ -z "$ip" ]; then
    ip="localhost"
  fi
  echo "$ip"
}

# Load .env file into current environment
load_env() {
  if [ -f ".env" ]; then
    info "Loading environment configuration from .env file..."
    set -a
    # shellcheck source=/dev/null
    source .env
    set +a
    ok "Environment variables loaded successfully."
  elif [ -f "../.env" ]; then
    info "Loading environment configuration from parent .env file..."
    set -a
    # shellcheck source=/dev/null
    source ../.env
    set +a
    ok "Environment variables loaded successfully."
  else
    warn "No .env file found. Using default cloud credentials (MongoDB Atlas & Upstash Redis)."
    warn "Copy .env.example to .env to configure local overrides."
  fi
}

# Check Docker availability
is_docker_available() {
  command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1
}

# Resolve compose command
compose_cmd() {
  if docker compose version >/dev/null 2>&1; then
    echo "docker compose"
  elif command -v docker-compose >/dev/null 2>&1; then
    echo "docker-compose"
  else
    fail "Docker Compose is not available. Please install Docker Desktop or run without Docker using: ./run.sh --local"
  fi
}

# Check Java availability
require_java() {
  if ! command -v java >/dev/null 2>&1; then
    warn "Java not found on PATH. Attempting to use Gradle wrapper bundled JDK..."
  fi
}

# Check if port is already in use
check_port() {
  local port="${PORT:-8080}"
  if command -v lsof >/dev/null 2>&1; then
    if lsof -Pi :"$port" -sTCP:LISTEN -t >/dev/null 2>&1; then
      warn "Port $port is currently in use by another process."
      echo "  To inspect or stop the process on port $port, run: lsof -i :$port"
    fi
  fi
}

# Print Access Info Banner
print_access_banner() {
  local mode="$1"
  local local_ip
  local_ip="$(detect_local_ip)"
  local port="${PORT:-8080}"

  echo ""
  echo "=========================================================================="
  ok "Duralap Modular Monolith Backend is running in [${mode}] mode!"
  echo "=========================================================================="
  echo ""
  echo -e "  ${BOLD}Application Endpoints:${NC}"
  echo -e "    Local Machine:    ${CYAN}http://localhost:${port}${NC}"
  echo -e "    REST API:         ${CYAN}http://localhost:${port}/api${NC}"
  echo -e "    LAN / Wi-Fi:      ${CYAN}http://${local_ip}:${port}${NC}"
  echo -e "    Android Emulator: ${CYAN}http://10.0.2.2:${port}${NC}"
  echo ""
  echo -e "  ${BOLD}Unified WebSocket & STOMP:${NC}"
  echo -e "    Endpoint:         ${CYAN}ws://localhost:${port}/websocket${NC}"
  echo ""
  echo -e "  ${BOLD}Health & Monitoring:${NC}"
  echo -e "    Actuator Health:  ${CYAN}http://localhost:${port}/actuator/health${NC}"
  echo -e "    Actuator Metrics: ${CYAN}http://localhost:${port}/actuator/metrics${NC}"
  echo "=========================================================================="
}

# ------------------------------------------------------------------------------
# Action: Run Locally WITHOUT Docker (via Gradle bootRun)
# ------------------------------------------------------------------------------
run_local() {
  echo "=========================================================================="
  info "Starting Duralap Backend [NATIVE LOCAL MODE — WITHOUT DOCKER]"
  echo "=========================================================================="
  load_env
  require_java
  check_port

  info "Launching Spring Boot via Gradle wrapper (:app:bootRun)..."
  print_access_banner "LOCAL JVM"
  echo ""
  info "Press Ctrl+C at any time to stop the backend."
  echo ""

  ./gradlew :app:bootRun --no-daemon
}

# ------------------------------------------------------------------------------
# Action: Build & Run Pre-built Executable JAR WITHOUT Docker
# ------------------------------------------------------------------------------
run_jar() {
  echo "=========================================================================="
  info "Building and running Executable JAR [WITHOUT DOCKER]"
  echo "=========================================================================="
  load_env
  require_java
  check_port

  info "Compiling and packaging bootJar..."
  ./gradlew :app:bootJar -x test --no-daemon

  local jar_file
  jar_file="$(find app/build/libs -name "*.jar" ! -name "*-plain.jar" | head -n 1)"

  if [ -z "$jar_file" ] || [ ! -f "$jar_file" ]; then
    fail "Application JAR not found in app/build/libs."
  fi

  info "Found executable JAR: $jar_file"
  print_access_banner "STANDALONE JAR"
  echo ""
  info "Starting Java process..."
  java -Xmx1024m -Xms256m -XX:+UseG1GC -jar "$jar_file"
}

# ------------------------------------------------------------------------------
# Action: Run WITH Docker Compose
# ------------------------------------------------------------------------------
run_docker() {
  echo "=========================================================================="
  info "Starting Duralap Backend [DOCKER COMPOSE MODE]"
  echo "=========================================================================="

  if ! is_docker_available; then
    fail "Docker daemon is not running.\nTo run without Docker instead, use:\n  ${YELLOW}./run.sh --local${NC}"
  fi

  COMPOSE="$(compose_cmd)"
  load_env

  info "Cleaning up old / stale containers..."
  $COMPOSE down --remove-orphans 2>/dev/null || true

  info "Building container image and starting Modular Monolith..."
  $COMPOSE up --build -d

  info "Waiting for container initialization..."
  sleep 6

  echo ""
  info "Real-time Container Status:"
  echo "--------------------------------------------------------------------------"
  $COMPOSE ps
  echo "--------------------------------------------------------------------------"

  print_access_banner "DOCKER CONTAINER"
  echo ""
  echo -e "  ${BOLD}Useful Docker Commands:${NC}"
  echo -e "    View Application Logs: ${YELLOW}${COMPOSE} logs -f duralap-app${NC}"
  echo -e "    View All Logs:         ${YELLOW}${COMPOSE} logs -f${NC}"
  echo -e "    Stop Containers:       ${YELLOW}./run.sh stop${NC} (or ${YELLOW}${COMPOSE} down${NC})"
  echo "=========================================================================="
}

# ------------------------------------------------------------------------------
# Action: Build bootJar
# ------------------------------------------------------------------------------
build_project() {
  info "Building Duralap Modular Monolith application..."
  ./gradlew :app:bootJar --no-daemon
  ok "Build completed successfully! Executable JAR is in app/build/libs/"
}

# ------------------------------------------------------------------------------
# Action: Run Tests
# ------------------------------------------------------------------------------
run_tests() {
  info "Running test suite across all modules..."
  ./gradlew check --no-daemon
  ok "All tests passed successfully!"
}

# ------------------------------------------------------------------------------
# Action: Stop all instances
# ------------------------------------------------------------------------------
stop_instances() {
  info "Stopping backend instances..."
  if is_docker_available; then
    COMPOSE="$(compose_cmd)"
    $COMPOSE down --remove-orphans 2>/dev/null || true
    ok "Docker containers stopped."
  fi

  # Stop any Gradle daemons
  ./gradlew --stop >/dev/null 2>&1 || true
  ok "Done."
}

# ------------------------------------------------------------------------------
# Action: Health Status
# ------------------------------------------------------------------------------
check_status() {
  local port="${PORT:-8080}"
  info "Checking backend health at http://localhost:${port}/actuator/health..."
  if command -v curl >/dev/null 2>&1; then
    if curl -fsS "http://localhost:${port}/actuator/health"; then
      echo ""
      ok "Backend is Healthy and UP!"
    else
      echo ""
      warn "Backend is unreachable at http://localhost:${port}/actuator/health"
    fi
  else
    warn "curl is not available to query healthcheck."
  fi
}

# ------------------------------------------------------------------------------
# Help Menu
# ------------------------------------------------------------------------------
show_help() {
  echo "=========================================================================="
  echo -e "${BOLD}Duralap Modular Monolith — Universal Startup Script${NC}"
  echo "=========================================================================="
  echo "Usage: ./run.sh [OPTION]"
  echo ""
  echo "Options:"
  echo "  --local, local       Run natively on JVM via Gradle (WITHOUT Docker)"
  echo "  --docker, docker     Run inside Docker Compose (WITH Docker)"
  echo "  --jar, jar           Build and run standalone executable JAR directly"
  echo "  --build, build       Compile all modules and package executable bootJar"
  echo "  --test, test         Run test suite across all modules"
  echo "  --status, status     Check HTTP actuator health of running backend"
  echo "  --stop, stop         Stop running Docker containers and services"
  echo "  --help, -h           Show this help message"
  echo ""
  echo "Examples:"
  echo "  ./run.sh --local     # Start locally without Docker"
  echo "  ./run.sh --docker    # Start with Docker Compose"
  echo "  ./run.sh             # Smart launcher (auto-detects Docker or falls back to local)"
  echo "=========================================================================="
}

# ------------------------------------------------------------------------------
# Main Dispatcher
# ------------------------------------------------------------------------------
RAW_MODE="${1:-}"

# Normalize input (handle cases like "-- docker" or leading dashes)
if [ "$RAW_MODE" = "--" ] && [ -n "${2:-}" ]; then
  RAW_MODE="$2"
fi

MODE="$(echo "$RAW_MODE" | tr '[:upper:]' '[:lower:]' | sed -e 's/^[ -]*//')"

case "$MODE" in
  local|l)
    run_local
    ;;
  docker|d)
    run_docker
    ;;
  jar|j)
    run_jar
    ;;
  build|b)
    build_project
    ;;
  test|t)
    run_tests
    ;;
  stop)
    stop_instances
    ;;
  status)
    check_status
    ;;
  help|h|\?)
    show_help
    ;;
  "")
    # Smart Auto-Detection:
    # If Docker is available and running, prompt or run Docker;
    # If Docker is NOT running, seamlessly run in Local Non-Docker mode.
    if is_docker_available; then
      if [ -t 0 ] && [ -t 1 ]; then
        echo "=========================================================================="
        echo -e "${BOLD}Duralap Modular Monolith — Select Execution Mode${NC}"
        echo "=========================================================================="
        echo "  1) Run Locally WITHOUT Docker  (Fastest development, uses JVM + Gradle)"
        echo "  2) Run in Docker Compose       (Full containerized stack)"
        echo "  3) Build Executable JAR"
        echo "  4) Run Test Suite"
        echo "  5) Exit"
        echo "=========================================================================="
        read -r -p "Enter choice [1-5] (default: 1): " choice
        case "${choice:-1}" in
          1) run_local ;;
          2) run_docker ;;
          3) run_jar ;;
          4) run_tests ;;
          5) exit 0 ;;
          *) warn "Invalid selection. Starting in Local mode."; run_local ;;
        esac
      else
        info "Non-interactive environment detected. Starting in Local mode (without Docker)..."
        run_local
      fi
    else
      info "Docker is not running or not installed. Automatically starting in Local mode (WITHOUT Docker)..."
      run_local
    fi
    ;;
  *)
    fail "Unknown option: $RAW_MODE\nRun './run.sh --help' for usage instructions."
    ;;
esac
