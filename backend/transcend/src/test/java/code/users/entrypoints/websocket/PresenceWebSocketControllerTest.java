package code.users.entrypoints.websocket;

import static code.shared.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.WebSocketConfig.SOCKET_PATH;
import static code.shared.WebSocketConfig.WS_HOST;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;
import static code.users.entrypoints.websocket.PresenceWebSocketController.PRESENCE_CHECK;
import static code.users.entrypoints.websocket.UserWebSocketConfig.userPresenceTopic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.shared.domain.model.WebSocketFixtures;
import code.users.domain.model.UserId;
import code.users.ports.in.ReadPresenceUseCase;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketAutoConfig.class, PresenceWebSocketController.class})
class PresenceWebSocketControllerTest extends WebSocketTest {

  private static final int TIMEOUT_SECONDS = 5;

  @LocalServerPort private int port;

  @MockitoBean private ReadPresenceUseCase readPresenceUseCase;

  @Test
  void shouldReturnPresenceStatusWhenChecked() throws Exception {
    // Given
    UserId userId = UserId.of(WebSocketFixtures.ID_FIXTURE);
    given(readPresenceUseCase.isOnline(userId)).willReturn(true);

    session =
        connectWithToken(
            stompClient, WS_HOST + port + SOCKET_ENDPOINT, WebSocketFixtures.TOKEN_FIXTURE);
    CompletableFuture<PresenceWebSocketController.PresenceStatusResponse> resultKeeper =
        subscribe(
            userPresenceTopic(userId.val()),
            PresenceWebSocketController.PresenceStatusResponse.class);

    // When
    sendPresenceCheck(userId);

    // Then
    var response = resultKeeper.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertThat(response.userId()).isEqualTo(WebSocketFixtures.ID_FIXTURE);
    assertThat(response.isOnline()).isTrue();
    verify(readPresenceUseCase).isOnline(userId);
  }

  private <T> CompletableFuture<T> subscribe(String destination, Class<T> payloadType) {
    CompletableFuture<T> future = new CompletableFuture<>();
    session.subscribe(
        destination,
        new StompFrameHandler() {
          @Override
          public Type getPayloadType(StompHeaders headers) {
            return payloadType;
          }

          @Override
          public void handleFrame(StompHeaders headers, Object payload) {
            future.complete((T) payload);
          }
        });
    return future;
  }

  private void sendPresenceCheck(UserId userId) {
    StompHeaders headers = new StompHeaders();
    headers.setDestination(SOCKET_PATH + PRESENCE_CHECK);
    headers.add("userId", userId.toString());
    session.send(headers, new PresenceWebSocketController.CheckPresenceRequest(userId.val()));
  }
}
