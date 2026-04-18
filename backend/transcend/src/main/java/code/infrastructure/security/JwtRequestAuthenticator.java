package code.infrastructure.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtRequestAuthenticator {

  private final JwtTokenService jwtTokenService;
  private final UserDetailsService userDetailsService;

  public void authenticate(String token, HttpServletRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      return;
    }

    try {
      String username = jwtTokenService.extractUsername(token);

      if (username == null || username.isBlank()) {
        return;
      }

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (!jwtTokenService.isTokenValid(token, userDetails)) {
        return;
      }

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (JwtException exception) {
      SecurityContextHolder.clearContext();
    }
  }
}
