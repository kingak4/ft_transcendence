include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
export BACKEND_URL

.PHONY: up down re build rebuild generate

CONTAINER := transcend-frontend

up: generate
	$(COMPOSE) up ${CONTAINER} -d

# nginx uses a self-signed cert in the dev build; NODE_TLS_REJECT_UNAUTHORIZED=0
# is scoped to just these two one-shot codegen calls (plain Node fetch, not
# next dev's internal rewrite proxy, which ignores this variable).
generate:
	@npm install
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