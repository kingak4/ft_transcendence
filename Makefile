include ./infra/.env

.PHONY: up down reset infra-up backend-up frontend-up

up: infra-up backend-up frontend-up

infra-up:
	${COMPOSE} up -d --wait db redis

backend-up:
	${COMPOSE} up -d --wait backend

frontend-up:
	${COMPOSE} up -d frontend

down:
	${COMPOSE} down -v

reset: down up