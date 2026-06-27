include .env
.PHONY: up build down rebuild re

up:
	make -C nginx up
	make -C postgres up
	make -C redis up

build:
	make -C nginx build
	make -C postgres build
	make -C redis build

down:
	make -C nginx down
	make -C postgres down
	make -C redis down

rebuild: down build up

re: down up

# Utils
.PHONY: ps clean killall

clean:
	$(MAKE) -C postgres clean
	$(MAKE) -C redis clean

ps:
	$(MAKE) -C postgres ps
	$(MAKE) -C redis ps

killall:
	ids=$$(docker ps -aq); [ -n "$$ids" ] && docker rm -f $$ids