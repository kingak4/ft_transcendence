package code.chat.entrypoints.websocket;

import static code.chat.entrypoints.websocket.ChatWebSocketController.MESSAGE_DELETE;
import static code.chat.entrypoints.websocket.ChatWebSocketController.MESSAGE_SEND;
import static code.shared.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.WebSocketConfig.SOCKET_PATH;
import static code.shared.WebSocketConfig.WS_HOST;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import code.chat.domain.model.ChatFixtures;
import code.chat.entrypoints.websocket.ChatWebSocketController.DeleteMessageRequest;
import code.chat.entrypoints.websocket.ChatWebSocketController.SendMessageRequest;
import code.chat.ports.in.ManageMessagesUseCase;
import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.shared.domain.model.WebSocketFixtures;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketAutoConfig.class, ChatWebSocketController.class})
class ChatWebSocketControllerTest extends WebSocketTest {

  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  @LocalServerPort private int port;

  @MockitoBean private ManageMessagesUseCase manageMessagesUseCase;

  @Test
  void shouldDeleteMessageViaWebSocket() throws Exception {
    // Given
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    String messageId = ChatFixtures.MESSAGE_ID_FIXTURE.toString();

    doNothing()
        .when(manageMessagesUseCase)
        .deleteMessage(any(ManageMessagesUseCase.DeleteMessageCommand.class));

    StompSession session = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);

    // When
    deleteMessage(session, messageId);

    // Then
    verify(manageMessagesUseCase, timeout(TIMEOUT.toMillis()))
        .deleteMessage(any(ManageMessagesUseCase.DeleteMessageCommand.class));

    session.disconnect();
  }

  @Test
  void shouldSendMessageViaWebSocket() throws Exception {
    // Given
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    String chatId = ChatFixtures.CHAT_ID_FIXTURE.toString();
    String messageContent = "Hello from WebSocket";

    doNothing()
        .when(manageMessagesUseCase)
        .sendMessage(any(ManageMessagesUseCase.SendMessageCommand.class));

    StompSession session = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);

    // When
    sendMessage(session, chatId, messageContent);

    // Then
    verify(manageMessagesUseCase, timeout(TIMEOUT.toMillis()))
        .sendMessage(any(ManageMessagesUseCase.SendMessageCommand.class));

    session.disconnect();
  }

  private void sendMessage(StompSession session, String chatId, String content) {
    session.send(
        SOCKET_PATH + MESSAGE_SEND.replace("{chatId}", chatId), new SendMessageRequest(content));
  }

  private void deleteMessage(StompSession session, String messageId) {
    session.send(
        SOCKET_PATH + MESSAGE_DELETE.replace("{messageId}", messageId), new DeleteMessageRequest());
  }
}
