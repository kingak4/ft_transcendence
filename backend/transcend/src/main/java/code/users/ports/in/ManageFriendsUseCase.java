package code.users.ports.in;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ManageFriendsUseCase {
  @PreAuthorize("hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #userId)")
  void addFriend(UserId userId, UserId friendId);

  @PreAuthorize("hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #userId)")
  void removeFriend(UserId userId, UserId friendId);

  Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size);
}
