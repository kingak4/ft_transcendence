include ./infra/.env

.PHONY: up down re build rebuild infra-up backend-up frontend-up nginx-up infra-build backend-build frontend-build nginx-build env

env:
	find . -name ".env.example" -type f | while read file; do \
		dir=$$(dirname $$file); \
		cp $$file $$dir/.env; \
		echo "Created $$dir/.env from $$file"; \
	done

up: infra-up backend-up frontend-up nginx-up

build: infra-build backend-build frontend-build nginx-build

rebuild: down build up

infra-up:
	${COMPOSE} up -d --wait db redis

infra-build:
	${COMPOSE} up --build -d --wait db redis

backend-up:
	${COMPOSE} up -d --wait backend

backend-build:
	${COMPOSE} up --build -d --wait backend

frontend-up:
	${COMPOSE} up -d frontend

frontend-build:
	${COMPOSE} up --build -d frontend

nginx-up:
	${COMPOSE} up -d nginx

nginx-build:
	${COMPOSE} up --build -d nginx

down:
	${COMPOSE} down -v

re: down up
