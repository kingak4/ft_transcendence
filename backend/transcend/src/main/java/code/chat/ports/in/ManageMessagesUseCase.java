package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ManageMessagesUseCase {
  @PreAuthorize(
      "hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.sender())")
  void sendMessage(SendMessageCommand command);

  @PreAuthorize(
      "hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.sender())")
  void deleteMessage(DeleteMessageCommand command);

  record SendMessageCommand(UserId sender, ChatId chatId, String content) {}

  record DeleteMessageCommand(UserId sender, MessageId messageId) {}
}
