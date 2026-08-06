package code.users.ports.out;

import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDao {

  Page<User> searchUsers(String query, Pageable pageable);

  Optional<User> findById(UserId id);

  Optional<User> findByEmail(String email);

  void createUser(User user);

  void updateUser(User user);

  void updateDetails(UserId id, UserDetails newDetails);

  void saveAvatar(Avatar avatar);

  Optional<Avatar> findById(AvatarId userId);

  void addFriend(UserId userId, FriendId friendId);

  void removeFriend(UserId userId, FriendId friendId);

  public Page<ManageFriendsUseCase.FriendResult> getFriendList(UserId userId, Pageable pageable);

  boolean exists(FriendId friendId);

  Optional<UserDetails> findUserDetailsById(UserId id);
}
