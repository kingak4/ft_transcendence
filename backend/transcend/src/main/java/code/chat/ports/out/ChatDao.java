package code.chat.ports.out;

import code.chat.domain.model.Chat;
import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;

import java.util.Optional;

public interface ChatDao {
    Optional<ChatId> findChat(UserId initiator, UserId recipient);

    ChatId createChat(UserId initiator, UserId recipient);
}
