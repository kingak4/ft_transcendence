package code.bootstrap.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.annotation.security.PermitAll;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Transcend API"),
    security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Call /users/login with fixture credentials: user@email.com / plain-password")
public class OpenApiConfig {
  private static final String PROBLEM_DETAIL_SCHEMA_NAME = "ProblemDetail";
  private static final String PROBLEM_DETAIL_SCHEMA_REF =
      "#/components/schemas/" + PROBLEM_DETAIL_SCHEMA_NAME;

  @Bean
  public OpenApiCustomizer problemDetailSchemaCustomizer() {
    return openApi -> {
      Schema<?> problemSchema =
          ModelConverters.getInstance()
              .resolveAsResolvedSchema(new AnnotatedType(ProblemDetail.class))
              .schema;

      if (openApi.getComponents() == null) {
        openApi.setComponents(new Components());
      }

      openApi.getComponents().addSchemas(PROBLEM_DETAIL_SCHEMA_NAME, problemSchema);
    };
  }

  @Bean
  public OperationCustomizer globalErrorResponsesCustomizer() {
    return (operation, handlerMethod) -> {
      ApiResponses apiResponses = operation.getResponses();

      addErrorResponseIfMissing(apiResponses, HttpStatus.BAD_REQUEST, "Bad Request");
      if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
        addErrorResponseIfMissing(apiResponses, HttpStatus.UNAUTHORIZED, "Unauthorized");
        addErrorResponseIfMissing(apiResponses, HttpStatus.FORBIDDEN, "Forbidden");
      }
      addErrorResponseIfMissing(
          apiResponses, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

      return operation;
    };
  }

  private void addErrorResponseIfMissing(
      ApiResponses apiResponses, HttpStatus status, String description) {
    String statusCode = String.valueOf(status.value());

    if (!apiResponses.containsKey(statusCode)) {
      apiResponses.addApiResponse(statusCode, createProblemDetailResponse(description));
    }
  }

  private ApiResponse createProblemDetailResponse(String description) {
    var mediaType =
        new io.swagger.v3.oas.models.media.MediaType()
            .schema(new Schema<>().$ref(PROBLEM_DETAIL_SCHEMA_REF));

    Content content =
        new Content().addMediaType(MediaType.APPLICATION_PROBLEM_JSON_VALUE, mediaType);

    return new ApiResponse().description(description).content(content);
  }
}
