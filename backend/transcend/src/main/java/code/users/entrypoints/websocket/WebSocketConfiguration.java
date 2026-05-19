package code.users.entrypoints.websocket;

import code.users.infrastructure.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

  private final JwtTokenService jwtTokenService;
  private final UserDetailsService userDetailsService;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.setApplicationDestinationPrefixes("/transcend");
    config.enableSimpleBroker("/topic", "/queue");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        new ChannelInterceptor() {

          @Override
          public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
              return message;
            }

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
              throw new AuthenticationCredentialsNotFoundException(
                  "No Bearer token found in STOMP headers");
            }

            String token = authHeader.substring(7);
            authenticateStompConnection(token, accessor);

            return message;
          }

          @Override
          public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
            SecurityContextHolder.clearContext();
          }
        });
  }

  private void authenticateStompConnection(String token, StompHeaderAccessor accessor) {
    try {
      String username = jwtTokenService.extractUsername(token);

      if (username == null) {
        throw new BadCredentialsException("Username extracted from JWT is null");
      }

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (!jwtTokenService.isTokenValid(token, userDetails)) {
        throw new BadCredentialsException("Invalid JWT Token");
      }

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      accessor.setUser(authentication);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      log.error("Failed to authenticate STOMP connection: {}", e.getMessage());
      throw new BadCredentialsException("Authentication failed", e);
    }
  }
}
