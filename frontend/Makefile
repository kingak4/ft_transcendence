include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
export BACKEND_URL

.PHONY: up down re build rebuild

CONTAINER := transcend-frontend

up:
	make -f Makefile.local generate
	$(COMPOSE) up ${CONTAINER} -d

down:
	$(COMPOSE) down ${CONTAINER}

build:
	$(COMPOSE) build 

rebuild: down build up

re: down up

# Utils
.PHONY: clean

clean:
	$(COMPOSE) down -v