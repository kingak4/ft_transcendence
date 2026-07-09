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
.PHONY: db logs clean ps tables users_table

db:
	${DOCKER} exec -it ${CONTAINER} psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f ${CONTAINER}

clean:
	${COMPOSE} down -v

ps:
	${COMPOSE} ps

tables:
	${DOCKER} exec -it postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} -c "\dt dev.*"

users_table:
	${DOCKER} exec -i postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} -c "SELECT * FROM dev.users; SELECT * FROM dev.user_details; SELECT * FROM dev.user_friends;" > users_table.txt


