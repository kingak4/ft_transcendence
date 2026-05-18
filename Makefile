.PHONY: all run backend frontend webclient

all: run

run:
	@echo "Starting both backend and frontend..."
	npx concurrently \
		-n "BACKEND,FRONTEND, WEBCLIENT" \
		-c "bgBlue.bold,bgMagenta.bold, bgGreen" \
		"$(MAKE) -C backend" \
		"$(MAKE) -C frontend" \
		"$(MAKE) webclient"

backend:
	@echo "Starting backend..."
	npx concurrently \
		-n "BACKEND" \
		-c "bgBlue.bold"\
		"$(MAKE) -C backend"

frontend:
	@echo "Starting frontend..."
	npx concurrently \
		-n "FRONTEND" \
		-c "bgMagenta.bold"\
		"$(MAKE) -C frontend"

webclient:
	@echo "Waiting for backend OpenAPI docs to be available..."
		@until curl --output /dev/null --silent --head --fail http://localhost:8080/api-docs; do \
			echo "Waiting for backend..."; \
			sleep 2; \
	done
	npx openapi-typescript http://localhost:8080/api-docs -o ./frontend/app/types/api.d.ts