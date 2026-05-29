package code.users.entrypoints.websocket;

import static code.shared.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.WebSocketConfig.WS_HOST;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;

import code.shared.config.WebSocketTestAutoConfig;
import code.shared.domain.model.WebSocketFixtures;
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
    String url = WS_HOST + port + SOCKET_ENDPOINT;
    StompSession session = connectWithToken(stompClient, url, WebSocketFixtures.TOKEN_FIXTURE);

    Mockito.verify(updatePresenceUseCase, Mockito.timeout(TIMEOUT_SECONDS * 1000L))
        .setUserOnline(Mockito.any(UpdatePresenceUseCase.SetUserOnlineCommand.class));

    session.disconnect();
    Mockito.verify(updatePresenceUseCase, Mockito.timeout(TIMEOUT_SECONDS * 1000L))
        .setUserOffline(Mockito.any(UpdatePresenceUseCase.SetUserOfflineCommand.class));
  }
}
