package code.modules.users.ports.out;

import code.modules.User;
import org.springframework.modulith.NamedInterface;

import java.util.Optional;

@NamedInterface
public interface UserDao {

  Optional<User> findByEmail(String email);
}
