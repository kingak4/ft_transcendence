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

# Utils
.PHONY: test clean

test:
	${COMPOSE} up --build transcend-backend-test --abort-on-container-exit

clean:
	${COMPOSE} down -v