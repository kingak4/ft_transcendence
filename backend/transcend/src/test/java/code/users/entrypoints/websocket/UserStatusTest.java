package code.users.entrypoints.websocket;

import static code.shared.config.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.config.WebSocketConfig.WS_HOST;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.shared.domain.model.WebSocketFixtures;
import code.users.ports.in.UpdatePresenceUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketAutoConfig.class, UserStatusWebSocketListener.class})
class UserStatusTest extends WebSocketTest {

  @LocalServerPort private int port;

  @MockitoBean private UpdatePresenceUseCase updatePresenceUseCase;

  @AfterEach
  void resetBusinessMocks() {
    Mockito.reset(updatePresenceUseCase);
  }

  @Test
  void shouldReportUserOnlineAndOffline() throws Exception {
    String url = WS_HOST + port + SOCKET_ENDPOINT;
    session = connectWithToken(stompClient, url, WebSocketFixtures.TOKEN_FIXTURE);

    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () ->
                verify(updatePresenceUseCase)
                    .setUserOnline(any(UpdatePresenceUseCase.SetUserOnlineCommand.class)));

    session.disconnect();
    session = null;

    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () ->
                verify(updatePresenceUseCase)
                    .setUserOffline(any(UpdatePresenceUseCase.SetUserOfflineCommand.class)));
  }
}
