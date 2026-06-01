package code.users.logic;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import code.users.ports.out.UserDao;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageFriends implements ManageFriendsUseCase {

  private final UserDao userDao;

  @Override
  public void addFriend(UserId userId, FriendId friendId) {
    userDao.addFriend(userId, friendId);
  }

  @Override
  public void removeFriend(UserId userId, FriendId friendId) {
    userDao.removeFriend(userId, friendId);
  }

  @Override
  public Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size) {
    return userDao.getFriendList(userId, page, size);
  }
}
