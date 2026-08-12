include .env
.PHONY: up build down rebuild re network

up: network
	make -C nginx up
	make -C postgres up
	make -C redis up

run: network
	make -C nginx run
	make -C postgres run
	make -C redis run

build:
	make -C nginx build
	make -C postgres build
	make -C redis build

down:
	make -C nginx down
	make -C postgres down
	make -C redis down
	-docker network rm transcend-net

rebuild: down build up

re: down up

# Utils
.PHONY: ps fclean killall

fclean:
	$(MAKE) -C postgres fclean
	$(MAKE) -C redis fclean

ps:
	$(MAKE) -C postgres ps
	$(MAKE) -C redis ps

killall:
	ids=$$(docker ps -aq); [ -n "$$ids" ] && docker rm -f $$ids

network:
	-docker network create transcend-net