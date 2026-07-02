package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;
import java.time.OffsetDateTime;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ManageMessagesUseCase {
  @PreAuthorize(
      "hasRole(T(code.users.domain.model.Role).ADMIN.name) or @ownershipValidator.isSameUser(authentication, #command.sender())")
  SendMessageResponse sendMessage(SendMessageCommand command);

  @PreAuthorize(
      "hasRole(T(code.users.domain.model.Role).ADMIN.name) or @ownershipValidator.isSameUser(authentication, #command.sender())")
  void deleteMessage(DeleteMessageCommand command);

  record SendMessageResponse(MessageId id, OffsetDateTime createdAt) {}

  record SendMessageCommand(UserId sender, ChatId chatId, String content) {}

  record DeleteMessageCommand(UserId sender, MessageId messageId) {}
}
