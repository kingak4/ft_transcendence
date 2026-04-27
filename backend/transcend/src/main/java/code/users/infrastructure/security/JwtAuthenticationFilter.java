package code.users.infrastructure.security;

import code.users.domain.exceptions.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenService jwtTokenService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    resolveFrom(request).ifPresent(token -> authenticateRequest(token, request));

    filterChain.doFilter(request, response);
  }

  private Optional<String> resolveFrom(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      return Optional.empty();
    }
    return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
  }

  private void authenticateRequest(String token, HttpServletRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      return;
    }
    try {
      String username = jwtTokenService.extractUsername(token);

      Optional.ofNullable(username)
          .filter(name -> !name.isBlank())
          .map(userDetailsService::loadUserByUsername)
          .filter(userDetails -> jwtTokenService.isTokenValid(token, userDetails))
          .ifPresent(userDetails -> setAuthentication(userDetails, request));

    } catch (JwtException | UserNotFoundException e) {
      SecurityContextHolder.clearContext();
    }
  }

  private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
    var authToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
}
