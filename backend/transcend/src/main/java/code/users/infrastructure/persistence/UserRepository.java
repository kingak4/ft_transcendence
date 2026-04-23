package code.users.infrastructure.persistence;

import code.users.domain.User;
import code.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements UserDao {

  // Password for the fixture is 'plain-password'
  @Override
  public Optional<User> findByEmail(String email) {
    User user =
        new User(
            UUID.randomUUID(),
            "user@email.com",
            "$2a$10$AXw0YvIyeQmI.HBhlXCIDOx.3bWg4M7/rwOm7U7m7wAuJvSi5FEhS");
    return Optional.ofNullable(user.email().equals(email) ? user : null);
  }

  @Override
  public void createUser(User user) {}
}
