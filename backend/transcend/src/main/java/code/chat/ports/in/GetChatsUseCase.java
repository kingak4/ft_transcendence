package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

public interface GetChatsUseCase {

  @PreAuthorize(
      "hasRole(T(code.users.domain.model.Role).ADMIN.name) or @membershipValidator.isSameUser(authentication, #userId)")
  List<ChatSummary> getChatList(UserId userId, int page, int size);

  record ChatSummary(ChatId chatId, UserId otherUserId, String displayName, UUID avatarId) {}
}
