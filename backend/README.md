# Backend Developer Guide

## Backend AsciiDoc Documentation

**To generate documentation:**
```bash
make docs
```

- **Documentation UI**: transcend/build/docs/asciidoc/index.html

## API Documentation (Swagger / OpenAPI)

### Documentation Endpoints

**Run the application with**
```bash
make run
```
- **Swagger Interactive UI**: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui/)
- **OpenAPI Raw JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)


## Test coverage
**Run the tests**
```bash
make test
```
- **Test Coverage UI**: transcend/build/reports/jacoco/index.html

## Linter
**Run the linter**
```bash
make lint
```
- **PmdMain UI**: transcend/build/reports/pmd/main.html
- **PmdTest UI**: transcend/build/reports/pmd/test.html