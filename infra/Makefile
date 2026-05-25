# Makefile to bring up the infrastructure (docker containers)

.PHONY: all up down restart logs ps clean
COMPOSE_PG = postgres/docker-compose.yml
COMPOSE_REDIS = redis/docker-compose.yml


all: up

up:
	docker compose -f $(COMPOSE_PG) -f $(COMPOSE_REDIS) up -d --build

down:
	docker compose -f $(COMPOSE_PG) -f $(COMPOSE_REDIS) down

restart: down up

ps:
	docker compose -f $(COMPOSE_PG) -f $(COMPOSE_REDIS) ps

clean:
	docker compose -f $(COMPOSE_PG) -f $(COMPOSE_REDIS) down -v
