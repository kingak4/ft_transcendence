# Backend Developer Guide

## Architecture Documentation (Spring Modulith)

This project uses Spring Modulith to verify and document modular architecture.

The documentation (PlantUML component diagrams and Asciidoc files) is automatically generated when running tests.

**To generate the documentation:**
```bash
./gradlew test --tests code.ModularityTest
```

The generated files will be located in the `transcend/build/spring-modulith-docs/` directory.

## API Documentation (Swagger / OpenAPI)

This project uses `springdoc-openapi` to generate API documentation.

### Documentation Endpoints

- **Swagger UI (Visual Interface)**: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui/)
- **OpenAPI Raw JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)