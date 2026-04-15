package code.modules.users.services;

import code.modules.users.ports.in.AuthenticateUser;
import code.modules.users.ports.out.HashingService;
import code.modules.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserAuthenticationService implements AuthenticateUser {

  private final UserDao userDao;
  private final HashingService hashingService;

  @Override
  public AuthResult authenticate(AuthCommand command) {
    var user = userDao.findByEmail(command.email())
        .orElseThrow(InvalidCredentialsException::new);

    if (!hashingService.matches(command.rawPassword(), user.password())) {
      throw new InvalidCredentialsException();
    }

    return new AuthResult(user.id(), user.email());
  }
}