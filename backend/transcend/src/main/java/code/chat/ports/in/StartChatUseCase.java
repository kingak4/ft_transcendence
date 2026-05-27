package code.chat.ports.in;

import code.chat.domain.model.UserId;

public interface StartChatUseCase {
    void startChat(StartChatCommand command);

    record StartChatCommand(UserId initiator, UserId recipient) {}
}
