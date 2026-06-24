package code.users.entrypoints.websocket;

import static code.bootstrap.config.TokenConfig.AUTHORIZATION_HEADER;
import static code.bootstrap.config.TokenConfig.BEARER_PREFIX;
import static code.shared.config.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.config.WebSocketConfig.WS_HOST;
import static code.shared.domain.model.WebSocketFixtures.TOKEN_FIXTURE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.users.ports.in.ReadPresenceUseCase;
import code.users.ports.in.UpdatePresenceUseCase;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketHttpHeaders;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
      WebSocketAutoConfig.class,
      PresenceWebSocketController.class,
      UserStatusWebSocketListener.class
    })
class BffAuthenticationTest extends WebSocketTest {

  @LocalServerPort private int port;

  @MockitoBean private ReadPresenceUseCase readPresenceUseCase;
  @MockitoBean private UpdatePresenceUseCase updatePresenceUseCase;

  @AfterEach
  void resetBusinessMocks() {
    Mockito.reset(readPresenceUseCase, updatePresenceUseCase);
  }

  @Test
  void shouldAuthenticateWhenTokenIsOnlyInHandshakeHeaders() throws Exception {
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    handshakeHeaders.add(AUTHORIZATION_HEADER, BEARER_PREFIX + TOKEN_FIXTURE);
    StompHeaders stompHeaders = new StompHeaders();

    CompletableFuture<StompSession> completableFuture =
        stompClient.connectAsync(
            wsUrl, handshakeHeaders, stompHeaders, new StompSessionHandlerAdapter() {});

    await().atMost(TIMEOUT).until(completableFuture::isDone);
    session = completableFuture.get();

    assertThat(session.isConnected()).isTrue();
  }

  @Test
  void shouldAuthenticateWhenTokenIsOnlyInStompHeaders() throws Exception {
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    StompHeaders stompHeaders = new StompHeaders();
    stompHeaders.add(AUTHORIZATION_HEADER, BEARER_PREFIX + TOKEN_FIXTURE);

    CompletableFuture<StompSession> completableFuture =
        stompClient.connectAsync(
            wsUrl, handshakeHeaders, stompHeaders, new StompSessionHandlerAdapter() {});

    await().atMost(TIMEOUT).until(completableFuture::isDone);
    session = completableFuture.get();

    assertThat(session.isConnected()).isTrue();
  }

  @Test
  void shouldFailToConnectWhenNoTokenIsProvided() {
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    StompHeaders stompHeaders = new StompHeaders();

    assertThatThrownBy(
            () -> {
              stompClient
                  .connectAsync(
                      wsUrl, handshakeHeaders, stompHeaders, new StompSessionHandlerAdapter() {})
                  .get(TIMEOUT.toSeconds(), SECONDS);
            })
        .describedAs("Connection should be refused or timeout due to lack of auth");
  }
}
