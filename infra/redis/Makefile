include ../.env
include .env
export COMPOSE_IGNORE_ORPHANS

.PHONY: up build down rebuild re run
CONTAINER := redis-db

up: run

run:
	${COMPOSE} up --wait ${CONTAINER}

build:
	${COMPOSE} build

down:
	${COMPOSE} down ${CONTAINER}

rebuild: down build up

re: down up

# Utils
.PHONY: redis logs fclean ps

redis:
	${DOCKER} exec -it ${CONTAINER} redis-cli

logs:
	${DOCKER} logs -f ${CONTAINER}

fclean:
	${COMPOSE} down -v ${CONTAINER}

ps:
	${COMPOSE} ps
