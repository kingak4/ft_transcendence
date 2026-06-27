.PHONY: build up down clean generate
include .env
-include .env.local
include ./../infra/.env

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
	${COMPOSE} -f compose.yml build

up: app/types/api.d.ts app/types/asyncapi.d.ts
	${COMPOSE} -f compose.yml up -d

down:
	${COMPOSE} -f compose.yml down

clean:
	${COMPOSE} -f compose.yml down -v
	rm -f app/types/api.d.ts app/types/asyncapi.d.ts
	
