include ./infra/.env

.PHONY: up down re build rebuild

up:
	$(MAKE) -C infra up
	$(MAKE) -C backend up
	$(MAKE) -C frontend up

down:
	$(MAKE) -C frontend down
	$(MAKE) -C backend down
	$(MAKE) -C infra down

build:
	${COMPOSE} build

rebuild: down build up

re: down up

# Setup
.PHONY: env

env:
	find . -name ".env.example" -type f | while read file; do \
		dir=$$(dirname $$file); \
		cp $$file $$dir/.env; \
		echo "Created $$dir/.env from $$file"; \
	done

# Utils
.PHONY: frontend-local

clean:
	$(MAKE) -C infra clean
	$(MAKE) -C backend clean
	$(MAKE) -C frontend clean

frontend-local:
	$(MAKE) -C infra up
	$(MAKE) -C backend -f Makefile.local up
	$(MAKE) -C frontend -f Makefile.local local