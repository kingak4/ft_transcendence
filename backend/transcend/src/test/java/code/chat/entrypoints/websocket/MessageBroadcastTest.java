package code.chat.entrypoints.websocket;

import static code.chat.entrypoints.websocket.ChatWebSocketConfig.chatMessagesTopic;
import static code.chat.entrypoints.websocket.ChatWebSocketController.MESSAGE_SEND;
import static code.shared.config.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.config.WebSocketConfig.SOCKET_PATH;
import static code.shared.config.WebSocketConfig.WS_HOST;
import static code.shared.util.WebSocketSecurityUtil.connectWithToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import code.chat.domain.model.ChatFixtures;
import code.chat.domain.model.MessageId;
import code.chat.ports.in.ManageMessagesUseCase;
import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import code.shared.domain.model.WebSocketFixtures;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketAutoConfig.class, ChatWebSocketController.class})
@Slf4j
class MessageBroadcastTest extends WebSocketTest {

  @LocalServerPort private int port;
  @MockitoBean private ManageMessagesUseCase manageMessagesUseCase;

  private StompSession observerOne;
  private StompSession observerTwo;
  private StompSession sender;

  @AfterEach
  void tearDown() {
    Mockito.reset(manageMessagesUseCase);
    disconnect(observerOne);
    disconnect(observerTwo);
    disconnect(sender);
  }

  @Test
  void shouldBroadcastMessageToAllSubscribersViaWebSocket() throws Exception {
    // Given
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    String chatId = ChatFixtures.CHAT_UUID_FIXTURE.toString();
    String messageContent = "Hello from WebSocket " + UUID.randomUUID();
    String topic = chatMessagesTopic(ChatFixtures.CHAT_UUID_FIXTURE);

    observerOne = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);
    observerTwo = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);
    sender = connectWithToken(stompClient, wsUrl, WebSocketFixtures.TOKEN_FIXTURE);

    MessageId expectedId = MessageId.generate();
    Mockito.when(manageMessagesUseCase.sendMessage(any()))
        .thenReturn(
            new ManageMessagesUseCase.SendMessageResponse(expectedId, OffsetDateTime.now()));

    BlockingQueue<ChatWebSocketController.ChatMessageResponse> observerOneEvents =
        subscribe(observerOne, topic);
    BlockingQueue<ChatWebSocketController.ChatMessageResponse> observerTwoEvents =
        subscribe(observerTwo, topic);
    BlockingQueue<ChatWebSocketController.ChatMessageResponse> senderEvents =
        subscribe(sender, topic);

    sendMessage(sender, chatId, messageContent);

    // When & Then
    await()
        .atMost(TIMEOUT)
        .pollInterval(Duration.ofMillis(500))
        .untilAsserted(
            () -> {
              assertThat(observerOneEvents)
                  .anySatisfy(
                      msg -> {
                        assertThat(msg.content()).isEqualTo(messageContent);
                        assertThat(msg.messageId()).isEqualTo(expectedId.val());
                      });

              assertThat(observerTwoEvents)
                  .anySatisfy(
                      msg -> {
                        assertThat(msg.content()).isEqualTo(messageContent);
                        assertThat(msg.messageId()).isEqualTo(expectedId.val());
                      });

              assertThat(senderEvents)
                  .anySatisfy(
                      msg -> {
                        assertThat(msg.content()).isEqualTo(messageContent);
                        assertThat(msg.messageId()).isEqualTo(expectedId.val());
                      });
            });

    verify(manageMessagesUseCase, atLeastOnce())
        .sendMessage(any(ManageMessagesUseCase.SendMessageCommand.class));
  }

  private void sendMessage(StompSession session, String chatId, String content) {
    session.send(
        SOCKET_PATH + MESSAGE_SEND.replace("{chatId}", chatId),
        new ChatWebSocketController.SendMessageRequest(content));
  }

  private BlockingQueue<ChatWebSocketController.ChatMessageResponse> subscribe(
      StompSession session, String destination) {
    BlockingQueue<ChatWebSocketController.ChatMessageResponse> queue = new LinkedBlockingQueue<>();
    session.subscribe(
        destination,
        new StompFrameHandler() {
          @Override
          public Type getPayloadType(StompHeaders headers) {
            return ChatWebSocketController.ChatMessageResponse.class;
          }

          @Override
          public void handleFrame(StompHeaders headers, Object payload) {
            log.info("Received message: {}", payload);
            queue.add((ChatWebSocketController.ChatMessageResponse) payload);
          }
        });
    return queue;
  }

  private void disconnect(StompSession session) {
    if (session != null && session.isConnected()) {
      session.disconnect();
    }
  }
}
