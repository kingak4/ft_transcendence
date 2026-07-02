package code.chat.entrypoints.websocket;

import static code.chat.entrypoints.websocket.ChatWebSocketConfig.CHAT_MESSAGES_TOPIC;
import static code.chat.entrypoints.websocket.ChatWebSocketConfig.chatMessagesTopic;

import io.github.springwolf.bindings.stomp.annotations.StompAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatEventListener {
  private final SimpMessagingTemplate messagingTemplate;

  @EventListener
  @AsyncPublisher(
      operation =
          @AsyncOperation(
              channelName = CHAT_MESSAGES_TOPIC,
              description = "Broadcasts new messages to chat subscribers",
              payloadType = SendMessageEventResponse.class))
  @StompAsyncOperationBinding
  public void handleMessageSent(MessageSentEvent event) {
    messagingTemplate.convertAndSend(
        chatMessagesTopic(event.chatId().val()),
        new SendMessageEventResponse(
            event.senderId().val(), event.messageId().val(), event.content(), event.time()));
  }

  @EventListener
  @AsyncPublisher(
      operation =
          @AsyncOperation(
              channelName = CHAT_MESSAGES_TOPIC,
              description = "Broadcasts message deletion notification to chat subscribers",
              payloadType = DeleteMessageEventResponse.class))
  @StompAsyncOperationBinding
  public void handleMessageDeleted(MessageSentEvent event) {
    messagingTemplate.convertAndSend(
        chatMessagesTopic(event.chatId().val()),
        new DeleteMessageEventResponse(event.senderId().val(), event.messageId().val()));
  }

  @Schema(description = "Response containing a message sent in a chat")
  record SendMessageEventResponse(
      UUID senderId, UUID messageId, String content, OffsetDateTime time) {}

  @Schema(description = "Response containing an id of the deleted message")
  record DeleteMessageEventResponse(UUID senderId, UUID messageId) {}
}
