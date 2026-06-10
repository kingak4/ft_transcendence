package code.users.entrypoints.websocket;

import static code.shared.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.WebSocketConfig.SOCKET_PATH;
import static code.shared.WebSocketConfig.SOCKET_QUEUE;
import static code.shared.WebSocketConfig.SOCKET_TOPIC;

import code.users.infrastructure.security.config.HandshakeJwtInterceptor;
import code.users.infrastructure.security.config.SocketJwtInterceptor;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class UserWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final SocketJwtInterceptor jwtInterceptor;
  private final HandshakeJwtInterceptor handshakeInterceptor;

  public static final String USER_PRESENCE_TOPIC_PREFIX = SOCKET_TOPIC + "/user/";
  public static final String USER_PRESENCE_TOPIC_SUFFIX = "/presence";

  public static String userPresenceTopic(UUID userId) {
    return USER_PRESENCE_TOPIC_PREFIX + userId + USER_PRESENCE_TOPIC_SUFFIX;
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(SOCKET_ENDPOINT)
        .addInterceptors(handshakeInterceptor)
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.setApplicationDestinationPrefixes(SOCKET_PATH);
    config.enableSimpleBroker(SOCKET_TOPIC, SOCKET_QUEUE);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(jwtInterceptor);
  }
}