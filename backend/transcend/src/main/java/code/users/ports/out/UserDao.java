package code.users.ports.out;

import code.users.domain.model.User;
import java.util.Optional;

public interface UserDao {

  Optional<User> findByEmail(String email);

  void createUser(User user);
}
