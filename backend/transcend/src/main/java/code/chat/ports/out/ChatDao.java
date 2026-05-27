package code.chat.ports.out;

import code.chat.domain.model.*;

import java.util.List;
import java.util.Optional;

public interface ChatDao {
    Optional<ChatId> findChat(UserId initiator, UserId recipient);

    List<ChatId> getChatList(UserId userId, int page, int size);

    Optional<Chat> getChat(ChatId chatId);

    List<Message> getRecentMessages(ChatId chatId, int page, int size);

    ChatId createChat(Chat chat);

    void saveMessage(Message message);

    Optional<Message> getMessage(MessageId id);

    void deleteMessage(MessageId messageId);
}
