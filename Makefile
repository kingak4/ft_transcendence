.PHONY: all run backend frontend

all: run

run:
	@echo "Starting both backend and frontend..."
	npx concurrently \
		-n "BACKEND,FRONTEND" \
		-c "bgBlue.bold,bgMagenta.bold" \
		"$(MAKE) -C backend" \
		"$(MAKE) -C frontend"

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