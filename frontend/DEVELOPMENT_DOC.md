# Frontend Development Documentation

## Prerequisites

The frontend depends on infra and backend being already running. Start order: **infra → backend → frontend**.

---

## Environment setup

Before starting for the first time, create the two env files the Makefile and compose expect:

```bash
cp .env.example .env
cp .env.example .env.dev
```

Both files should already contain the correct value after copying:

```
BACKEND_URL=http://transcend-backend:5000
```

**Why two files?**

| File | Who reads it | When |
|---|---|---|
| `.env` | `Makefile.dev` | Supplies `BACKEND_URL` as a Docker **build arg** — baked into the Next.js bundle during `docker build` |
| `.env.dev` | `app-compose.yml` | Loaded as **runtime env vars** into the running container |

The `rewrites()` in `next.config.ts` read `BACKEND_URL` at build time, so the `.env` value is what actually controls where `/api/*` requests are proxied. The `.env.dev` copy is redundant for that purpose but must exist because the compose file declares it as `env_file`.

> `http://transcend-backend:5000` is the Docker-internal address of the backend container on `transcend-net`. Do not use `localhost` here — inside a container, `localhost` means the container itself, not your host machine.

> If you change `BACKEND_URL` you must rebuild the image: `make -f Makefile.dev build`.

---

## API type generation

The frontend uses generated TypeScript types from the backend's OpenAPI spec. These are written to `app/types/api.d.ts` and are **not committed** to the repo.

`make -f Makefile.dev up` will handle everything automatically on first run: it installs local `node_modules` (needed to run `openapi-typescript`) and then generates the types. If you ever need to regenerate manually — e.g. after a backend schema change — run:

```bash
make -f Makefile.dev generate
```

Then rebuild the Docker image so the new types are baked in:

```bash
make -f Makefile.dev build
```

> The backend must be running before generating types, as `openapi-typescript` fetches the live `/api-docs` endpoint.

---

## Docker commands

All commands are run from the `frontend/` directory using `Makefile.dev`.

### Start

```bash
make -f Makefile.dev up
```

Builds (if not already built) and starts the frontend container in detached mode. The app listens on port **3000**.

### Stop

```bash
make -f Makefile.dev down
```

Stops and removes the container.

### Other useful commands

| Command | Description |
|---|---|
| `make -f Makefile.dev build` | Rebuild the Docker image without starting |
| `make -f Makefile.dev clean` | Stop the container **and delete volumes** |

---

## How the frontend is built

The Dockerfile uses a two-stage build:

1. **Builder stage** — installs dependencies and runs `npm run build` (Next.js static + standalone output). The `BACKEND_URL` build arg is injected here.
2. **Runner stage** — copies only the compiled standalone output and static assets, then starts `node server.js`.

This means the final image contains no source code or `node_modules` — only the compiled output. It is a production-mode image even in development.

---

## Accessing the app

The frontend container joins the shared **`transcend-net`** Docker network. nginx (started from `infra/nginx/`) is the HTTPS entry point and reverse-proxies to the frontend.

| Access point | URL |
|---|---|
| Direct (HTTP, no TLS) | http://localhost:3000 |
| Via nginx (HTTPS) | https://localhost:8443 |

Use `https://localhost:8443` for normal development — this is the address nginx is configured for and the one the browser should use.

---

## Running without nginx (HTTP only)

If you want to skip nginx and access the app directly over HTTP, no configuration change is needed. The `BACKEND_URL` stays the same (`http://transcend-backend:5000`) because it is used server-side inside the container — the Next.js server proxies all `/api/*` requests to the backend over the Docker-internal network, regardless of how the browser reaches the frontend.

Start the frontend the same way:

```bash
make -f Makefile.dev up
```

Then open:

```
http://localhost:3000
```

The only thing you give up by skipping nginx is TLS. All other functionality (routing, API proxying to the backend) works identically.
