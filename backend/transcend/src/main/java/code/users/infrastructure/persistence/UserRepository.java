package code.users.infrastructure.persistence;

+import code.users.domain.model.Role;
import code.users.domain.model.User;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class UserRepository implements UserDao {
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
}
