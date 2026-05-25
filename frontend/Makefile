NAME        = ft_transcendence
PRETTIER    = npx prettier
ESLINT      = npx eslint

BLUE        = \033[0;34m
GREEN       = \033[0;32m
RESET       = \033[0m

include .env 
.PHONY: install dev build start lint format check clean fclean re

all: dev

certificate:
	mkdir -p certificates
	~/.cache/mkcert/mkcert-v1.4.4-linux-amd64 -key-file ./certificates/localhost-key.pem -cert-file ./certificates/localhost.pem localhost 127.0.0.1 ::1

install:
	@echo "$(BLUE)Installing npm dependencies...$(RESET)"
	@npm install

dev: install
	@echo "$(BLUE)Starting Next.js development server...$(RESET)"
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
	@rm -rf node_modules

re: fclean all