package code.modules.users.services;

import code.modules.users.ports.in.AuthenticateUseCase;
import code.modules.users.ports.in.LoginUseCase;
import code.modules.users.ports.out.AccessTokenIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService implements LoginUseCase {

  private static final String TOKEN_TYPE = "Bearer";
  private final AuthenticateUseCase authenticateUseCase;
  private final AccessTokenIssuer accessTokenIssuer;

  @Override
  public LoginResult login(LoginCommand command) {
    var authResult =
        authenticateUseCase.authenticate(
            new AuthenticateUseCase.AuthCommand(command.email(), command.rawPassword()));
    String token = accessTokenIssuer.generateToken(authResult.email());
    return new LoginResult(token, TOKEN_TYPE);
  }
}