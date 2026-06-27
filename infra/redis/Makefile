include ../.env
include .env
export COMPOSE_IGNORE_ORPHANS

.PHONY: up build down rebuild re
CONTAINER := redis-db

up:
	${COMPOSE} up --wait ${CONTAINER}

build:
	${COMPOSE} build

down:
	${COMPOSE} down ${CONTAINER}

rebuild: down build up

re: down up

# Utils
.PHONY: redis logs clean ps

redis:
	${DOCKER} exec -it ${CONTAINER} redis-cli

logs:
	${DOCKER} logs -f ${CONTAINER}

clean:
	${COMPOSE} down -v

ps:
	${COMPOSE} ps
