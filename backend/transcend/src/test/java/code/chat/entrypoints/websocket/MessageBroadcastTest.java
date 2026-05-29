package code.chat.entrypoints.websocket;

import static code.chat.entrypoints.websocket.ChatWebSocketConfig.chatMessagesTopic;
import static code.chat.entrypoints.websocket.ChatWebSocketController.MESSAGE_SEND;
import static code.shared.WebSocketConfig.SOCKET_ENDPOINT;
import static code.shared.WebSocketConfig.SOCKET_PATH;
import static code.shared.WebSocketConfig.WS_HOST;
import static code.users.entrypoints.websocket.util.WebSocketSecurityUtil.connectWithToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import code.chat.domain.model.ChatFixtures;
import code.chat.ports.in.ManageMessagesUseCase;
import code.users.domain.model.UserFixtures;
import code.users.entrypoints.websocket.WebSocketTestAutoConfig;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketTestAutoConfig.class, ChatWebSocketController.class})
class MessageBroadcastTest extends WebSocketTestAutoConfig {

  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  @LocalServerPort private int port;

  @MockitoBean private ManageMessagesUseCase manageMessagesUseCase;

  @Test
  void shouldBroadcastMessageToAllSubscribersViaWebSocket() throws Exception {
    // Given
    String wsUrl = WS_HOST + port + SOCKET_ENDPOINT;
    String chatId = ChatFixtures.CHAT_ID_FIXTURE.toString();
    String messageContent = "Hello from WebSocket";

    doNothing()
        .when(manageMessagesUseCase)
        .sendMessage(any(ManageMessagesUseCase.SendMessageCommand.class));

    BlockingQueue<ChatWebSocketController.ChatMessageResponse> observerOneEvents =
        new LinkedBlockingQueue<>();
    BlockingQueue<ChatWebSocketController.ChatMessageResponse> observerTwoEvents =
        new LinkedBlockingQueue<>();
    BlockingQueue<ChatWebSocketController.ChatMessageResponse> senderEvents =
        new LinkedBlockingQueue<>();

    StompSession observerOne = connectWithToken(stompClient, wsUrl, ChatFixtures.TOKEN_FIXTURE);
    StompSession observerTwo = connectWithToken(stompClient, wsUrl, ChatFixtures.TOKEN_FIXTURE);
    StompSession sender = connectWithToken(stompClient, wsUrl, ChatFixtures.TOKEN_FIXTURE);

    observerOne.subscribe(
        chatMessagesTopic(ChatFixtures.CHAT_ID_FIXTURE),
        new QueueingFrameHandler<>(
            ChatWebSocketController.ChatMessageResponse.class, observerOneEvents));
    observerTwo.subscribe(
        chatMessagesTopic(ChatFixtures.CHAT_ID_FIXTURE),
        new QueueingFrameHandler<>(
            ChatWebSocketController.ChatMessageResponse.class, observerTwoEvents));
    sender.subscribe(
        chatMessagesTopic(ChatFixtures.CHAT_ID_FIXTURE),
        new QueueingFrameHandler<>(
            ChatWebSocketController.ChatMessageResponse.class, senderEvents));

    // When
    sendMessage(sender, chatId, messageContent);

    // Then
    verify(manageMessagesUseCase, timeout(TIMEOUT.toMillis()))
        .sendMessage(any(ManageMessagesUseCase.SendMessageCommand.class));

    ChatWebSocketController.ChatMessageResponse observerOneMessage =
        observerOneEvents.poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
    ChatWebSocketController.ChatMessageResponse observerTwoMessage =
        observerTwoEvents.poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
    ChatWebSocketController.ChatMessageResponse senderMessage =
        senderEvents.poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

    assertThat(observerOneMessage).isNotNull();
    assertThat(observerTwoMessage).isNotNull();
    assertThat(senderMessage).isNotNull();
    assertThat(observerOneMessage).isEqualTo(senderMessage);
    assertThat(observerTwoMessage).isEqualTo(senderMessage);
    assertThat(observerOneMessage.chatId()).isEqualTo(ChatFixtures.CHAT_ID_FIXTURE);
    assertThat(observerOneMessage.senderId()).isEqualTo(UserFixtures.ID_FIXTURE);
    assertThat(observerOneMessage.content()).isEqualTo(messageContent);

    sender.disconnect();
    observerTwo.disconnect();
    observerOne.disconnect();
  }

  private void sendMessage(StompSession session, String chatId, String content) {
    session.send(
        SOCKET_PATH + MESSAGE_SEND.replace("{chatId}", chatId),
        new ChatWebSocketController.SendMessageRequest(content));
  }

  private record QueueingFrameHandler<T>(Class<T> payloadType, BlockingQueue<T> queue)
      implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
      return payloadType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleFrame(StompHeaders headers, Object payload) {
      queue.add((T) payload);
    }
  }
}
