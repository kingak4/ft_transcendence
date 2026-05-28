package code.chat.logic;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import code.chat.ports.in.GetChatMessagesUseCase;
import code.chat.ports.out.ChatDao;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChatMessages implements GetChatMessagesUseCase {
  private final ChatDao dao;

  @Override
  public List<Message> getChatMessages(ChatId chatId, int page, int size) {
    return dao.getRecentMessages(chatId, page, size);
  }
}
