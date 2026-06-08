# Infra Development Documentation

## Is infra required to run the full stack?

**Yes.** The infra repo must be running before you start the backend or frontend.

- The **backend** (Spring Boot) connects to PostgreSQL for persistence and Redis for caching/sessions. Neither is provided by the backend itself — they live here.
- The **frontend** (port 3000) is not started by infra, but nginx expects it to be reachable at `http://frontend:3000` on the shared Docker network.
- **nginx** is the single HTTPS entry point for the browser. All traffic from `https://localhost:8443` flows through nginx and is reverse-proxied to the frontend.

Start order: **infra → backend → frontend**.

---

## Docker commands

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

Stops containers and removes their volumes (data survives).

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
| `make clean` | Stop containers and delete volumes (wipes all data) |
| `make -C postgres db` | Open a psql shell inside the postgres container |
| `make -C redis redis` | Open a redis-cli shell inside the redis container |
| `make -C postgres logs` | Tail postgres container logs |
| `make -C redis logs` | Tail redis container logs |

---

## How the services connect

All four containers (postgres, redis, nginx, and — when running — backend and frontend) share a single Docker bridge network called **`transcend-net`**. This means they reach each other by container name as the hostname, with no host-machine routing needed.

### PostgreSQL

- **Container name:** `postgres-db`
- **Host port:** `5432` → internal `5432`
- **Database schemas created on init:** `test`, `local`, `dev`, `prod`
- **How the backend connects:** The backend uses the hostname `postgres-db` (or `localhost:5432` from the host) and the credentials from `infra/postgres/.env`.
- **Data persistence:** stored in a named Docker volume `pgdata`; survives `make down` but is wiped by `make clean`.

### Redis

Redis is an **in-memory key-value store**. Unlike PostgreSQL, which writes everything to disk and is designed for durable, relational data, Redis keeps its entire dataset in RAM. This makes reads and writes extremely fast (microseconds) but means it is not the right place for data you cannot afford to lose — it is used for data that is either reproducible or short-lived.

In this stack the backend uses Redis for two main purposes. First, **session storage**: when a user logs in, their session token is written to Redis so the backend can validate subsequent requests without hitting the database on every call. Second, **caching**: results of expensive or frequently repeated database queries can be stored in Redis with a time-to-live (TTL), so the backend serves them from memory instead of querying PostgreSQL again. Both use cases benefit from Redis's speed and its ability to automatically expire keys after a set time.

A practical consequence: if the Redis container is stopped and restarted, active user sessions are lost and logged-in users will be signed out. Cached data is also lost, but that only causes a temporary slowdown — the backend will repopulate the cache from PostgreSQL as requests come in. This is expected behaviour in development.

- **Container name:** `redis-db`
- **Host port:** `6380` → internal `6379` (note the non-standard host port to avoid conflicts with a locally installed Redis)
- **How the backend connects:** The backend uses hostname `redis-db` on port `6379` inside the network, or `localhost:6380` from the host.
- **Data persistence:** stored in a named Docker volume `redisdata`; survives `make down` but is wiped by `make clean`.

### nginx

- **Container name:** `nginx`
- **Host port:** `8443` → internal `443` (HTTPS only)
- **TLS:** A self-signed certificate is generated automatically inside the Docker image at build time — no manual cert setup needed.
- **What it proxies:** All requests to `https://localhost:8443/` are forwarded to `http://frontend:3000`. nginx does not proxy the backend directly; the frontend app is responsible for calling the backend.
- **Why it exists:** It provides the single HTTPS entry point the browser uses. The frontend dev server (port 3000) is HTTP-only; nginx wraps it in TLS and forwards the correct host headers so the app behaves as if it were running on `localhost:8443`.

### Network diagram

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

---

## Environment setup

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
