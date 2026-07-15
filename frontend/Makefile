include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
export BACKEND_URL

.PHONY: up down re build rebuild generate

CONTAINER := transcend-frontend

BUILD_STAMP := .build.stamp
# app/types is excluded: `generate` rewrites it on every `up` regardless of
# content, which would make the stamp look permanently stale. See project
# notes on the idempotent-write alternative if this ever needs revisiting.
BUILD_SOURCES := $(shell find . -type f \
	-not -path './node_modules/*' \
	-not -path './.next/*' \
	-not -path './app/types/*' \
	-not -name '$(BUILD_STAMP)')

up: generate $(BUILD_STAMP)
	$(COMPOSE) up ${CONTAINER} -d

$(BUILD_STAMP): $(BUILD_SOURCES)
	$(COMPOSE) build ${CONTAINER}
	@touch $(BUILD_STAMP)

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
	@touch $(BUILD_STAMP)

rebuild: down build up

re: down up

# Utils
.PHONY: clean

clean:
	$(COMPOSE) down -v