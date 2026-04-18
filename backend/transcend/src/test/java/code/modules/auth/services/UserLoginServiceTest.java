package code.modules.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.modules.users.ports.in.AuthenticateUseCase;
import code.modules.users.ports.in.AuthenticateUseCase.AuthResult;
import code.modules.users.ports.in.LoginUseCase.LoginCommand;
import code.modules.users.ports.out.AccessTokenIssuer;
import code.modules.users.services.UserLoginService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

  @Mock private AuthenticateUseCase authenticateUseCase;

  @Mock private AccessTokenIssuer accessTokenIssuer;

  @InjectMocks private UserLoginService service;

  @Test
  void loginAuthenticatesAndReturnsBearerToken() {
    var email = "john@example.com";
    var command = new LoginCommand(email, "plain-password");

    when(authenticateUseCase.authenticate(new AuthenticateUseCase.AuthCommand(email, "plain-password")))
        .thenReturn(new AuthResult(UUID.randomUUID(), email));
    when(accessTokenIssuer.generateToken(email)).thenReturn("jwt-token");

    var result = service.login(command);

    assertEquals("jwt-token", result.accessToken());
    assertEquals("Bearer", result.tokenType());
    verify(authenticateUseCase)
        .authenticate(new AuthenticateUseCase.AuthCommand(email, "plain-password"));
    verify(accessTokenIssuer).generateToken(email);
  }
}