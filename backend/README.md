# Backend Developer Guide

> Ensure the infra is running before running the application or tests.

---

## Running Locally

```bash
make run
```

The app is available on **port 8080**:

| Documentation | URL |
|---|---|
| Swagger UI (REST) | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON (REST) | http://localhost:8080/api-docs |
| SpringWolf UI (AsyncAPI / WebSocket) | http://localhost:8080/springwolf/asyncapi-ui.html |
| AsyncAPI JSON | http://localhost:8080/springwolf/docs |

---

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
| `make -f Makefile.dev build up` | Rebuild images and start containers |
| `make -f Makefile.dev clean` | Stop containers **and delete volumes** (wipes DB data) |
| `make -f Makefile.dev test` | Run the test suite inside a dedicated container |

The backend is exposed on **port 5001** on the host machine (mapped from internal port 5000):

| Documentation | URL |
|---|---|
| Swagger UI (REST) | http://localhost:5001/swagger-ui.html |
| OpenAPI JSON (REST) | http://localhost:5001/api-docs |
| SpringWolf UI (AsyncAPI / WebSocket) | http://localhost:5001/springwolf/asyncapi-ui.html |

---

## Backend AsciiDoc Documentation

```bash
make test
```

- **Documentation UI**: `transcend/build/docs/asciidoc/index.html`

---

## Test Coverage and Linter

```bash
make check
```

- **Test Coverage UI**: `transcend/build/reports/jacoco/index.html`
- **PmdMain UI**: `transcend/build/reports/pmd/main.html`
- **PmdTest UI**: `transcend/build/reports/pmd/test.html`
