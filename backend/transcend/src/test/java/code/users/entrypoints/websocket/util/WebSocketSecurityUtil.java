package code.users.entrypoints.websocket.util;

import static code.bootstrap.config.TokenConfig.AUTHORIZATION_HEADER;
import static code.bootstrap.config.TokenConfig.BEARER_PREFIX;
import static org.mockito.Mockito.when;

import code.users.infrastructure.security.JwtTokenService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.NoArgsConstructor;
import org.mockito.ArgumentMatchers;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@NoArgsConstructor
public final class WebSocketSecurityUtil {

  public static void mockAuth(
      JwtTokenService jwtTokenService,
      UserDetailsService userDetailsService,
      String token,
      UUID userId,
      String password) {
    String username = userId.toString();
    UserDetails user = User.withUsername(username).password(password).build();

    when(jwtTokenService.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
    when(jwtTokenService.isTokenValid(
            ArgumentMatchers.eq(token), ArgumentMatchers.any(UserDetails.class)))
        .thenReturn(true);
  }

  public static WebSocketStompClient createStompClient() {
    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    SockJsClient sockJsClient = new SockJsClient(transports);

    return new WebSocketStompClient(sockJsClient);
  }

  public static StompSession connectWithToken(
      WebSocketStompClient stompClient, String url, String token)
      throws java.util.concurrent.ExecutionException,
          InterruptedException,
          java.util.concurrent.TimeoutException {
    StompHeaders connectHeaders = new StompHeaders();
    connectHeaders.add(AUTHORIZATION_HEADER, BEARER_PREFIX + token);

    return stompClient
        .connectAsync(
            url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {})
        .get(5, TimeUnit.SECONDS);
  }
}
