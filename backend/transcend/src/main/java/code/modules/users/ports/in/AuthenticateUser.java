package code.modules.users.ports.in;

import java.util.UUID;

public interface AuthenticateUser {

  AuthResult authenticate(AuthCommand command);

  record AuthCommand(String email, String rawPassword) {}

  record AuthResult(UUID userId, String email) {}
}
