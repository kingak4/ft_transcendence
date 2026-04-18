package code.modules.users.services;

import code.modules.users.ports.in.AuthenticateUseCase;
import code.modules.users.ports.out.HashingService;
import code.modules.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticateUseCase {

  private final UserDao userDao;
  private final HashingService hashingService;

  @Override
  public AuthResult authenticate(AuthCommand command) {
    var user =
        userDao
            .findByEmail(command.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!hashingService.matches(command.rawPassword(), user.password())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    return new AuthResult(user.id(), user.email());
  }
}