package code.users.entrypoints.websocket;

import static code.shared.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.WebSocketConfig.SOCKET_PATH;
import static code.shared.WebSocketConfig.WS_HOST;
import static code.shared.domain.model.WebSocketFixtures.ID_FIXTURE;
import static code.shared.domain.model.WebSocketFixtures.TOKEN_FIXTURE;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;
import static code.users.entrypoints.websocket.PresenceWebSocketController.CheckPresenceRequest;
import static code.users.entrypoints.websocket.PresenceWebSocketController.PRESENCE_CHECK;
import static code.users.entrypoints.websocket.PresenceWebSocketController.PresenceStatusResponse;
import static code.users.entrypoints.websocket.UserWebSocketConfig.userPresenceTopic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import code.shared.config.WebSocketTestAutoConfig;
import code.users.domain.model.UserId;
import code.users.ports.in.ReadPresenceUseCase;
import code.users.ports.in.UpdatePresenceUseCase;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
      WebSocketTestAutoConfig.class,
      PresenceWebSocketController.class,
      UserStatusWebSocketListener.class
    })
class PresenceFeatureTest extends WebSocketTestAutoConfig {

  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  @LocalServerPort private int port;

  @MockitoBean private ReadPresenceUseCase readPresenceUseCase;
  @MockitoBean private UpdatePresenceUseCase updatePresenceUseCase;

  @Test
  void shouldBroadcastPresenceEventsWhenUserConnectsAndDisconnects() throws Exception {
    // Given
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    UserId userId = UserId.of(ID_FIXTURE);
    given(readPresenceUseCase.isOnline(userId)).willReturn(true);

    BlockingQueue<PresenceStatusResponse> events = new LinkedBlockingQueue<>();

    StompSession observerSession = connectWithToken(stompClient, wsUrl, TOKEN_FIXTURE);
    observerSession.subscribe(
        userPresenceTopic(userId.val()),
        new QueueingFrameHandler<>(PresenceStatusResponse.class, events));

    StompSession actorSession = connectWithToken(stompClient, wsUrl, TOKEN_FIXTURE);

    // When
    actorSession.send(SOCKET_PATH + PRESENCE_CHECK, new CheckPresenceRequest(userId.val()));

    // Then: Should receive Online event
    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () -> {
              assertThat(events).anyMatch(PresenceStatusResponse::isOnline);
            });

    // When: Actor disconnects
    actorSession.disconnect();

    // Then: Should receive Offline event
    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () -> {
              assertThat(events).anyMatch(res -> !res.isOnline());
            });

    verify(updatePresenceUseCase, atLeastOnce())
        .setUserOnline(any(UpdatePresenceUseCase.SetUserOnlineCommand.class));
    verify(updatePresenceUseCase, atLeastOnce())
        .setUserOffline(any(UpdatePresenceUseCase.SetUserOfflineCommand.class));
    assertThat(events).extracting(PresenceStatusResponse::userId).containsOnly(ID_FIXTURE);

    observerSession.disconnect();
  }

  private record QueueingFrameHandler<T>(Class<T> payloadType, BlockingQueue<T> queue)
      implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
      return payloadType;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      queue.add((T) payload);
    }
  }
}
