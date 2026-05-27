package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;

import java.util.List;

public interface GetChatsUseCase {

    List<ChatId> getChatList(UserId userId, int page, int size);
}
