package code.users.infrastructure.persistence;

import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import code.users.ports.out.UserDao;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Repository
@Transactional
public class UserRepository implements UserDao {
  private final UserJpaRepository userJpaRepository;
  private final UserDetailsJpaRepository userDetailsJpaRepository;
  private final AvatarJpaRepository avatarJpaRepository;
  private final UserEntityMapper mapper;
  private static final int MAX_PAGE_SIZE = 20;

  @Override
  public Page<User> searchUsers(String query, Pageable pageable) {
    Page<UserDetailsEntity> detailsPage =
        userDetailsJpaRepository.findByDisplayNameContainingIgnoreCase(query, pageable);

    return detailsPage.map(
        detailsEntity -> {
          UserIdEntity userIdEntity = detailsEntity.getId();
          UserEntity userEntity =
              userJpaRepository.findById(userIdEntity).orElseThrow(EntityNotFoundException::new);

          User user = mapper.toDomain(userEntity);
          return user.withDetails(mapper.toDomain(detailsEntity));
        });
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public void createUser(User user) {
    UserEntity entity = mapper.toEntity(user);
    UserDetailsEntity detailsEntity = mapper.toEntity(user.getDetails(), entity);
    userJpaRepository.save(entity);
    userDetailsJpaRepository.save(detailsEntity);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userJpaRepository.findById(mapper.map(id)).map(mapper::toDomain);
  }

  @Override
  public void updateUser(User user) {
    UserIdEntity userIdEntity = mapper.map(user.getId());

    UserEntity userEntity =
        userJpaRepository.findById(userIdEntity).orElseThrow(EntityNotFoundException::new);

    userEntity.setHash(user.getPassword());

    if (user.getDetails() != null) {
      updateDetails(user.getId(), user.getDetails());
    }
  }

  @Override
  public void updateDetails(UserId id, UserDetails newDetails) {
    UserIdEntity userIdEntity = mapper.map(id);

    UserDetailsEntity details =
        userDetailsJpaRepository.findById(userIdEntity).orElseGet(UserDetailsEntity::new);

    details.setId(userIdEntity);
    details.setAvatarId(mapper.map(newDetails.getAvatarId()));
    details.setDisplayName(newDetails.getDisplayName());

    userDetailsJpaRepository.save(details);
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
    userJpaRepository.save(entity);
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
    Optional<AvatarEntity> avatarEntity =
        avatarJpaRepository.findById(new AvatarIdEntity(avatarId.val())); // was avatarId.val()
    return avatarEntity.map(mapper::toDomain);
  }

  @Override
  public Page<ManageFriendsUseCase.FriendResult> getFriendList(UserId userId, Pageable pageable) {
    if (pageable.getPageSize() < 0 || pageable.getPageSize() > MAX_PAGE_SIZE) {
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "size must be between 0 and " + MAX_PAGE_SIZE
      );
    }

    Page<Object[]> rows =
            userDetailsJpaRepository.findFriendDetailsByUserId(userId.val(), pageable);

    return rows.map(row -> new ManageFriendsUseCase.FriendResult(
            FriendId.of((UUID) row[0]),
            UserDetails.builder()
                    .displayName(row[1] != null ? (String) row[1] : "")
                    .avatarId(row[2] != null ? AvatarId.of((UUID) row[2]) : AvatarId.DEFAULT_AVATAR_ID)
                    .build()
    ));
  }

  @Override
  public boolean exists(FriendId friendId) {
    return userJpaRepository.existsById(new UserIdEntity(friendId.val()));
  }
}
