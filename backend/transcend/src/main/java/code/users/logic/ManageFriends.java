package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.FriendId;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageFriends implements ManageFriendsUseCase {

  private final UserDao userDao;

  @Override
  public void addFriend(UserId userId, FriendId friendId) {
    if (!userDao.exists(friendId)) throw new UserNotFoundException();
    userDao.addFriend(userId, friendId);
  }

  @Override
  public void removeFriend(UserId userId, FriendId friendId) {
    if (!userDao.exists(friendId)) throw new UserNotFoundException();
    userDao.removeFriend(userId, friendId);
  }

  @Override
  public Page<FriendResult> getFriendList(UserId userId, Pageable pageable) {
    return userDao.getFriendList(userId, pageable);
  }
}
