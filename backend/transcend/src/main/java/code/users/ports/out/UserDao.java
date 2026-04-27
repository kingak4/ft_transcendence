package code.users.ports.out;

import code.users.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {

  Optional<User> findByEmail(String email);
  Optional<User> findById(UUID id);
  void createUser(User user);
}
