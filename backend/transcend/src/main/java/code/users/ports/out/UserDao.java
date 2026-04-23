package code.users.ports.out;

import code.users.domain.User;
import java.util.Optional;

public interface UserDao {

  Optional<User> findByEmail(String email);

  void createUser(User user);
}
