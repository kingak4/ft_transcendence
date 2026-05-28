package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface GetChatsUseCase {

    @PreAuthorize("hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #userId)")
    List<ChatId> getChatList(UserId userId, int page, int size);
}
