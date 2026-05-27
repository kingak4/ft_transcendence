package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;

import java.util.List;
import java.util.Map;

public interface GetChatMessagesUseCase {
    List<Message> getChatMessages(GetMessagesRequest request);

    record GetMessagesRequest(ChatId chatId, int page, int size) { }
}
