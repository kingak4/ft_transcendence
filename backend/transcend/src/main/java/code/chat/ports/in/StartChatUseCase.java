package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;

public interface StartChatUseCase {
    ChatId startChat(StartChatCommand command);

    record StartChatCommand(UserId initiator, UserId recipient) {}
}
