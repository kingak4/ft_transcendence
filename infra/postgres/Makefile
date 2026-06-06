include ../.env
include .env

.PHONY: up down re db logs

up:
	docker compose up -d

down:
	docker compose down

re: down up

db:
	${DOCKER} exec -it postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f postgres-db

clean:
	docker compose down -v

ps:
	${COMPOSE} ps
