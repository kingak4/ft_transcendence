package code.chat.logic;

import code.chat.domain.exception.ChatNotFoundException;
import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;
import code.chat.ports.out.ChatDao;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("chatValidator")
@RequiredArgsConstructor
public class MembershipValidator {

  private final ChatDao dao;

  public boolean isMember(Authentication authentication, ChatId chatId) {
    UserId userId = UserId.of(UUID.fromString(authentication.getName()));
    return dao.getChat(chatId)
        .map(chat -> chat.getParticipants().contains(userId))
        .orElseThrow(ChatNotFoundException::new);
  }
}