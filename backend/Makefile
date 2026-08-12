include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
.PHONY: up build down rebuild re

CONTAINER := transcend-backend

BUILD_STAMP := .build.stamp
BUILD_SOURCES := $(shell find transcend -type f -not -path 'transcend/build/*' -not -path 'transcend/.gradle/*')

up: $(BUILD_STAMP)
	${COMPOSE} up --wait ${CONTAINER} -d

$(BUILD_STAMP): $(BUILD_SOURCES)
	${COMPOSE} build ${CONTAINER}
	@touch $(BUILD_STAMP)

build:
	${COMPOSE} build
	@touch $(BUILD_STAMP)

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