package code.users.ports.in;

import java.util.UUID;

public interface RegisterUseCase {

  RegisteredUser register(RegisterCommand command);

  record RegisterCommand(String email, String rawPassword) {}

  record RegisteredUser(UUID id) {}
}
