package code.users.infrastructure.security.config;

import static code.bootstrap.config.TokenConfig.AUTHORIZATION_HEADER;
import static code.bootstrap.config.TokenConfig.BEARER_PREFIX;

import code.users.infrastructure.security.JwtTokenService;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketJwtInterceptor implements ChannelInterceptor {

  private final JwtTokenService jwtTokenService;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      Optional<String> jwtToken = extractJwtToken(accessor);
      jwtToken.ifPresent(token -> handleToken(token, accessor));
    }
    return message;
  }

  private void handleToken(String token, StompHeaderAccessor accessor) {
    try {
      String username = jwtTokenService.extractUsername(token);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtTokenService.isTokenValid(token, userDetails)) {
          var authToken = jwtTokenService.buildAuthentication(userDetails);
          accessor.setUser(authToken);
          log.info("User '{}' authenticated for WebSocket session.", username);
        }
      }
    } catch (JwtException | UsernameNotFoundException e) {
      log.error("WebSocket authentication failed: {}", e.getMessage());
    }
  }

  private Optional<String> extractJwtToken(StompHeaderAccessor accessor) {
    Optional<String> token = Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER))
        .filter(header -> header.startsWith(BEARER_PREFIX))
        .map(header -> header.substring(BEARER_PREFIX.length()));
    if (token.isPresent()) {
      return token;
    }
    Object attr = accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get(AUTHORIZATION_HEADER) : null;
    return Optional.ofNullable(attr)
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .filter(header -> header.startsWith(BEARER_PREFIX))
        .map(header -> header.substring(BEARER_PREFIX.length()));
  }
}