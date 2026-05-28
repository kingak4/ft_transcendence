package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GetChatMessagesUseCase {
  @PreAuthorize("hasRole('ADMIN') or @chatValidator.isMember(authentication, #chatId)")
  List<Message> getChatMessages(ChatId chatId, int page, int size);
}
