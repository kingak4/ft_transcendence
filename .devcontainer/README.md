# ft_transcendence — Dev Container

## What the devcontainer gives you

| Tool | Why |
|---|---|
| Node 20 | `npm ci` + `npm run generate:api:dev` in the frontend Makefile |
| JDK 21 (Eclipse Temurin) | VS Code Java / Spring Boot IDE features |
| Docker-outside-of-Docker | Lets you run `docker compose` / `make -f Makefile.dev` from inside the container using the host's Docker daemon |
| Claude Code | AI coding assistant available in every terminal session inside the container |

## Ports forwarded automatically

| Port | Service |
|---|---|
| 3000 | Frontend (HTTP, direct) |
| 5001 | Backend API / Swagger UI |
| 8443 | nginx HTTPS entry point |
| 5432 | PostgreSQL |
| 6380 | Redis |

## Claude Code

### How authentication persists between container runs

Claude Code authentication is stored in a **named Docker volume** (`claude-code-config-<id>`) mounted at `/home/vscode/.claude` inside the container. Because it is a volume and not a bind mount, the data survives container rebuilds and restarts.

On every start the container also bind-mounts your **host's `~/.claude` directory** (read-only) at `/home/vscode/.claude-host`. The `postCreateCommand` and `postStartCommand` copy three files from there into the volume:

| File copied from host | Purpose |
|---|---|
| `CLAUDE.md` | Your global coding instructions |
| `settings.json` | Preferences, theme, allowed tools |
| `plugins/` | Any installed plugins |

This means changes you make to your host-side Claude config are picked up automatically on the next container start, without losing credentials stored in the volume.

### Authenticating for the first time

The first time you open the container you need to log in once:

1. Open the integrated terminal inside the container
2. Run:
   ```bash
   claude
   ```
3. Claude Code will print a browser URL — open it, complete the OAuth flow, and paste the code back into the terminal
4. Your credentials are written into the named volume and will be reused on every subsequent start

### Using Claude Code

Once authenticated, run `claude` from any directory:

```bash
# open interactive chat
claude

# ask a one-off question
claude "explain what transcend/Dockerfile does"

# start a coding session in the backend
cd backend && claude
```

### Prerequisite on the host

The bind mount `${localEnv:HOME}/.claude` must exist on your host machine before reopening the container, otherwise Docker will create it as an empty directory and the copy step will silently produce nothing. If `~/.claude` does not exist yet, create it:

```bash
mkdir -p ~/.claude
```

---

## Opening the project in the dev container

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) is running
- The [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension is installed in VS Code

### Steps

1. Open the project folder in VS Code (`File → Open Folder…` → select `ft_transcendence/`)
2. Press `Ctrl+Shift+P` to open the command palette
3. Type `Dev Containers: Reopen in Container` and press `Enter`
4. VS Code will build the image and reopen the workspace inside the container — this takes a few minutes on first run
5. Once inside, open the integrated terminal (`Ctrl+`` ` ``) — you are now running inside the container

To return to your local environment at any time: `Ctrl+Shift+P` → `Dev Containers: Reopen Folder Locally`.

---

## First open

On first container creation, `setup.sh` runs automatically and copies all `.env.example` files into place:

```
infra/.env
infra/postgres/.env
infra/redis/.env
frontend/.env
frontend/.env.dev
```

The script is idempotent — it skips any file that already exists.

## Starting the stack

Start services in this order from within the devcontainer:

```bash
# 1. infra (postgres + redis + nginx)
cd infra && make up
cd infra/nginx && docker compose up -d

# 2. backend
cd backend && make -f Makefile.dev up

# 3. frontend
cd frontend && make -f Makefile.dev up
```

## Access points

| URL | Description |
|---|---|
| http://localhost:3000 | Frontend (direct, no TLS) |
| https://localhost:8443 | Frontend via nginx (HTTPS) |
| http://localhost:5001/swagger-ui.html | Backend Swagger UI |
| http://localhost:5001/api-docs | OpenAPI JSON |
| http://localhost:5001/springwolf/asyncapi-ui.html | SpringWolf AsyncAPI UI |
