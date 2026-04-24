package code.users.logic;

import code.users.domain.exceptions.InvalidCredentialsException;
import code.users.ports.in.LoginUseCase;
import code.users.ports.out.AccessTokenProvider;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class Login implements LoginUseCase {
  private final UserDao userDao;
  private final HashingService hashingService;
  private final AccessTokenProvider accessTokenProvider;

  @Override
  public LoginResult login(LoginCommand command) {
    var user = userDao.findByEmail(command.email()).orElseThrow(InvalidCredentialsException::new);

    if (!hashingService.matches(command.rawPassword(), user.getPassword())) {
      throw new InvalidCredentialsException();
    }

    String token = accessTokenProvider.generateToken(user.getEmail());
    return new LoginResult(token, "Bearer");
  }
}