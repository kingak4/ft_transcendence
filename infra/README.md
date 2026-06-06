# Infrastructure Developer Guide

## Is infra required to run the full stack?

**Yes.** The infra repo must be running before you start the backend or frontend.

- The **backend** (Spring Boot) connects to PostgreSQL for persistence and Redis for caching/sessions. Neither is provided by the backend itself — they live here.
- The **frontend** (port 3000) is not started by infra, but nginx expects it to be reachable at `http://frontend:3000` on the shared Docker network.
- **nginx** is the single HTTPS entry point for the browser. All traffic from `https://localhost:8443` flows through nginx and is reverse-proxied to the frontend.

Start order: **infra → backend → frontend**.

---

## Environment Setup

Before starting for the first time, copy the example env files:

```bash
# root infra env (sets DOCKER and COMPOSE variables used by Makefiles)
cp .env.example .env

# postgres credentials and version
cp postgres/.env.example postgres/.env

# redis version
cp redis/.env.example redis/.env
```

Defaults in the example files are safe for local development. Edit `postgres/.env` if you need a different database name, user, or password — and mirror those values in the backend's own env configuration.

---

## Docker Commands

All commands are run from the `infra/` directory.

### Start (postgres + redis)

```bash
make up
```

Starts PostgreSQL and Redis in detached mode. nginx is defined separately under `nginx/` and must be started independently (see below).

### Stop (postgres + redis)

```bash
make down
```

Stops containers and removes their network, but preserves volumes (data survives).

### Start nginx

```bash
cd nginx && docker compose up -d
```

### Stop nginx

```bash
cd nginx && docker compose down
```

### Other useful commands

| Command | Description |
|---|---|
| `make re` | Stop then start postgres + redis |
| `make ps` | Show running container status |
| `make logs` | Stream logs from all containers |
| `make clean` | Stop containers and delete volumes (wipes all data) |
| `make -C postgres db` | Open a psql shell inside the postgres container |
| `make -C redis redis` | Open a redis-cli shell inside the redis container |
| `make -C postgres logs` | Tail postgres container logs |
| `make -C redis logs` | Tail redis container logs |

---

## How the Services Connect

All containers (postgres, redis, nginx, and — when running — backend and frontend) share a single Docker bridge network called **`transcend-net`**. They reach each other by container name as the hostname, with no host-machine routing needed.

### PostgreSQL

- **Container name:** `postgres-db`
- **Host port:** `5432` → internal `5432`
- **Database schemas created on init:** `test`, `local`, `dev`, `prod`
- **How the backend connects:** hostname `postgres-db` (or `localhost:5432` from the host) with credentials from `infra/postgres/.env`.
- **Data persistence:** named Docker volume `pgdata`; survives `make down`, wiped by `make clean`.

### Redis

- **Container name:** `redis-db`
- **Host port:** `6380` → internal `6379` (non-standard host port to avoid conflicts with a locally installed Redis)
- **How the backend connects:** hostname `redis-db` on port `6379` inside the network, or `localhost:6380` from the host.
- **Data persistence:** named Docker volume `redisdata`; survives `make down`, wiped by `make clean`.
- **Used for:** session storage and query caching. If the container is restarted, active sessions are lost and logged-in users will be signed out — this is expected in development.

### nginx

- **Container name:** `nginx`
- **Host port:** `8443` → internal `443` (HTTPS only)
- **TLS:** A self-signed certificate is generated automatically inside the Docker image at build time — no manual cert setup needed.
- **What it proxies:** All requests to `https://localhost:8443/` are forwarded to `http://frontend:3000`. nginx does not proxy the backend directly; the frontend app is responsible for calling the backend.

### Network Diagram

```
Browser
  │  HTTPS :8443
  ▼
nginx (transcend-net)
  │  HTTP  :3000
  ▼
frontend (transcend-net)
  │  HTTP  :5000 / :5001
  ▼
backend (transcend-net)
  ├──► postgres-db :5432  (persistence)
  └──► redis-db    :6379  (caching / sessions)
```
