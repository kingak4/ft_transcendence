package code.shared.util;

import static code.shared.config.WebSocketTest.TIMEOUT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import code.users.domain.model.Role;
import code.users.infrastructure.security.JwtTokenService;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class WebSocketSecurityUtil {

  public static void mockAuth(
      JwtTokenService jwtTokenService,
      UserDetailsService userDetailsService,
      String token,
      UUID userId,
      String password) {

    String username = userId.toString();
    UserDetails user =
        User.withUsername(username).password(password).authorities(Role.USER.toString()).build();

    lenient().when(jwtTokenService.extractUsername(token)).thenReturn(username);
    lenient().when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
    lenient()
        .when(jwtTokenService.isTokenValid(eq(token), any(UserDetails.class)))
        .thenReturn(true);

    lenient()
        .when(jwtTokenService.buildAuthentication(any(UserDetails.class)))
        .thenAnswer(
            invocation -> {
              UserDetails ud = invocation.getArgument(0);
              return new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
            });
  }

  public static WebSocketStompClient createStompClient() {
    List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
    SockJsClient sockJsClient = new SockJsClient(transports);

    WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(1);
    taskScheduler.setThreadNamePrefix("stomp-scheduler-");
    taskScheduler.afterPropertiesSet();
    stompClient.setTaskScheduler(taskScheduler);

    return stompClient;
  }

  public static StompSession connectWithToken(
      WebSocketStompClient stompClient, String url, String token) throws Exception {

    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    handshakeHeaders.add("Authorization", "Bearer " + token);

    StompHeaders connectHeaders = new StompHeaders();
    connectHeaders.add("Authorization", "Bearer " + token);

    return stompClient
        .connectAsync(
            url,
            handshakeHeaders,
            connectHeaders,
            new StompSessionHandlerAdapter() {
              @Override
              public void handleException(
                  StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
                System.err.println("STOMP Session Error: " + ex.getMessage());
              }

              @Override
              public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received frame: " + headers.getDestination());
              }
            })
        .get(TIMEOUT, TimeUnit.SECONDS);
  }
}
