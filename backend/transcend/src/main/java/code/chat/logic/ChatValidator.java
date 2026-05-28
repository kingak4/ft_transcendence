package code.chat.logic;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;
import code.chat.ports.out.ChatDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("chatValidator")
@RequiredArgsConstructor
public class ChatValidator {
    
    private final ChatDao dao;

    public boolean isMember(Authentication authentication, ChatId chatId) {
        UserId userId = UserId.of(UUID.fromString(authentication.getName()));
        return dao.getChat(chatId)
                  .map(chat -> chat.getParticipants().contains(userId))
                  .orElse(false);
    }
}