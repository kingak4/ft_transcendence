package code.chat.entrypoints.websocket;

import static code.chat.entrypoints.websocket.ChatWebSocketController.MESSAGE_DELETE;
import static code.chat.entrypoints.websocket.ChatWebSocketController.MESSAGE_SEND;
import static code.shared.config.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.config.WebSocketConfig.SOCKET_PATH;
import static code.shared.config.WebSocketConfig.WS_HOST;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import code.chat.domain.model.ChatFixtures;
import code.chat.domain.model.MessageId;
import code.chat.entrypoints.websocket.ChatWebSocketController.DeleteMessageRequest;
import code.chat.entrypoints.websocket.ChatWebSocketController.SendMessageRequest;
import code.chat.ports.in.ManageMessagesUseCase;
import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.shared.domain.model.WebSocketFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketAutoConfig.class, ChatWebSocketController.class})
class ChatWebSocketControllerTest extends WebSocketTest {

  @LocalServerPort private int port;

  @MockitoBean private ManageMessagesUseCase manageMessagesUseCase;

  @Test
  void shouldDeleteMessageViaWebSocket() throws Exception {
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    String chatId = ChatFixtures.CHAT_UUID_FIXTURE.toString();
    String messageId = MessageId.generate().val().toString();

    session = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);
    deleteMessage(session, chatId, messageId);

    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () ->
                verify(manageMessagesUseCase)
                    .deleteMessage(any(ManageMessagesUseCase.DeleteMessageCommand.class)));
  }

  @Test
  void shouldSendMessageViaWebSocket() throws Exception {
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    String chatId = ChatFixtures.CHAT_UUID_FIXTURE.toString();
    String messageContent = "Hello from WebSocket";

    session = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);
    sendMessage(session, chatId, messageContent);

    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () ->
                verify(manageMessagesUseCase)
                    .sendMessage(any(ManageMessagesUseCase.SendMessageCommand.class)));
  }

  private void sendMessage(StompSession session, String chatId, String content) {
    session.send(
        SOCKET_PATH + MESSAGE_SEND.replace("{chatId}", chatId), new SendMessageRequest(content));
  }

  private void deleteMessage(StompSession session, String chatId, String messageId) {
    session.send(
        SOCKET_PATH + MESSAGE_DELETE.replace("{chatId}", chatId).replace("{messageId}", messageId),
        new DeleteMessageRequest());
  }
}
