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

# Utils
.PHONY: test clean

test:
	${COMPOSE} up --build transcend-backend-test --abort-on-container-exit

clean:
	${COMPOSE} down -v