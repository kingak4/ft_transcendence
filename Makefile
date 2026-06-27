include ./infra/.env

.PHONY: up down re build rebuild env network

up: network
	$(MAKE) -C infra up
	$(MAKE) -C backend up
	$(MAKE) -C frontend up

down:
	${COMPOSE} down

build: network 
	$(MAKE) -C infra build
	$(MAKE) -C backend build
	$(MAKE) -C frontend build

rebuild: down build up

re: down up

# Setup

env:
	find . -name ".env.example" -type f | while read file; do \
		dir=$$(dirname $$file); \
		cp $$file $$dir/.env; \
		echo "Created $$dir/.env from $$file"; \
	done

network:
	docker network create transcend-net 2>/dev/null || true

# Utils

frontend-local: infra-up backend-up
	$(MAKE) -C frontend local