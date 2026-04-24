package code.users.infrastructure.persistence;

import code.users.domain.model.User;
import code.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class UserRepository implements UserDao {

  // Password for the fixture is 'plain-password'
  @Override
  public Optional<User> findByEmail(String email) {
    User user =
        User.builder()
            .id(UUID.randomUUID())
            .email("user@email.com")
            .password("$2a$10$AXw0YvIyeQmI.HBhlXCIDOx.3bWg4M7/rwOm7U7m7wAuJvSi5FEhS")
            .build();
    return Optional.ofNullable(user.getEmail().equals(email) ? user : null);
  }

  @Override
  public void createUser(User user) {}
}
