package code.chat.infrastructure.persistence;

import code.chat.domain.model.*;
import code.chat.ports.out.ChatDao;
import code.shared.exceptions.NotImplementedException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepository implements ChatDao {
  @Override
  public Optional<ChatId> findChat(UserId initiator, UserId recipient) {
    throw new NotImplementedException();
  }

  @Override
  public List<ChatId> getChatList(UserId userId, int page, int size) {
    throw new NotImplementedException();
  }

  @Override
  public Optional<Chat> getChat(ChatId chatId) {
    throw new NotImplementedException();
  }

  @Override
  public List<Message> getRecentMessages(ChatId chatId, int page, int size) {
    throw new NotImplementedException();
  }

  @Override
  public ChatId createChat(Chat chat) {
    throw new NotImplementedException();
  }

  @Override
  public void saveMessage(Message message) {
    throw new NotImplementedException();
  }

  @Override
  public Optional<Message> getMessage(MessageId id) {
    throw new NotImplementedException();
  }

  @Override
  public void deleteMessage(MessageId messageId) {
    throw new NotImplementedException();
  }
}
