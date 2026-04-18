package code.modules.users.services;

import code.modules.users.ports.in.LoginUseCase;
import code.modules.users.ports.out.AccessTokenIssuer;
import code.modules.users.ports.out.HashingService;
import code.modules.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService implements LoginUseCase {
  private final UserDao userDao;
  private final HashingService hashingService;
  private final AccessTokenIssuer accessTokenIssuer;

  @Override
  public LoginResult login(LoginCommand command) {
    var user =
        userDao
            .findByEmail(command.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!hashingService.matches(command.rawPassword(), user.password())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    String token = accessTokenIssuer.generateToken(user.email());
    return new LoginResult(token, "Bearer");
  }
}
