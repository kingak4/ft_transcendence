package code.chat.ports.in;

import code.chat.domain.model.UserId;

public interface GetChatsUseCase {
    void getChatList(UserId userId);
}
