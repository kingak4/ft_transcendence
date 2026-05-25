package code.users.entrypoints.websocket;

import static code.users.entrypoints.websocket.WebSocketConfiguration.SOCKET_ENDPOINT;
import static code.users.entrypoints.websocket.util.WebSocketSecurityUtil.connectWithToken;

import code.users.domain.model.UserFixtures;
import code.users.ports.in.UpdatePresenceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketTestAutoConfig.class, UserStatusWebSocketListener.class})
class UserStatusTest extends WebSocketTestAutoConfig {

  private static final int TIMEOUT_SECONDS = 10;

  @LocalServerPort private int port;

  @MockitoBean private UpdatePresenceUseCase updatePresenceUseCase;

  @Test
  void shouldReportUserOnlineAndOffline() throws Exception {
    String url = "ws://localhost:" + port + SOCKET_ENDPOINT;
    StompSession session = connectWithToken(stompClient, url, UserFixtures.TOKEN_FIXTURE);

    Mockito.verify(updatePresenceUseCase, Mockito.timeout(TIMEOUT_SECONDS * 1000L))
        .setUserOnline(Mockito.any(UpdatePresenceUseCase.SetUserOnlineCommand.class));

    session.disconnect();
    Mockito.verify(updatePresenceUseCase, Mockito.timeout(TIMEOUT_SECONDS * 1000L))
        .setUserOffline(Mockito.any(UpdatePresenceUseCase.SetUserOfflineCommand.class));
  }
}
