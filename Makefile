-include ./infra/.env

.PHONY: up down re build rebuild

.PHONY: local

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
	cp infra/.env.example infra/.env
	cp infra/postgres/.env.example infra/postgres/.env
	cp infra/redis/.env.example infra/redis/.env
	cp backend/transcend/.env.example backend/transcend/.env
	cp backend/transcend/.env.local.example backend/transcend/.env.local
	cp backend/.env.example backend/.env
	cp frontend/.env.example frontend/.env
	cp frontend/.env.local.example frontend/.env.local

# Utils
.PHONY: frontend-local

clean:
	$(MAKE) -C infra clean
	$(MAKE) -C backend clean
	$(MAKE) -C frontend clean

frontend-local: backend-dev
	$(MAKE) -C frontend -f Makefile.local up

backend-dev:
	$(MAKE) -C infra up
	$(MAKE) -C backend -f Makefile.dev up

backend-local:
	$(MAKE) -C infra -f Makefile.local up
	$(MAKE) -C backend -f Makefile.local up

.PHONY: check-backend-endpoints