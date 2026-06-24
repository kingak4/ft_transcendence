include ../.env
include .env

.PHONY: up down re db logs users_table tables

up:
	${COMPOSE} up -d

down:
	${COMPOSE} down

re: down up

db:
	${DOCKER} exec -it postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f postgres-db

clean:
	${COMPOSE} down -v

ps:
	${COMPOSE} ps

tables:
	${DOCKER} exec -it postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} -c "\dt dev.*"

users_table:
	${DOCKER} exec -i postgres-db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} -c "SELECT * FROM dev.users;" > users_table.txt


