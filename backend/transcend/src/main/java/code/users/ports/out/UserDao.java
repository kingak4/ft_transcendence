package code.users.ports.out;

import code.users.domain.model.User;
import code.users.domain.model.UserId;
import java.util.Optional;

public interface UserDao {

  Optional<User> findById(UserId id);

  Optional<User> findByEmail(String email);
  void createUser(User user);

  void updateUser(User user);
}
