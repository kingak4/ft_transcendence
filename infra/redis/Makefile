include ../.env
include .env

.PHONY: up down reset redis logs

up:
	${COMPOSE} up -d

down:
	${COMPOSE} down -v

reset: down up

redis:
	${DOCKER} exec -it redis-db redis-cli

logs:
	${DOCKER} logs -f redis-db

clean:
	docker compose down -v
ps:
	${COMPOSE} ps
