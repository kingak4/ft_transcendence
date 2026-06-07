# Frontend Developer Guide

Welcome to the Next.js frontend application for `ft_transcendence`.

---

## Environment Setup

Only `.env.example` is committed. All real env files are gitignored — create them locally once:

```bash
cp .env.example .env          # Docker context: transcend-backend:5000 already set
```

Then create `.env.local` manually (value depends on your setup — see the Local Development section below).

**Two env files, two purposes:**

| File | Read by | Used when | Value | Committed? |
|---|---|---|---|---|
| `.env` | Docker Compose (automatic), `Makefile.dev` (build arg), `app-compose.yml` (container runtime), `npm run generate:api` | Docker `build` — bakes `BACKEND_URL` into the Next.js bundle via `rewrites()` in `next.config.ts`; also injected as a runtime env var inside the container | `http://transcend-backend:5000` | **No** — create with `cp .env.example .env` |
| `.env.local` | Next.js (automatic, highest priority — overrides `.env`), `npm run generate:*:dev`, `Makefile.dev` generate target | `npm run dev` runtime and local type generation — overrides the Docker-internal default so these can reach the backend | `http://localhost:5001` — works for both devcontainer (`--network=host` in `devcontainer.json` shares the host network stack) and host terminal | **No** — create manually |

`http://transcend-backend:5000` is the Docker-internal hostname — it only resolves inside the `transcend-net` Docker network. Outside Docker (local dev, type generation) you need the host-reachable address, which is what `.env.local` provides.

> If you change `BACKEND_URL` in `.env` you must rebuild the Docker image: `make -f Makefile.dev build`.

---

## Running the Application

### Local Development (Recommended) — uses `.env.local`

Once the `.env` files are ready run Next.js directly with Node — no Docker required. Works both inside and outside a dev container, as long as Node.js is installed.

```bash
cd frontend
npm install              # first time only
npm run generate:all:dev # first time, and after backend schema changes (backend must be running)
npm run dev
```

Then open [http://localhost:3000](http://localhost:3000).

**Connecting to the backend from local dev**

`http://transcend-backend:5000` is a Docker-internal address that does not resolve outside Docker. Create a `.env.local` file — Next.js reads it automatically at the highest priority, overriding `.env`. You only need to do this once.

**Both devcontainer and host terminal** — use the same value:
```bash
# frontend/.env.local
BACKEND_URL=http://localhost:5001
```
`devcontainer.json` runs the devcontainer with `--network=host`, which makes the container share the host's network stack entirely. `localhost` inside the devcontainer is the same `localhost` as on your WSL/Linux host, so port 5001 reaches the backend's published port directly.

> `.env.local` is gitignored. See the table in [Environment Setup](#environment-setup) for the full breakdown of both env files.

**What if the backend is not running?**

For UI work and component development, you do not need the backend at all — API calls will simply fail and you handle the empty/error state as you would in production. Only bring up the backend when you need to test a real API call end-to-end.

When you do need the backend, start only the services it depends on (skip nginx and the frontend container):

```bash
cd infra && make up                      # Postgres + Redis
cd backend && make -f Makefile.dev up    # Spring Boot backend
```

#### How to check if your full development stack is running correctly in development container

Here's the full verification sequence in order — each step confirms the
  previous one worked before going further.

Step 1 — Backend containers are up and healthy
```bash
docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
```
*List running containers as a table showing name, health status, and port
bindings. All three — transcend-backend, redis-db, postgres-db — should
show (healthy).*

Step 2 — Devcontainer is joined to transcend-net
```bash
docker network inspect transcend-net --format '{{range.Containers}}{{.Name}}{{"\n"}}{{end}}'
```
*Print the names of every container currently on transcend-net. You should
see transcend-backend and your devcontainer's name (something like
ft_transcendence_devcontainer_...).*

If the devcontainer is missing, the postStartCommand hasn't run yet —
reload the VS Code window first (Ctrl+Shift+P → Developer: Reload Window),
then re-run this check.

Step 3 — Backend is reachable by name from inside the devcontainer
```bash
curl -s http://transcend-backend:5000/actuator/health | cat
```
*Make an HTTP request to the backend's health endpoint using the Docker
container name (not an IP). If the network join worked, this resolves and
returns {"status":"UP"}. If it fails with Could not resolve host, step 2
didn't succeed.*

Step 4 — Type generation works

Via npm (reads `BACKEND_URL` from `.env.local`):
```bash
npm run generate:all:dev
```

Via Make (reads `BACKEND_URL` from `.env`, then `.env.local` overrides — same value in devcontainer):
```bash
make -f Makefile.dev generate
```
*Both commands generate `app/types/api.d.ts` (OpenAPI/REST) and
`app/types/asyncapi.d.ts` (WebSocket) by fetching live specs from the
running backend. Both should complete without errors.*

Step 5 — Frontend compiles cleanly

Via npm:
```bash
npm run build 2>&1 | tail -20
```

Via Make:
```bash
make build 2>&1 | tail -20
```
*Run a production Next.js build and show only the last 20 lines of output.
A clean build ends with `✓ Compiled successfully`. The `make build` target
does the same thing — it installs dependencies first if missing, then runs
`npm run build`.*

Step 6 — Makefile targets all work

These targets do not require the backend:
```bash
make lint
```
*Run ESLint across the project. Should exit with no errors.*

```bash
make check
```
*Run ESLint then verify Prettier formatting across all `.ts`, `.tsx`, and
`.css` files. Should exit cleanly. Use `make format` to auto-fix any
formatting issues.*

These targets verify the clean/rebuild cycle:
```bash
make clean && make build 2>&1 | tail -5
```
*Delete `app/types/api.d.ts` and the `.next` build cache, then rebuild.
Confirms that Make correctly re-triggers the build from a clean state.
The last 5 lines should still end with `✓ Compiled successfully`.*

```bash
make fclean && make build 2>&1 | tail -5
```
*Delete `node_modules` as well, then rebuild. This is the slowest path —
Make reinstalls all dependencies before building. Useful to confirm a
fresh-clone experience works end-to-end.*

### Docker Environment — uses `.env` (build arg)

Runs the frontend inside a container, mirroring the production setup. Requires infra and backend to already be running.

**Start order: infra → backend → frontend.**

```bash
make -f Makefile.dev up
```

Builds (if not already built) and starts the container in detached mode. The app listens on port **3000**.

| Command | Description |
|---|---|
| `make -f Makefile.dev up` | Build (if needed) and start the container |
| `make -f Makefile.dev down` | Stop and remove the container |
| `make -f Makefile.dev build` | Rebuild the Docker image without starting |
| `make -f Makefile.dev clean` | Stop the container and delete volumes |

**Accessing the app**

| Access point | URL |
|---|---|
| Direct (HTTP, no TLS) | http://localhost:3000 |
| Via nginx (HTTPS) | https://localhost:8443 |

Use `https://localhost:8443` for normal Docker-based development — that is the address nginx is configured for. If you skip nginx, access the app directly at `http://localhost:3000`; all routing and API proxying still works, the only thing you give up is TLS.

---

## Generated API Types

Type safety between the backend and frontend is maintained by auto-generating TypeScript interfaces from the backend's live documentation. **Do not edit these files manually** — they are overwritten every time types are regenerated.

### 1. OpenAPI (REST)
- **Path:** `app/types/api.d.ts`
- **Source:** Generated from the backend Swagger/OpenAPI spec via `openapi-typescript`
- **Usage:** Used for strong typing in HTTP REST requests with `openapi-fetch`

### 2. AsyncAPI (WebSockets & STOMP)
- **Path:** `app/types/asyncapi.d.ts`
- **Source:** Generated from the backend Springwolf spec via `@asyncapi/modelina`
- **Usage:** Payload models for STOMP communication:
  ```ts
  import { SendMessageRequest } from '@/app/types/asyncapi';

  const payload = new SendMessageRequest({ content: 'Hello' });
  stompClient.publish({ destination: '/app/chat/123/send', body: JSON.stringify(payload) });
  ```

### Regenerating types

Types are generated automatically on `make -f Makefile.dev up`. To regenerate manually after a backend schema change:

```bash
# local dev (reads BACKEND_URL from .env.local)
npm run generate:all:dev

# or via Make (reads BACKEND_URL from .env.local, falls back to .env)
make -f Makefile.dev generate
```

> The backend must be running before regenerating — `openapi-typescript` fetches the live `/api-docs` endpoint.
> Make sure `.env.local` exists with the correct `BACKEND_URL` for your environment before running either command.

---

## WebSocket Infrastructure

The entire application is wrapped in a global `<StompProvider>` (via `react-stomp-hooks`) in `app/layout.tsx`. You can use `useSubscription` and `useStompClient` in any Client Component without managing the underlying connection yourself.

---

## Learn More

- [Next.js Documentation](https://nextjs.org/docs)
- [React Stomp Hooks](https://github.com/stomp-js/react-stomp-hooks)
