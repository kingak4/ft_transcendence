package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;
import org.springframework.security.access.prepost.PreAuthorize;

public interface StartChatUseCase {
    @PreAuthorize("hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.initiator())")
    ChatId startChat(StartChatCommand command);

    record StartChatCommand(UserId initiator, UserId recipient) {}
}
