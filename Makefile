.PHONY: up down reset
include ./infra/.env

up:
	${COMPOSE} up -d

down:
	${COMPOSE} down -v

reset: down up