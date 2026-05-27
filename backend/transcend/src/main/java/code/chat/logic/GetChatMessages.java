package code.chat.logic;

import code.chat.domain.model.Message;
import code.chat.ports.in.GetChatMessagesUseCase;

import java.util.List;

public class GetChatMessages implements GetChatMessagesUseCase {
    @Override
    public List<Message> getChatMessages(GetMessagesRequest request) {
        return List.of();
    }
}
