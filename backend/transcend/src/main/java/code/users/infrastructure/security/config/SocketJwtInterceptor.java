package code.users.infrastructure.security.config;

import static code.bootstrap.config.TokenConfig.AUTHORIZATION_HEADER;
import static code.bootstrap.config.TokenConfig.BEARER_PREFIX;

import code.users.infrastructure.security.JwtTokenService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

      // 1. Fail if token is missing
      if (jwtToken.isEmpty()) {
        log.error("Authentication failed: No token provided");
        throw new MessageDeliveryException("Missing authentication token");
      }

      // 2. Attempt to authenticate
      handleToken(jwtToken.get(), accessor);

      // 3. Fail if handleToken did not result in a valid user
      if (accessor.getUser() == null) {
        log.error("Authentication failed: Invalid token");
        throw new MessageDeliveryException("Invalid authentication token");
      }
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
    } catch (Exception e) {
      log.error("WebSocket authentication processing error: {}", e.getMessage());
    }
  }

  private Optional<String> extractJwtToken(StompHeaderAccessor accessor) {
    Optional<String> token =
        Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER))
            .filter(header -> header.startsWith(BEARER_PREFIX))
            .map(header -> header.substring(BEARER_PREFIX.length()));
    if (token.isPresent()) {
      return token;
    }
    Object attr =
        accessor.getSessionAttributes() != null
            ? accessor.getSessionAttributes().get(AUTHORIZATION_HEADER)
            : null;
    return Optional.ofNullable(attr)
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .filter(header -> header.startsWith(BEARER_PREFIX))
        .map(header -> header.substring(BEARER_PREFIX.length()));
  }
}