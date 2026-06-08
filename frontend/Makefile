NAME        = ft_transcendence
PRETTIER    = npx prettier
ESLINT      = npx eslint

BLUE        = \033[0;34m
GREEN       = \033[0;32m
RESET       = \033[0m

include .env 
.PHONY: install dev build start lint format check clean fclean re

all: local

install:
	@echo "$(BLUE)Installing npm dependencies...$(RESET)"
	@npm install

dev: install
	@echo "$(BLUE)Starting Next.js development server...$(RESET)"
	@npm run dev

local: install
	@echo "$(BLUE)Waiting for backend API to be ready...$(RESET)"
	@while ! curl -s ${BACKEND_URL}/api-docs > /dev/null; do sleep 1; done
	@echo "$(BLUE)Generating all API types (OpenAPI & AsyncAPI)...$(RESET)"
	@npm run generate:all
	@echo "$(BLUE)Starting Next.js local...$(RESET)"
	@npm run dev

build: install
	@echo "$(BLUE)️Building Next.js for production...$(RESET)"
	@npm run build

start: install
	@echo "$(GREEN)Starting production server...$(RESET)"
	@npm run start

lint:
	@echo "$(BLUE)Running Linter...$(RESET)"
	@npm run lint

format:
	@echo "$(BLUE)Formatting code and sorting Tailwind classes...$(RESET)"
	@$(PRETTIER) --write "./**/*.{ts,tsx,css}"

check: lint
	@echo "$(BLUE)Checking formatting...$(RESET)"
	@npx prettier --check "./**/*.{ts,tsx,css}"

clean:
	@echo "$(BLUE)Cleaning build artifacts...$(RESET)"
	@rm -rf .next

fclean: clean
	@echo "$(BLUE)Removing node_modules...$(RESET)"
	@rm -rf app/types/api.d.ts
	@rm -rf app/types/asyncapi.d.ts
	@rm -rf node_modules

re: fclean all