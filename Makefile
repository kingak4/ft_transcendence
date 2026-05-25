include ./infra/.env

.PHONY: up down re infra-up backend-up frontend-up nginx-up

up: infra-up backend-up frontend-up nginx-up

infra-up:
	${COMPOSE} up -d --wait db redis

backend-up:
	${COMPOSE} up -d --wait backend

frontend-up:
	${COMPOSE} up -d frontend

nginx-up:
	${COMPOSE} up -d nginx

down:
	${COMPOSE} down -v

re: down up