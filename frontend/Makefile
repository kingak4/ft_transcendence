include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
export BACKEND_URL

.PHONY: up down re build rebuild generate

CONTAINER := transcend-frontend

# Host-reachable address for the dev/eval build's backend. Unlike .env's
# BACKEND_URL (Docker-internal DNS, used by the transcend-frontend
# container's own runtime), this must be resolvable from the host, since
# `generate` below runs on the host before the frontend image is built.
# nginx's port (8443) is a fixed mapping in infra/nginx/docker-compose.yml,
# not per-environment config, so it's hardcoded here rather than sourced
# from a dotenv file.
CODEGEN_URL := https://localhost:8443/api

up: generate
	$(COMPOSE) up --build ${CONTAINER} -d

# nginx uses a self-signed cert in the dev build; NODE_TLS_REJECT_UNAUTHORIZED=0
# is scoped to just these two one-shot codegen calls (plain Node fetch, not
# next dev's internal rewrite proxy, which ignores this variable).
generate:
	$(MAKE) -f Makefile.local install
	@mkdir -p app/types
	@NODE_TLS_REJECT_UNAUTHORIZED=0 ./node_modules/.bin/openapi-typescript $(CODEGEN_URL)/api-docs -o app/types/api.d.ts
	@NODE_TLS_REJECT_UNAUTHORIZED=0 BACKEND_URL=$(CODEGEN_URL) node scripts/generate-asyncapi.mjs

down:
	$(COMPOSE) down ${CONTAINER}

build:
	$(COMPOSE) build 

rebuild: down build up

re: down up

# Utils
.PHONY: clean

clean:
	$(COMPOSE) down -v