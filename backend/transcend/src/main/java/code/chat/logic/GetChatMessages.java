package code.chat.logic;

import code.chat.domain.exception.ChatNotFoundException;
import code.chat.domain.model.Chat;
import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import code.chat.ports.in.GetChatMessagesUseCase;
import code.chat.ports.out.ChatDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetChatMessages implements GetChatMessagesUseCase {
    private final ChatDao dao;

    @Override
    public List<Message> getChatMessages(ChatId chatId, int page, int size) {
        Optional<Chat> chat = dao.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException();
        // TODO validate if user in token belongs to the chat.
        return dao.getRecentMessages(chatId, page, size);
    }
}
