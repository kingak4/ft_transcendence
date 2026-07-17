package code.users.entrypoints.websocket;

import static code.shared.config.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.config.WebSocketConfig.SOCKET_PATH;
import static code.shared.config.WebSocketConfig.WS_HOST;
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

import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.users.domain.model.UserId;
import code.users.ports.in.ReadPresenceUseCase;
import code.users.ports.in.UpdatePresenceUseCase;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
      WebSocketAutoConfig.class,
      PresenceWebSocketController.class,
      UserStatusWebSocketListener.class
    })
class PresenceFeatureTest extends WebSocketTest {

  @LocalServerPort private int port;

  @MockitoBean private ReadPresenceUseCase readPresenceUseCase;
  @MockitoBean private UpdatePresenceUseCase updatePresenceUseCase;

  @AfterEach
  void resetBusinessMocks() {
    Mockito.reset(readPresenceUseCase, updatePresenceUseCase);
  }

  @Test
  void shouldBroadcastPresenceEventsWhenUserConnectsAndDisconnects() throws Exception {
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    UserId userId = UserId.of(ID_FIXTURE);
    given(readPresenceUseCase.isOnline(userId)).willReturn(true);

    BlockingQueue<PresenceStatusResponse> events = new LinkedBlockingQueue<>();
    StompSession observerSession = null;
    StompSession actorSession = null;

    try {
      observerSession = connectWithToken(stompClient, wsUrl, TOKEN_FIXTURE);
      observerSession.setAutoReceipt(true);

      CompletableFuture<StompHeaders> receipt = new CompletableFuture<>();
      StompSession.Subscription sub =
          observerSession.subscribe(
              userPresenceTopic(userId.val()),
              new QueueingFrameHandler<>(PresenceStatusResponse.class, events));
      sub.addReceiptTask(() -> receipt.complete(new StompHeaders()));

      actorSession = connectWithToken(stompClient, wsUrl, TOKEN_FIXTURE);

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
      actorSession = null;

      // Then: Should receive Offline event
      await()
          .atMost(TIMEOUT)
          .untilAsserted(
              () -> {
                assertThat(events).anyMatch(res -> !res.isOnline());
              });

      // Verify mocks
      verify(updatePresenceUseCase, atLeastOnce()).setUserOnline(any());
      verify(updatePresenceUseCase, atLeastOnce()).setUserOffline(any());

    } finally {
      if (observerSession != null && observerSession.isConnected()) observerSession.disconnect();
      if (actorSession != null && actorSession.isConnected()) actorSession.disconnect();
    }
  }
}
