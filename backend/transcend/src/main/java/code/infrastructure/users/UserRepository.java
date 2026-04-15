package code.infrastructure.users;

import code.modules.users.domain.User;
import code.modules.users.ports.out.UserDao;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository implements UserDao {

  @Override
  public Optional<User> findByEmail(String email) {
		User user = new User(
				UUID.randomUUID(),
				"user@email.com",
				"pass"
		);
    return Optional.ofNullable(user.email().equals(email) ? user : null);
  }
}