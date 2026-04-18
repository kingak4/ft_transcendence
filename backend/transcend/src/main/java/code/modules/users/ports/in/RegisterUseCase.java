package code.modules.users.ports.in;

import java.util.UUID;

public interface RegisterUseCase {

  RegisteredUser register(RegisterCommand command); // TODO

  record RegisterCommand(String email, String rawPassword) {}

  record RegisteredUser(UUID id, String email) {}
}
