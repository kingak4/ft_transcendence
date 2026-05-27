package code.chat.logic;

import code.chat.domain.model.Message;
import code.chat.ports.in.GetChatMessagesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetChatMessages implements GetChatMessagesUseCase {
    @Override
    public List<Message> getChatMessages(GetMessagesRequest request) {
        return List.of();
    }
}
