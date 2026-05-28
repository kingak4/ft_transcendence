package code.chat.logic;

import code.chat.domain.model.Chat;
import code.chat.domain.model.ChatId;
import code.chat.ports.in.StartChatUseCase;
import code.chat.ports.out.ChatDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StartChat implements StartChatUseCase {
    private final ChatDao chatDao;

    @Override
    public ChatId startChat(StartChatCommand command) {
        Optional<ChatId> id = chatDao.findChat(command.initiator(), command.recipient());
        if (id.isPresent()) return id.get();
        Chat chat = Chat.builder()
                .id(ChatId.generate())
                .participants(Set.of(command.initiator(), command.recipient()))
                .build();
        return chatDao.createChat(chat);
    }
}
