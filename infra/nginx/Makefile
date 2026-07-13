include ../.env
export COMPOSE_IGNORE_ORPHANS
.PHONY: up build down rebuild re

CONTAINER := nginx
BUILD_STAMP := .build.stamp
BUILD_SOURCES := $(shell find . -type f -not -name '$(BUILD_STAMP)')

up: $(BUILD_STAMP)
	${COMPOSE} up -d

$(BUILD_STAMP): $(BUILD_SOURCES)
	${COMPOSE} build
	@touch $(BUILD_STAMP)

build:
	${COMPOSE} build
	@touch $(BUILD_STAMP)

down:
	${COMPOSE} down ${CONTAINER}

rebuild: down build up

re: down up
