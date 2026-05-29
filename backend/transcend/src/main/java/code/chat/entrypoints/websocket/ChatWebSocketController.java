package code.chat.entrypoints.websocket;

import static code.chat.entrypoints.websocket.ChatWebSocketConfig.chatMessagesTopic;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;
import code.chat.ports.in.ManageMessagesUseCase;
import code.chat.ports.in.ManageMessagesUseCase.DeleteMessageCommand;
import code.chat.ports.in.ManageMessagesUseCase.SendMessageCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final ManageMessagesUseCase manageMessagesUseCase;
  private final SimpMessagingTemplate messagingTemplate;
  public static final String MESSAGE_SEND = "/chat/{chatId}/send";
  public static final String MESSAGE_DELETE = "/chat/messages/{messageId}/delete";

  @MessageMapping(MESSAGE_SEND)
  public void sendMessage(
      @DestinationVariable UUID chatId,
      @Payload SendMessageRequest request,
      Authentication authentication) {

    UserId sender = UserId.of(UUID.fromString(authentication.getName()));
    ChatId chat = ChatId.of(chatId);

    var command = new SendMessageCommand(sender, chat, request.content());

    manageMessagesUseCase.sendMessage(command);
    messagingTemplate.convertAndSend(
        chatMessagesTopic(chat.val()),
        new ChatMessageResponse(chat.val(), sender.val(), request.content()));
  }

  @MessageMapping(MESSAGE_DELETE)
  public void deleteMessage(@DestinationVariable UUID messageId, Authentication authentication) {

    UserId sender = UserId.of(UUID.fromString(authentication.getName()));
    MessageId message = MessageId.of(messageId);

    var command = new DeleteMessageCommand(sender, message);
    manageMessagesUseCase.deleteMessage(command);
  }

  public record SendMessageRequest(String content) {}

  public record ChatMessageResponse(UUID chatId, UUID senderId, String content) {}
}
