package code.users.infrastructure.persistence;

import code.shared.exceptions.NotImplementedException;
import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class UserRepository implements UserDao {
  private final UserJpaRepository userJpaRepository;
  private final UserDetailsJpaRepository userDetailsJpaRepository;
  private final AvatarJpaRepository avatarJpaRepository;
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
  @Transactional
  public void updateUser(User user) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(user.getId()))
            .orElseThrow(EntityNotFoundException::new);
    entity.setHash(user.getPassword());

    if (user.getDetails() != null) {
      UserIdEntity userIdEntity = userEntityMapper.map(user.getId());
      UserDetailsEntity details = userDetailsJpaRepository.findById(userIdEntity)
              .orElseGet(() -> {
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
  // TODO impl this
  public Optional<UserDetails> findUserDetailsById(UserId id) {
    throw new NotImplementedException();
  }

  @Override
  @Transactional
  public void saveAvatar(UserId userId, Avatar avatar) {
    UserIdEntity userIdEntity = userEntityMapper.map(userId);
    UserEntity entity = userJpaRepository.findById(userIdEntity)
            .orElseThrow(EntityNotFoundException::new);

    UserDetailsEntity details = userDetailsJpaRepository.findById(userIdEntity)
            .orElseGet(() -> {
              UserDetailsEntity d = new UserDetailsEntity();
              d.setId(userIdEntity);
              return d;
            });

    // TODO UUID is generated in avatar, save that instead and set.
    UUID avatarVal = UUID.randomUUID();
    AvatarEntity avatarEntity = new AvatarEntity();
    avatarEntity.setVal(avatarVal);
    avatarEntity.setContent(avatar.content());
    avatarJpaRepository.save(avatarEntity);

    details.setAvatarId(avatarEntity.getVal());
    userDetailsJpaRepository.save(details);

    entity.setUserDetailsId(userIdEntity.val());
  }

  @Override
  public Avatar findById(AvatarId userId) {
    // TODO adjust this
//    UserIdEntity userIdEntity = userEntityMapper.map(userId);
//    UserDetailsEntity details = userDetailsJpaRepository.findById(userIdEntity)
//            .orElseThrow(EntityNotFoundException::new);
//
//    if (details.getAvatarId() == null) {
//      throw new EntityNotFoundException();
//    }
//
//    AvatarEntity avatarEntity = avatarJpaRepository.findById(details.getAvatarId())
//            .orElseThrow(EntityNotFoundException::new);
//
//    return new Avatar(avatarEntity.getContent());
    throw new NotImplementedException();
  }

  @Override
  @Transactional
  public void addFriend(UserId userId, FriendId friendId) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);
    entity.getFriends().add(friendId.val());
  }

  @Override
  @Transactional
  public void removeFriend(UserId userId, FriendId friendId) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);
    entity.getFriends().remove(friendId.val());
  }

  @Override
  @Transactional
  public Map<FriendId, UserDetails> getFriendList(UserId userId, int page, int size) {
    UserEntity entity = userJpaRepository.findById(userEntityMapper.map(userId))
            .orElseThrow(EntityNotFoundException::new);

    // TODO adapt to not use default URL.
//    return entity.getFriends().stream()
//            .skip((long) page * size)
//            .limit(size)
//            .collect(Collectors.toMap(
//                    FriendId::of,
//                    friendUuid -> {
//                      UserIdEntity friendIdEntity = new UserIdEntity(friendUuid);
//                      Optional<UserDetailsEntity> detailsOpt = userDetailsJpaRepository.findById(friendIdEntity);
//
//                      String displayName = detailsOpt.map(UserDetailsEntity::getDisplayName).orElse("");
//                      String avatarUrl = detailsOpt
//                              .map(UserDetailsEntity::getAvatarId)
//                              .map(avatarId -> UserDetails.AVATARS_BASE_URL + avatarId)
//                              .orElse(UserDetails.DEFAULT_AVATAR_URL);
//
//                      return UserDetails.builder()
//                              .displayName(displayName)
//                              .avatarUrl(avatarUrl)
//                              .build();
//                    }
//            ));
    throw new NotImplementedException();
  }

  @Override
  public boolean exists(FriendId friendId) {
    return userJpaRepository.existsById(new UserIdEntity(friendId.val()));
  }
}