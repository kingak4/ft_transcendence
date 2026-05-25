package code.users.infrastructure.persistence;

import code.shared.exceptions.NotImplementedException;
import code.users.domain.model.Avatar;
import code.users.domain.model.FriendId;
import code.users.domain.model.Role;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
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
            user -> {
              // TODO: Replace role mocking with actual role retrieval from database
              return user.withRole(email.contains("admin") ? Role.ADMIN : Role.USER);
            });
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
        .map(
            user -> {
              // TODO: Replace role mocking with actual role retrieval from database
              return user.withRole(Role.USER);
            });
  }

  @Override
  public void updateUser(User user) {
    throw new NotImplementedException();
  }

  @Override
  public void saveAvatar(UserId userId, Avatar avatar) {
    throw new NotImplementedException();
  }

  @Override
  public Avatar getAvatar(UserId userId) {
    throw new NotImplementedException();
  }

  @Override
  public void addFriend(UserId userId, UserId friendId) {
    throw new NotImplementedException();
  }

  @Override
  public void removeFriend(UserId userId, UserId friendId) {
    throw new NotImplementedException();
  }

  @Override
  public Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size) {
    throw new NotImplementedException();
  }
}
