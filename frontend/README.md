# Frontend Developer Guide

Welcome to the Next.js frontend application for `ft_transcendence`.

## Running the Application

### Local Development (Recommended)

To start the full development environment with automatic API type generation, use:
```bash
make local
```
**What this command does:**
1. Installs all NPM dependencies.
2. Waits for the local Spring Boot backend API to be ready.
3. Automatically fetches and generates TypeScript interfaces for both REST (OpenAPI) and WebSockets (AsyncAPI).
4. Starts the Next.js development server on [http://localhost:3000](http://localhost:3000).

### Docker Environment

If you prefer to run the frontend inside Docker:
```bash
make -f Makefile.dev build up
```

## Generated API Types

We maintain strict type safety between the backend and frontend by automatically generating TypeScript interfaces directly from the backend's live documentation.

**Do not edit these files manually!** They are overwritten automatically every time you run `make local` or `npm run generate:all`.

### 1. OpenAPI (REST)
- **Path:** `app/types/api.d.ts`
- **Source:** Generated from the backend Swagger/OpenAPI spec via `openapi-typescript`.
- **Usage:** Used for strong typing in your HTTP REST requests (e.g. `fetch` or Axios endpoints).

### 2. AsyncAPI (WebSockets & STOMP)
- **Path:** `app/types/asyncapi.d.ts`
- **Source:** Generated from the backend Springwolf spec via `@asyncapi/modelina`.
- **Usage:** Contains payload models like `SendMessageRequest` or `CheckPresenceRequest`. Use these classes when communicating through STOMP:
  ```ts
  import { SendMessageRequest } from '@/app/types/asyncapi';
  // ...
  const payload = new SendMessageRequest({ content: "Hello" });
  stompClient.publish({ destination: '/app/chat/123/send', body: JSON.stringify(payload) });
  ```

## WebSocket Infrastructure
The entire application is wrapped in a global `<StompProvider>` (using `react-stomp-hooks`) located in `app/layout.tsx`. 
You can use hooks like `useSubscription` and `useStompClient` in any Client Component without worrying about managing the underlying connection.

## Learn More

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [React Stomp Hooks](https://github.com/stomp-js/react-stomp-hooks) - Library used for STOMP integration.
