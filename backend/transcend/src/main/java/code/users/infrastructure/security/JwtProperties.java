package code.users.infrastructure.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
@Getter
@RequiredArgsConstructor
public class JwtProperties {

  private final String secret;
  private final long expirationMs;
}
