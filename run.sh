#!/usr/bin/env bash
# ==============================================================================
# Duralap Microservices — Universal Docker Entry Point
# Supported OS: macOS (Apple Silicon M-Series / Intel), Windows (Git Bash / WSL / PowerShell / CMD), Linux
#
# Usage (macOS / Linux / Windows Git Bash / WSL):
#   ./run.sh
#
# Usage (Windows PowerShell / CMD):
#   bash run.sh
# ==============================================================================

set -euo pipefail

# Determine script location and target backend directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -f "${SCRIPT_DIR}/DuralapBackend/docker-compose.yml" ]; then
  BACKEND_DIR="${SCRIPT_DIR}/DuralapBackend"
elif [ -f "${SCRIPT_DIR}/docker-compose.yml" ]; then
  BACKEND_DIR="${SCRIPT_DIR}"
else
  echo "Error: docker-compose.yml not found in ${SCRIPT_DIR} or ${SCRIPT_DIR}/DuralapBackend" >&2
  exit 1
fi

cd "$BACKEND_DIR"

# UI Colors (disabled if not connected to a terminal)
if [ -t 1 ]; then
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[1;33m'
  CYAN='\033[0;36m'
  BOLD='\033[1m'
  NC='\033[0m'
else
  RED=''
  GREEN=''
  YELLOW=''
  CYAN=''
  BOLD=''
  NC=''
fi

info()  { echo -e "${CYAN}${BOLD}[INFO]${NC} $*"; }
ok()    { echo -e "${GREEN}${BOLD}[OK]${NC} $*"; }
warn()  { echo -e "${YELLOW}${BOLD}[WARN]${NC} $*"; }
fail()  { echo -e "${RED}${BOLD}[ERROR]${NC} $*" >&2; exit 1; }

require_docker() {
  if ! command -v docker >/dev/null 2>&1; then
    fail "Docker CLI is not installed or not available on PATH.\nInstall Docker Desktop from https://www.docker.com/products/docker-desktop/"
  fi
  if ! docker info >/dev/null 2>&1; then
    fail "Docker daemon is not running. Please start Docker Desktop or the Docker service and try again."
  fi
}

compose_cmd() {
  if docker compose version >/dev/null 2>&1; then
    echo "docker compose"
  elif command -v docker-compose >/dev/null 2>&1; then
    echo "docker-compose"
  else
    fail "Docker Compose is not available. Please install Docker Desktop (includes Compose V2)."
  fi
}

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
      # Git Bash / MSYS / Cygwin on Windows
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

echo "=========================================================================="
info "Duralap Microservices Backend — Universal Docker Runner"
echo "=========================================================================="

require_docker
COMPOSE="$(compose_cmd)"

if [ -f ".env" ]; then
  info "Loading environment settings from .env file"
else
  warn "No .env file found. Using default values from docker-compose.yml"
  warn "Copy .env.example to .env if you wish to override MongoDB, JWT, or port defaults."
fi

info "Cleaning up old / stale containers and network state..."
$COMPOSE down --remove-orphans 2>/dev/null || true

info "Building container images and starting infrastructure + 10 microservices..."
$COMPOSE up --build -d

info "Waiting 12 seconds for containers and healthchecks to initialize..."
sleep 12

echo ""
info "Real-time Container Status:"
echo "--------------------------------------------------------------------------"
$COMPOSE ps
echo "--------------------------------------------------------------------------"

LOCAL_IP="$(detect_local_ip)"

echo ""
echo "=========================================================================="
ok "Duralap Microservices Backend successfully launched in Docker!"
echo "=========================================================================="
echo ""
echo -e "  ${BOLD}API Gateway Access:${NC}"
echo -e "    Local Machine:    ${CYAN}http://localhost:8080${NC}"
echo -e "    LAN / Wi-Fi Network: ${CYAN}http://${LOCAL_IP}:8080${NC}"
echo -e "    Android Emulator: ${CYAN}http://10.0.2.2:8080${NC}"
echo ""
echo -e "  ${BOLD}Health Check Endpoint:${NC}"
echo -e "    ${CYAN}http://localhost:8080/actuator/health${NC}"
echo ""
echo -e "  ${BOLD}Useful Docker Commands:${NC}"
echo -e "    View Gateway Logs: ${YELLOW}${COMPOSE} logs -f gateway-service${NC}"
echo -e "    View All Logs:     ${YELLOW}${COMPOSE} logs -f${NC}"
echo -e "    Check Status:      ${YELLOW}${COMPOSE} ps${NC}"
echo -e "    Stop Containers:   ${YELLOW}${COMPOSE} down${NC}"
echo "=========================================================================="
