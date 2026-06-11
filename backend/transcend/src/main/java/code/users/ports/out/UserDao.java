package code.users.ports.out;

import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import java.util.Map;
import java.util.Optional;

public interface UserDao {

  Optional<User> findById(UserId id);

  Optional<User> findByEmail(String email);

  void createUser(User user);

  void updateUser(User user);

  void saveAvatar(UserId userId, Avatar avatar);

  Avatar findById(AvatarId userId);

  void addFriend(UserId userId, FriendId friendId);

  void removeFriend(UserId userId, FriendId friendId);

  Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size);

  boolean exists(FriendId friendId);

  Optional<UserDetails> findUserDetailsById(UserId id);
}