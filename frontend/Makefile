include .env
include ./../infra/.env
export COMPOSE_IGNORE_ORPHANS
export BACKEND_URL

.PHONY: up down re build rebuild run

CONTAINER := transcend-frontend

BUILD_STAMP := .build.stamp
BUILD_SOURCES := $(shell find . -type f \
	-not -path './node_modules/*' \
	-not -path './.next/*' \
	-not -path './app/types/*' \
	-not -name '$(BUILD_STAMP)')

up: $(BUILD_STAMP) run

run:
	${COMPOSE} up --wait ${CONTAINER} -d

$(BUILD_STAMP): $(BUILD_SOURCES)
	make -f Makefile.local build
	$(COMPOSE) build ${CONTAINER}
	@touch $(BUILD_STAMP)

down:
	$(COMPOSE) down ${CONTAINER}

build:
	$(COMPOSE) build
	@touch $(BUILD_STAMP)

rebuild: down build up

re: down up

# Utils
.PHONY: fclean check-endpoints

fclean:
	$(COMPOSE) down -v


FRONTEND_ENDPOINTS := \
	https://localhost:8443

check-endpoints:
	@for url in $(FRONTEND_ENDPOINTS); do \
		echo "Checking $$url ..."; \
		code=$$(curl -k -s -o /dev/null -w "%{http_code}" \
			--retry 2 \
			--retry-delay 3 \
			--retry-connrefused \
			--connect-timeout 2 \
			"$$url" || true); \
		if [ "$$code" = "200" ]; then \
			echo "[v] $$url accessible"; \
		else \
			echo "[x] $$url inaccessible (HTTP $$code)"; \
			exit 1; \
		fi; \
	done