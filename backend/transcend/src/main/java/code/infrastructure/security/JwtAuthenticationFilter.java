package code.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final BearerTokenResolver bearerTokenResolver;
  private final JwtRequestAuthenticator jwtRequestAuthenticator;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    bearerTokenResolver
        .resolveFrom(request)
        .ifPresent(token -> jwtRequestAuthenticator.authenticate(token, request));

    filterChain.doFilter(request, response);
  }
}
