package code.users.infrastructure.persistence;

import code.shared.exceptions.NotImplementedException;
import code.users.domain.model.Avatar;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class UserRepository implements UserDao {
  private final UserJpaRepository userJpaRepository;
  private final UserEntityMapper userEntityMapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email).map(userEntityMapper::toDomain);
  }

  @Override
  public void createUser(User user) {
    UserEntity entity = userEntityMapper.toEntity(user);
    userJpaRepository.save(entity);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userJpaRepository.findById(userEntityMapper.map(id)).map(userEntityMapper::toDomain);
  }

  @Override
  public void updateUser(User user) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(user.getId()))
            .orElseThrow(EntityNotFoundException::new);
    entity.setHash(user.getPassword());
    if (user.getDetails() != null) {
      entity.setDisplayName(user.getDetails().getDisplayName());
    }
    userJpaRepository.save(entity);
  }

  @Override
  public void saveAvatar(UserId userId, Avatar avatar) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);
    entity.setAvatar(avatar.content());
    userJpaRepository.save(entity);
  }

  @Override
  public Avatar getAvatar(UserId userId) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);
    return new Avatar(entity.getAvatar());
  }

  @Override
  public void addFriend(UserId userId, FriendId friendId) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);
    entity.getFriends().add(friendId.val());
    userJpaRepository.save(entity);
  }

  @Override
  public void removeFriend(UserId userId, FriendId friendId) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);
    entity.getFriends().remove(friendId.val());
    userJpaRepository.save(entity);
  }

  @Override
  public Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);

    return entity.getFriends().stream()
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toMap(
                    FriendId::of,
                    friendUuid -> userJpaRepository.findById(new UserIdEntity(friendUuid))
                            .map(f -> UserDetails.builder()
                                    .displayName(f.getDisplayName())
                                    .avatarUrl(f.getAvatar() != null
                                            ? UserDetails.AVATARS_BASE_URL + friendUuid
                                            : UserDetails.DEFAULT_AVATAR_URL)
                                    .build())
                            .orElse(UserDetails.builder()
                                    .displayName("")
                                    .avatarUrl(UserDetails.DEFAULT_AVATAR_URL)
                                    .build())
            ));
  }

  @Override
  public boolean exists(FriendId friendId) {
    return userJpaRepository.existsById(new UserIdEntity(friendId.val()));
  }
}
