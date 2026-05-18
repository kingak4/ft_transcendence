include ../.env
include .env

.PHONY: up down reset db logs

up:
	${COMPOSE} up

down:
	${COMPOSE} down -v

reset: down up

db:
	${DOCKER} exec -it postgres-db-container psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f postgres-db-container
