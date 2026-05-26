include ../.env
include .env

.PHONY: up down re db logs

up:
	${COMPOSE} up -d

down:
	${COMPOSE} down -v

re: down up

db:
	${DOCKER} exec -it postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f postgres-db

clean:
	docker compose down -v

ps:
	${COMPOSE} ps
