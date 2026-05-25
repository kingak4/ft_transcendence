# Backend Developer Guide

## API Documentation (Swagger / OpenAPI)

### Documentation Endpoints

**Run the application with**
```bash
make run
```
- **Swagger Interactive UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Raw JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
- **Springwolf UI**: [http://localhost:8080/springwolf/asyncapi-ui.html](http://localhost:8080/springwolf/asyncapi-ui.html)
- **AsyncAPI Raw JSON**: [http://localhost:8080/springwolf/docs](http://localhost:8080/springwolf/docs)

## Backend AsciiDoc Documentation
**Generate the docs**
```bash
make test
```
- **Documentation UI**: transcend/build/docs/asciidoc/index.html

## Test coverage and Linter
**Run the tests**
```bash
make check
```
- **Test Coverage UI**: transcend/build/reports/jacoco/index.html
- **PmdMain UI**: transcend/build/reports/pmd/main.html
- **PmdTest UI**: transcend/build/reports/pmd/test.html