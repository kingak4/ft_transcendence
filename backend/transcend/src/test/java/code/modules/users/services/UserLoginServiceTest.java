package code.modules.users.services;

import code.modules.users.ports.in.LoginUser.LoginCommand;
import code.modules.users.ports.out.AccessTokenIssuer;
import code.modules.users.ports.in.AuthenticateUser;
import code.modules.users.ports.in.AuthenticateUser.AuthResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

  @Mock
  private AuthenticateUser authenticateUser;

  @Mock
  private AccessTokenIssuer accessTokenIssuer;

  @InjectMocks
  private UserLoginService service;

  @Test
  void loginAuthenticatesAndReturnsBearerToken() {
    // given
    var email = "john@example.com";
    var command = new LoginCommand(email, "plain-password");

    when(authenticateUser.authenticate(new AuthenticateUser.AuthCommand(email, "plain-password")))
        .thenReturn(new AuthResult(UUID.randomUUID(), email));
    when(accessTokenIssuer.generateToken(email)).thenReturn("jwt-token");

    // when
    var result = service.login(command);

    // then
    assertEquals("jwt-token", result.accessToken());
    assertEquals("Bearer", result.tokenType());
    verify(authenticateUser).authenticate(new AuthenticateUser.AuthCommand(email, "plain-password"));
    verify(accessTokenIssuer).generateToken(email);
  }
}