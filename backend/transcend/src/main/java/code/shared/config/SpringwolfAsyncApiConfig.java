package code.shared.config;

import io.github.springwolf.asyncapi.v3.model.AsyncAPI;
import io.github.springwolf.asyncapi.v3.model.components.Components;
import io.github.springwolf.asyncapi.v3.model.security_scheme.HttpSecurityScheme;
import io.github.springwolf.asyncapi.v3.model.security_scheme.SecurityScheme;
import io.github.springwolf.core.asyncapi.AsyncApiCustomizer;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "springwolf.docket.base-package")
@RequiredArgsConstructor
public class SpringwolfAsyncApiConfig implements AsyncApiCustomizer {

  @Override
  public void customize(AsyncAPI asyncAPI) {
    SecurityScheme jwtScheme =
        HttpSecurityScheme.httpBuilder().scheme("bearer").bearerFormat("JWT").build();

    if (asyncAPI.getComponents() == null) asyncAPI.setComponents(new Components());
    if (asyncAPI.getComponents().getSecuritySchemes() == null) {
      asyncAPI.getComponents().setSecuritySchemes(new HashMap<>());
    }
    asyncAPI.getComponents().getSecuritySchemes().put("bearerAuth", jwtScheme);

    if (asyncAPI.getOperations() != null) {
      asyncAPI
          .getOperations()
          .values()
          .forEach(
              operation -> {
                operation.setSecurity(List.of(SecurityScheme.builder().ref("bearerAuth").build()));
              });
    }
  }
}
