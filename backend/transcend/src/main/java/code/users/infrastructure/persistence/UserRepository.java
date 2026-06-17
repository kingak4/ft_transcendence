package code.users.infrastructure.persistence;

import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional
public class UserRepository implements UserDao {
  private final UserJpaRepository userJpaRepository;
  private final UserDetailsJpaRepository userDetailsJpaRepository;
  private final AvatarJpaRepository avatarJpaRepository;
  private final UserEntityMapper mapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public void createUser(User user) {
    UserEntity entity = mapper.toEntity(user);
    UserDetailsEntity detailsEntity = mapper.toEntity(user.getDetails());
    detailsEntity.setId(mapper.map(user.getId()));
    userDetailsJpaRepository.save(detailsEntity);
    userJpaRepository.save(entity);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userJpaRepository.findById(mapper.map(id)).map(mapper::toDomain);
  }

  @Override
  public void updateUser(User user) {
    UserEntity entity =
        userJpaRepository
            .findById(mapper.map(user.getId()))
            .orElseThrow(EntityNotFoundException::new);
    entity.setHash(user.getPassword());

    if (user.getDetails() != null) {
      UserIdEntity userIdEntity = mapper.map(user.getId());
      UserDetailsEntity details =
          userDetailsJpaRepository
              .findById(userIdEntity)
              .orElseGet(
                  () -> {
                    UserDetailsEntity d = new UserDetailsEntity();
                    d.setId(userIdEntity);
                    return d;
                  });
      details.setDisplayName(user.getDetails().getDisplayName());
      userDetailsJpaRepository.save(details);
      entity.setUserDetailsId(userIdEntity.val());
    }
  }

  @Override
  public void saveAvatar(Avatar avatar) {
    AvatarEntity avatarEntity = new AvatarEntity();
    avatarEntity.setId(new AvatarIdEntity(avatar.id().val())); // was setVal()
    avatarEntity.setContent(avatar.content());
    avatarJpaRepository.save(avatarEntity);
  }

  @Override
  public void addFriend(UserId userId, FriendId friendId) {
    UserEntity entity =
        userJpaRepository.findById(mapper.map(userId)).orElseThrow(EntityNotFoundException::new);
    entity.getFriends().add(friendId.val());
  }

  @Override
  public void removeFriend(UserId userId, FriendId friendId) {
    UserEntity entity =
        userJpaRepository.findById(mapper.map(userId)).orElseThrow(EntityNotFoundException::new);
    entity.getFriends().remove(friendId.val());
  }

  @Override
  public Optional<UserDetails> findUserDetailsById(UserId id) {
    return userDetailsJpaRepository.findById(mapper.map(id)).map(mapper::toDomain);
  }

  @Override
  public Optional<Avatar> findById(AvatarId avatarId) {
    Optional<AvatarEntity> avatarEntity = avatarJpaRepository.findById(new AvatarIdEntity(avatarId.val())); // was avatarId.val()
    return avatarEntity.map(mapper::toDomain);
  }

  @Override
  public Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size) {
    UserEntity entity =
        userJpaRepository.findById(mapper.map(userId)).orElseThrow(EntityNotFoundException::new);
    return entity.getFriends().stream()
        .skip((long) page * size)
        .limit(size)
        .collect(
            Collectors.toMap(
                FriendId::of,
                friendUuid -> {
                  UserIdEntity friendIdEntity = new UserIdEntity(friendUuid);
                  Optional<UserDetailsEntity> detailsOpt =
                      userDetailsJpaRepository.findById(friendIdEntity);

                  String displayName = detailsOpt.map(UserDetailsEntity::getDisplayName).orElse("");
                  UUID avatarId =
                          detailsOpt
                                  .map(d -> d.getAvatarId() != null ? d.getAvatarId().val() : UserDetails.DEFAULT_AVATAR_ID.val())
                                  .orElse(UserDetails.DEFAULT_AVATAR_ID.val());

                  return UserDetails.builder()
                      .displayName(displayName)
                      .avatarId(AvatarId.of(avatarId))
                      .build();
                }));
  }

  @Override
  public boolean exists(FriendId friendId) {
    return userJpaRepository.existsById(new UserIdEntity(friendId.val()));
  }
}
