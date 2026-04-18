package code.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BearerTokenResolver {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  public Optional<String> resolveFrom(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      return Optional.empty();
    }

    return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
  }
}
