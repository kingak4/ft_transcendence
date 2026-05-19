package code.users.entrypoints.websocket;

import static code.users.entrypoints.websocket.WebSocketConfiguration.SOCKET_ENDPOINT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import code.users.domain.model.UserFixtures;
import code.users.infrastructure.security.JwtTokenService;
import code.users.infrastructure.security.config.SocketJwtInterceptor;
import code.users.ports.in.UpdateUserStatusUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserStatusTest.MinimalSocketWebConfig.class)
class UserStatusTest {

  @LocalServerPort private int port;
  private WebSocketStompClient stompClient;

  @Configuration
  @ImportAutoConfiguration({
    ServletWebServerFactoryAutoConfiguration.class,
    DispatcherServletAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    JacksonAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class,
    WebSocketMessagingAutoConfiguration.class,
    WebSocketServletAutoConfiguration.class
  })
  @Import({
    WebSocketConfiguration.class,
    UserStatusWebSocketListener.class,
    SocketJwtInterceptor.class
  })
  static class MinimalSocketWebConfig {}

  @MockitoBean private UpdateUserStatusUseCase updateUserStatusUseCase;
  @MockitoBean private JwtTokenService jwtTokenService;
  @MockitoBean private UserDetailsService userDetailsService;

  @BeforeEach
  void setup() {
    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    SockJsClient sockJsClient = new SockJsClient(transports);

    stompClient = new WebSocketStompClient(sockJsClient);
    stompClient.setMessageConverter(new StringMessageConverter());

    UserDetails userDetails =
        User.withUsername(UserFixtures.NAME_FIXTURE)
            .password(UserFixtures.PASSWORD_FIXTURE)
            .build();

    when(jwtTokenService.extractUsername(UserFixtures.TOKEN_FIXTURE))
        .thenReturn(UserFixtures.NAME_FIXTURE);
    when(userDetailsService.loadUserByUsername(UserFixtures.NAME_FIXTURE)).thenReturn(userDetails);
    when(jwtTokenService.isTokenValid(
            eq(UserFixtures.TOKEN_FIXTURE), Mockito.any(UserDetails.class)))
        .thenReturn(true);
  }

  @Test
  void shouldReportUserOnlineAndOffline() throws Exception {
    String url = "ws://localhost:" + port + SOCKET_ENDPOINT;
    StompHeaders connectHeaders = new StompHeaders();
    connectHeaders.add("Authorization", "Bearer " + UserFixtures.TOKEN_FIXTURE);

    StompSession session =
        stompClient
            .connectAsync(
                url,
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {})
            .get(5, TimeUnit.SECONDS);

    Mockito.verify(updateUserStatusUseCase, Mockito.timeout(2000))
        .setUserOnline(eq(UserFixtures.NAME_FIXTURE), anyString());

    session.disconnect();
    Mockito.verify(updateUserStatusUseCase, Mockito.timeout(2000)).setUserOffline(anyString());
  }
}