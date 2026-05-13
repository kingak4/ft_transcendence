package code.users.ports.in;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;

import java.util.Map;

public interface ManageFriendsUseCase {
  void addFriend(UserId userId, UserId friendId);
  void removeFriend(UserId userId, UserId friendId);

  Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size);
}