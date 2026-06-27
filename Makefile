include ./infra/.env

.PHONY: up down re build rebuild

up: network
	$(MAKE) -C infra up
	$(MAKE) -C backend up
	$(MAKE) -C frontend up

down:
	${COMPOSE} down

build: network 
	${COMPOSE} build

rebuild: down build up

re: down up

# Setup
.PHONY: env network

env:
	find . -name ".env.example" -type f | while read file; do \
		dir=$$(dirname $$file); \
		cp $$file $$dir/.env; \
		echo "Created $$dir/.env from $$file"; \
	done

network:
	docker network create transcend-net 2>/dev/null || true

# Utils
.PHONY: frontend-local

clean:
	$(MAKE) -C infra clean
	$(MAKE) -C backend clean
	$(MAKE) -C frontend clean

frontend-local: infra-up backend-up
	$(MAKE) -C frontend local