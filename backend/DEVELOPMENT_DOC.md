# Backend Development Documentation

## Running with Docker

All commands are run from the `backend/` directory using `Makefile.dev`.

### Start

```bash
make -f Makefile.dev up
```

Starts the backend container in detached mode. The app runs with `SPRING_PROFILES_ACTIVE=dev`.

### Stop

```bash
make -f Makefile.dev down
```

Stops and removes containers, but preserves volumes.

### Other useful commands

| Command | Description |
|---|---|
| `make -f Makefile.dev build` | Rebuild Docker images without starting |
| `make -f Makefile.dev clean` | Stop containers **and delete volumes** (wipes DB data) |
| `make -f Makefile.dev test` | Run the test suite inside a dedicated container |

---

## API Documentation URLs

The backend is exposed on **port 5001** on the host machine (mapped from internal port 5000).

| Documentation | URL |
|---|---|
| Swagger UI (REST) | http://localhost:5001/swagger-ui.html |
| OpenAPI JSON (REST) | http://localhost:5001/api-docs |
| SpringWolf UI (AsyncAPI / WebSocket) | http://localhost:5001/springwolf/asyncapi-ui.html |
