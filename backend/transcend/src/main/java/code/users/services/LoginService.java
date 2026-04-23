package code.users.services;

import code.users.domain.exceptions.InvalidCredentialsException;
import code.users.ports.in.LoginUseCase;
import code.users.ports.out.AccessTokenProvider;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class LoginService implements LoginUseCase {
  private final UserDao userDao;
  private final HashingService hashingService;
  private final AccessTokenProvider accessTokenProvider;

  @Override
  public LoginResult login(LoginCommand command) {
    var user =
        userDao
            .findByEmail(command.email())
            .orElseThrow(InvalidCredentialsException::new);

    if (!hashingService.matches(command.rawPassword(), user.password())) {
      throw new InvalidCredentialsException();
    }

    String token = accessTokenProvider.generateToken(user.email());
    return new LoginResult(token, "Bearer");
  }
}