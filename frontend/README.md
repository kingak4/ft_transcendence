# Frontend Developer Guide

Welcome to the Next.js frontend application for `ft_transcendence`.

---

## Environment Setup

Before starting for the first time, create the two env files:

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

> `http://transcend-backend:5000` is the Docker-internal hostname of the backend on `transcend-net`. It only resolves inside Docker — **do not use it for local dev.**

> If you change `BACKEND_URL` you must rebuild the Docker image: `make -f Makefile.dev build`.

---

## Running the Application

### Local Development (Recommended)

Runs Next.js directly with Node — no Docker required. Works both inside and outside a dev container, as long as Node.js is installed.

```bash
cd frontend
npm install        # first time only
npm run dev
```

Then open [http://localhost:3000](http://localhost:3000).

**Connecting to the backend from local dev**

`http://transcend-backend:5000` is a Docker-internal address that does not resolve from your host. When running `npm run dev`, you need to override `BACKEND_URL` to point at localhost. Create a `.env.local` file (Next.js reads this automatically and it takes precedence over `.env`):

```bash
# frontend/.env.local  — not committed, local override only
BACKEND_URL=http://localhost:5000
```

> `.env.local` is gitignored by Next.js by convention. You only need to create it once.

**What if the backend is not running?**

For UI work and component development, you do not need the backend at all — API calls will simply fail and you handle the empty/error state as you would in production. Only bring up the backend when you need to test a real API call end-to-end.

When you do need the backend, start only the services it depends on (skip nginx and the frontend container):

```bash
cd infra && make up                      # Postgres + Redis
cd backend && make -f Makefile.dev up    # Spring Boot backend
```

### Docker Environment

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
# local dev
npm run generate:all:dev

# or via Make (also rebuilds the Docker image)
make -f Makefile.dev generate
make -f Makefile.dev build
```

> The backend must be running before regenerating — `openapi-typescript` fetches the live `/api-docs` endpoint.

---

## WebSocket Infrastructure

The entire application is wrapped in a global `<StompProvider>` (via `react-stomp-hooks`) in `app/layout.tsx`. You can use `useSubscription` and `useStompClient` in any Client Component without managing the underlying connection yourself.

---

## Learn More

- [Next.js Documentation](https://nextjs.org/docs)
- [React Stomp Hooks](https://github.com/stomp-js/react-stomp-hooks)
