package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;

import java.util.List;

public interface GetChatMessagesUseCase {
    List<Message> getChatMessages(GetMessagesRequest request);

    record GetMessagesRequest(ChatId chatId, int page, int size) { }
}
