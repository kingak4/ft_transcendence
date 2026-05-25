package code.users.infrastructure.persistence;

import code.users.domain.model.Avatar;
import code.users.domain.model.FriendId;
import code.users.domain.model.Role;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepository implements UserDao {
  private final UserJpaRepository userJpaRepository;
  private final UserEntityMapper userEntityMapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository
        .findByEmail(email)
        .map(userEntityMapper::toDomain)
        .map(
            user ->
                user.withRole(email.contains("admin") ? Role.ADMIN : Role.USER)); // mock ADMIN role
  }

  @Override
  public void createUser(User user) {
    UserEntity entity = userEntityMapper.toEntity(user);
    userJpaRepository.save(entity);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userJpaRepository
        .findById(userEntityMapper.map(id))
        .map(userEntityMapper::toDomain)
        .map(user -> user.withRole(Role.USER)); // Mock role assignment
  }

  @Override
  public void updateUser(User user) {}

  @Override
  public void saveAvatar(UserId userId, Avatar avatar) {}

  @Override
  public Avatar getAvatar(UserId userId) {
    return new Avatar(new byte[0]);
  }

  @Override
  public void addFriend(UserId userId, UserId friendId) {}

  @Override
  public void removeFriend(UserId userId, UserId friendId) {}

  @Override
  public Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size) {
    return Collections.emptyMap();
  }
}
