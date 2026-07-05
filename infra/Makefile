.PHONY: all up down re logs ps clean killall network

all: up

network:
	docker network create transcend-net 2>/dev/null || true

up: network
	$(MAKE) -C postgres up
	$(MAKE) -C redis up

down:
	$(MAKE) -C postgres down
	$(MAKE) -C redis down
	docker network rm transcend-net 2>/dev/null || true

re: down up

ps:
	$(MAKE) -C postgres ps
	$(MAKE) -C redis ps

clean:
	$(MAKE) -C postgres clean
	$(MAKE) -C redis clean

killall:
	ids=$$(docker ps -aq); [ -n "$$ids" ] && docker rm -f $$ids
