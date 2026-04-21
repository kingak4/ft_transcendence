package code.modules.users.ports.in;

import java.util.UUID;

public interface RegisterUser {

  RegisteredUser register(RegisterCommand command);

  record RegisterCommand(String email, String rawPassword) {}

  record RegisteredUser(UUID id, String email) {}
}
