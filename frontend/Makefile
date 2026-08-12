include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
export BACKEND_URL

.PHONY: up down re build rebuild

CONTAINER := transcend-frontend

BUILD_STAMP := .build.stamp
BUILD_SOURCES := $(shell find . -type f \
	-not -path './node_modules/*' \
	-not -path './.next/*' \
	-not -path './app/types/*' \
	-not -name '$(BUILD_STAMP)')

up: $(BUILD_STAMP)
	$(COMPOSE) up ${CONTAINER} -d

$(BUILD_STAMP): $(BUILD_SOURCES)
	$(COMPOSE) build ${CONTAINER}
	@touch $(BUILD_STAMP)

down:
	$(COMPOSE) down ${CONTAINER}

build:
	$(COMPOSE) build
	@touch $(BUILD_STAMP)

rebuild: down build up

re: down up

# Utils
.PHONY: clean

clean:
	$(COMPOSE) down -v