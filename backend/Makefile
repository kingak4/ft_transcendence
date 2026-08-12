include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
.PHONY: up build down rebuild re

CONTAINER := transcend-backend

up:
	${COMPOSE} up --wait ${CONTAINER} -d

build:
	${COMPOSE} build

down:
	${COMPOSE} down ${CONTAINER}

rebuild: down build up

logs:
	docker compose -p transcend -f docker-compose.yml logs transcend-backend

# Utils
.PHONY: clean test

clean:
	${COMPOSE} down -v

test:
	${COMPOSE} up transcend-backend-test --abort-on-container-exit