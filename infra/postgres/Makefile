include .env

up:
	docker compose up -d

down:
	docker compose down -v

reset: down up

db:
	docker exec -it postgres-db-container psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

logs:
	docker logs -f postgres-db-container
