include ../.env
include .env
export COMPOSE_IGNORE_ORPHANS
.PHONY: up build down rebuild re

CONTAINER := postgres-db

up:
	${COMPOSE} up --wait ${CONTAINER}

build:
	${COMPOSE} build

down:
	${COMPOSE} down ${CONTAINER}

rebuild: down build up

re: down up

# Utils
.PHONY: db logs clean ps

db:
	${DOCKER} exec -it ${CONTAINER} psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f ${CONTAINER}

clean:
	${COMPOSE} down -v

ps:
	${COMPOSE} ps
