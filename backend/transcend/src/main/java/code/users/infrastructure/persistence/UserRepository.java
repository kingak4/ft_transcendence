package code.users.infrastructure.persistence;

import code.users.domain.model.User;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class UserRepository implements UserDao {
  private final UserJpaRepository userJpaRepository;
  private final UserEntityMapper userEntityMapper;

  public UserRepository(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
    this.userJpaRepository = userJpaRepository;
    this.userEntityMapper = userEntityMapper;
  }

  // Password for the fixture is 'plain-password'
  @Override
  public Optional<User> findByEmail(String email) {
    User user =
        User.builder()
            .id(UserId.generate())
            .email("user@email.com")
            .password("$2a$10$AXw0YvIyeQmI.HBhlXCIDOx.3bWg4M7/rwOm7U7m7wAuJvSi5FEhS")
            .build();
    return Optional.ofNullable(user.getEmail().equals(email) ? user : null);
  }

  @Override
  public void createUser(User user) {
    UserEntity entity = userEntityMapper.toEntity(user);
    userJpaRepository.save(entity);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userJpaRepository.findById(id.getValue()).map(userEntityMapper::toDomain);
  }

  @Override
  public void updateUser(User user) {}
}
