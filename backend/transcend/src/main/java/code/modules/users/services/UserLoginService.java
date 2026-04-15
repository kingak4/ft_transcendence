package code.modules.users.services;

import code.modules.users.ports.in.LoginUser;
import code.modules.users.ports.out.AccessTokenIssuer;
import code.modules.users.ports.in.AuthenticateUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService implements LoginUser {

  private static final String TOKEN_TYPE = "Bearer";
  private final AuthenticateUser authenticateUser;
  private final AccessTokenIssuer accessTokenIssuer;

  @Override
  public LoginResult login(LoginCommand command) {
    var authResult = authenticateUser.authenticate(
        new AuthenticateUser.AuthCommand(command.email(), command.rawPassword())
    );
    String token = accessTokenIssuer.generateToken(authResult.email());
    return new LoginResult(token, TOKEN_TYPE);
  }
}