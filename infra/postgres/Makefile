include ../.env
include .env

up:
	${COMPOSE} up -d

down:
	${COMPOSE} down -v

reset: down up

db:
	${DOCKER} exec -it postgres-db-container psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	${DOCKER} logs -f postgres-db-container
