package code.users.ports.in;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ManageFriendsUseCase {
  @PreAuthorize(
      "hasRole(T(code.users.domain.model.Role).ADMIN.name) or @ownershipValidator.isSameUser(authentication, #userId)")
  void addFriend(UserId userId, FriendId friendId);

  @PreAuthorize(
      "hasRole(T(code.users.domain.model.Role).ADMIN.name) or @ownershipValidator.isSameUser(authentication, #userId)")
  void removeFriend(UserId userId, FriendId friendId);

  Page<FriendResult> getFriendList(UserId userId, Pageable pageable);

  record FriendResult(FriendId id, UserDetails details) {}
}
