#!/usr/bin/env bash
# Verification script for dev (make) and local (make frontend-local) builds.
# Run from the repo root: bash /path/to/verify.sh
# Pauses between parts so you can do the manual browser checks.

set -uo pipefail
cd "$(dirname "$0")" >/dev/null 2>&1 || true
REPO_ROOT="/root/ft_transcendence"
cd "$REPO_ROOT" || { echo "Could not cd to $REPO_ROOT"; exit 1; }

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

pass() { echo -e "  ${GREEN}PASS${NC}: $1"; }
fail() { echo -e "  ${RED}FAIL${NC}: $1"; }
info() { echo -e "${YELLOW}==>${NC} $1"; }

# Checks that a URL returns a specific HTTP status code.
check_http() {
  local url="$1" expected="$2" label="$3"
  local code
  code=$(curl -k -s -o /dev/null -w "%{http_code}" --connect-timeout 3 "$url")
  if [ "$code" = "$expected" ]; then
    pass "$label ($url -> $code)"
  else
    fail "$label ($url -> got $code, expected $expected)"
  fi
}

# Checks that a URL is NOT reachable (connection refused/timeout expected).
check_closed() {
  local url="$1" label="$2"
  local code
  code=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 2 "$url" 2>/dev/null)
  if [ -z "$code" ] || [ "$code" = "000" ]; then
    pass "$label ($url unreachable, as expected)"
  else
    fail "$label ($url -> got $code, expected unreachable)"
  fi
}

pause() {
  read -r -p "$(echo -e "${YELLOW}--> $1 [press Enter to continue]${NC}")" _
}

clean_slate() {
  info "Clean slate: stopping containers, removing network, killing next dev"
  docker compose -p transcend down
  docker network rm transcend-net 2>/dev/null
  pkill -f "next dev" 2>/dev/null
  sleep 1
}

### Part A ###
info "PART A — Clean slate"
clean_slate

### Part B ###
info "PART B — Dev build (make)"
make
echo
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo

info "Waiting for https://localhost:8443/ (frontend via nginx) to come up (up to 90s)..."
for i in $(seq 1 90); do
  code=$(curl -k -s -o /dev/null -w "%{http_code}" --connect-timeout 1 https://localhost:8443/ 2>/dev/null)
  if [ "$code" = "200" ]; then
    break
  fi
  sleep 1
done

info "Checking dev build is reachable only through nginx/HTTPS"
check_http  "https://localhost:8443/"               200 "Homepage via nginx"
check_http  "https://localhost:8443/api/api-docs"    200 "OpenAPI doc via nginx (prefix-stripping)"
check_http  "https://localhost:8443/api/ws/info"     200 "STOMP info via nginx"
check_closed "http://localhost:5000/"                "Backend NOT reachable directly on :5000"
check_closed "http://localhost:5001/"                "Backend NOT reachable directly on :5001"

pause "Now do the browser check: open https://localhost:8443, accept the cert warning, log in/out/in as a different user, watch DevTools console for '[STOMP] Connected successfully' with no reconnect loop."

### Part C ###
info "PART C — Local build (make frontend-local)"
clean_slate
info "Starting 'make frontend-local' in the background (next dev is a long-running process); logging to /tmp/frontend-local.log"
nohup make frontend-local > /tmp/frontend-local.log 2>&1 &
LOCAL_PID=$!
echo "Background PID: $LOCAL_PID (log: /tmp/frontend-local.log)"

info "Waiting for http://localhost:3000 to come up (up to 90s)..."
for i in $(seq 1 90); do
  code=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 1 http://localhost:3000 2>/dev/null)
  if [ "$code" = "200" ]; then
    break
  fi
  sleep 1
done

echo
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo
info "Checking local build's loopback backend + next dev"
check_http "http://localhost:5001/actuator/health" 200 "Backend reachable on loopback :5001"
check_http "http://localhost:3000/"                200 "next dev serving on :3000"

info "Tailing last 40 lines of codegen/next-dev output (check for TLS/cert errors):"
tail -n 40 /tmp/frontend-local.log

pause "Now do the browser check: open http://localhost:3000 directly, log in/out, DevTools console for '[STOMP] Connected successfully', no reconnect loop, no cert warnings."

info "Stopping local build (killing next dev, PID $LOCAL_PID)"
kill "$LOCAL_PID" 2>/dev/null
pkill -f "next dev" 2>/dev/null

### Part D ###
info "PART D — Cross-check: local's port didn't leak into dev"
clean_slate
make
echo
check_closed "http://localhost:5001/" "Backend NOT reachable on :5001 in plain dev build (no docker-compose.local.yml)"

clean_slate
info "Done. Review any FAIL lines above."
