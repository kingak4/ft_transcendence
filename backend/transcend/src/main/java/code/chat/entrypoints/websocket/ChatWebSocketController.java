package code.chat.entrypoints.websocket;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;
import code.chat.ports.in.ManageMessagesUseCase;
import code.chat.ports.in.ManageMessagesUseCase.DeleteMessageCommand;
import code.chat.ports.in.ManageMessagesUseCase.SendMessageCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

  private final ApplicationEventPublisher eventPublisher;
  private final ManageMessagesUseCase manageMessagesUseCase;
  private final SimpMessagingTemplate messagingTemplate;
  public static final String MESSAGE_SEND = "/chat/{chatId}/send";
  public static final String MESSAGE_DELETE = "/chat/{chatId}/messages/{messageId}/delete";

  @MessageMapping(MESSAGE_SEND)
  @Operation(
      summary = "Send a message in a chat",
      description = "The message is broadcast to all subscribers of that chat topic.")
  public void sendMessage(
      @DestinationVariable UUID chatId,
      @Payload SendMessageRequest request,
      Authentication authentication) {

    UserId sender = UserId.of(UUID.fromString(authentication.getName()));
    ChatId chat = ChatId.of(chatId);

    var command = new SendMessageCommand(sender, chat, request.content());

    ManageMessagesUseCase.SendMessageResponse response = manageMessagesUseCase.sendMessage(command);

    eventPublisher.publishEvent(
        new MessageSentEvent(chat, sender, response.id(), request.content(), response.createdAt()));
  }

  @MessageMapping(MESSAGE_DELETE)
  @Operation(
      summary = "Delete a message from a chat",
      description = "Only the message sender can delete their own messages.")
  public void deleteMessage(
      @DestinationVariable UUID chatId,
      @DestinationVariable UUID messageId,
      @Payload DeleteMessageRequest request,
      Authentication authentication) {

    UserId sender = UserId.of(UUID.fromString(authentication.getName()));
    ChatId chat = ChatId.of(chatId);
    MessageId message = MessageId.of(messageId);

    var command = new DeleteMessageCommand(sender, message);
    manageMessagesUseCase.deleteMessage(command);

    eventPublisher.publishEvent(new MessageDeletedEvent(chat, sender, message));
  }

  @Schema(description = "Request to send a message in a chat")
  public record SendMessageRequest(
      @Schema(description = "The content of the message", example = "Hello, world!")
          String content) {}

  @Schema(description = "Request to delete a message (empty payload)")
  public record DeleteMessageRequest() {}
}
