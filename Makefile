.PHONY: up down reset infra-up backend-up frontend-up wait-postgres wait-redis wait-backend
include ./infra/.env

up:
	$(MAKE) infra-up
	$(MAKE) backend-up
	$(MAKE) frontend-up

infra-up:
	${COMPOSE} up -d db redis
	$(MAKE) wait-postgres
	$(MAKE) wait-redis

backend-up:
	${COMPOSE} up -d backend
	$(MAKE) wait-backend

frontend-up:
	${COMPOSE} up -d frontend

wait-postgres:
	@i=0; until [ "$$( ${DOCKER} inspect -f '{{.State.Health.Status}}' postgres-db 2>/dev/null )" = "healthy" ]; do \
		i=$$((i + 1)); \
		if [ $$i -ge 60 ]; then \
			echo 'Postgres did not become ready in time' >&2; \
			exit 1; \
		fi; \
		sleep 1; \
	done

wait-redis:
	@i=0; until [ "$$( ${DOCKER} inspect -f '{{.State.Health.Status}}' redis-db 2>/dev/null )" = "healthy" ]; do \
		i=$$((i + 1)); \
		if [ $$i -ge 60 ]; then \
			echo 'Redis did not become ready in time' >&2; \
			exit 1; \
		fi; \
		sleep 1; \
	done

wait-backend:
	@i=0; until ${DOCKER} logs transcend-backend 2>/dev/null | grep -q 'Resolved DB URL:'; do \
		i=$$((i + 1)); \
		if [ $$i -ge 120 ]; then \
			echo 'Backend did not become ready in time' >&2; \
			exit 1; \
		fi; \
		sleep 1; \
	done

down:
	${COMPOSE} down -v

reset: down up