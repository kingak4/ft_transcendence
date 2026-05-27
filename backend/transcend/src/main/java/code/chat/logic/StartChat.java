package code.chat.logic;

import code.chat.domain.model.ChatId;
import code.chat.ports.in.StartChatUseCase;
import code.chat.ports.out.ChatDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StartChat implements StartChatUseCase {
    private final ChatDao chatDao;

    @Override
    public ChatId startChat(StartChatCommand command) {
        Optional<ChatId> chat = chatDao.findChat(command.initiator(), command.recipient());
        return chat.orElseGet(() -> chatDao.createChat(command.initiator(), command.recipient()));
    }
}
