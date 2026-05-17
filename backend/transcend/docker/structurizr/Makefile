NAME        = structurizr-lite
COMPOSE_DIR = .
COMPOSE_CMD = docker-compose -f $(COMPOSE_DIR)/docker-compose.yml

.PHONY: up down logs rebuild ps

up:
	$(COMPOSE_CMD) up -d

down:
	$(COMPOSE_CMD) down

logs:
	$(COMPOSE_CMD) logs -f

rebuild:
	$(COMPOSE_CMD) up -d --force-recreate

ps:
	$(COMPOSE_CMD) ps

# sandrzej - run this to access via localhost when docker on a vm.
tunnel:
	ssh -L 8082:localhost:8082 user@192.168.99.105