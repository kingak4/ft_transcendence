package code.users.entrypoints.websocket;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import code.users.ports.in.UpdateUserStatusUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserStatusWebSocketIntegrationTest.MinimalSocketWebConfig.class)
class UserStatusWebSocketIntegrationTest {

  @LocalServerPort private int port;

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
  @Import({WebSocketConfiguration.class, UserStatusWebSocketListener.class})
  static class MinimalSocketWebConfig {}

  @MockitoBean private UpdateUserStatusUseCase updateUserStatusUseCase;

  private WebSocketStompClient stompClient;

  @BeforeEach
  void setup() {
    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    SockJsClient sockJsClient = new SockJsClient(transports);

    stompClient = new WebSocketStompClient(sockJsClient);
    stompClient.setMessageConverter(new StringMessageConverter());
  }

  @Test
  void shouldReportUserOnlineAndOffline() throws Exception {
    String url = "ws://localhost:" + port + "/ws";

    StompHeaders connectHeaders = new StompHeaders();
    connectHeaders.add("username", "testuser");

    StompSession session =
        stompClient
            .connectAsync(
                url,
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {})
            .get(5, TimeUnit.SECONDS);

    // Verify the listener triggered the 'online' use case via the port mock
    verify(updateUserStatusUseCase, timeout(2000)).setUserOnline(eq("testuser"), anyString());

    // Disconnect to trigger 'offline' event
    String sessionId = session.getSessionId();
    session.disconnect();

    // Verify the listener triggered the 'offline' use case via the port mock
    // Connection disconnection event might take a split second
    verify(updateUserStatusUseCase, timeout(2000)).setUserOffline(anyString());
  }
}
