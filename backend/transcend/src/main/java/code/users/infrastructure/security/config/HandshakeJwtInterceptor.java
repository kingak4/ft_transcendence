package code.users.infrastructure.security.config;

import static code.bootstrap.config.TokenConfig.AUTHORIZATION_HEADER;
import static code.bootstrap.config.TokenConfig.BEARER_PREFIX;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandshakeJwtInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {
    var authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      attributes.put(AUTHORIZATION_HEADER, authHeader);
    }
    return true;
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception) {
  }
}