.PHONY: all up down re logs ps clean

all: up

up:
	$(MAKE) -C postgres up
	$(MAKE) -C redis up

down:
	$(MAKE) -C postgres down
	$(MAKE) -C redis down

re: down up

ps:
	$(MAKE) -C postgres ps
	$(MAKE) -C redis ps

clean:
	$(MAKE) -C postgres clean
	$(MAKE) -C redis clean
