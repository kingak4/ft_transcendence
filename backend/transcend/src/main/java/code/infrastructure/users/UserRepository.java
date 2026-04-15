package code.infrastructure.users;

import code.modules.users.domain.User;
import code.modules.users.ports.out.UserDao;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository implements UserDao {

	// Password for the fixture is 'plain-password'
  @Override
  public Optional<User> findByEmail(String email) {
		User user = new User(
				UUID.randomUUID(),
				"user@email.com",
				"$2a$10$AXw0YvIyeQmI.HBhlXCIDOx.3bWg4M7/rwOm7U7m7wAuJvSi5FEhS"
		);
    return Optional.ofNullable(user.email().equals(email) ? user : null);
  }
}