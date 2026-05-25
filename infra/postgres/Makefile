include ../.env
include .env

.PHONY: up down reset db logs

up:
	${COMPOSE} up -d

down:
	${COMPOSE} down -v

reset: down up

db:
	${DOCKER} exec -it postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f postgres-db

clean:
	docker compose down -v
