.PHONY: build up down clean generate
include .env
-include .env.local
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS

node_modules:
	npm ci

app/types/api.d.ts: node_modules
	mkdir -p app/types
	./node_modules/.bin/openapi-typescript $(BACKEND_URL)/api-docs -o app/types/api.d.ts

app/types/asyncapi.d.ts: node_modules
	mkdir -p app/types
	BACKEND_URL=$(BACKEND_URL) node scripts/generate-asyncapi.mjs

generate: node_modules
	rm -f app/types/api.d.ts app/types/asyncapi.d.ts
	mkdir -p app/types
	./node_modules/.bin/openapi-typescript $(BACKEND_URL)/api-docs -o app/types/api.d.ts
	BACKEND_URL=$(BACKEND_URL) node scripts/generate-asyncapi.mjs

build: app/types/api.d.ts app/types/asyncapi.d.ts
	${COMPOSE} build

up: app/types/api.d.ts app/types/asyncapi.d.ts
	${COMPOSE} up -d

down:
	${COMPOSE} down

clean:
	${COMPOSE} down -v
	rm -f app/types/api.d.ts app/types/asyncapi.d.ts
	
