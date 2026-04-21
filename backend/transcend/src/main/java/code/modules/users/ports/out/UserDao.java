package code.modules.users.ports.out;

import code.modules.users.domain.User;
import java.util.Optional;

public interface UserDao {

  Optional<User> findByEmail(String email);
}
