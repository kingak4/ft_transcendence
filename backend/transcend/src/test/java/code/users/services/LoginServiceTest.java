package code.users.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.model.User;
import code.users.ports.in.LoginUseCase.LoginCommand;
import code.users.ports.out.AccessTokenProvider;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock private UserDao userDao;

  @Mock private HashingService hashingService;

  @Mock private AccessTokenProvider accessTokenProvider;

  @InjectMocks private LoginService service;

  @Test
  void loginAuthenticatesAndReturnsBearerToken() {
    var email = "john@example.com";
    var command = new LoginCommand(email, "plain-password");
    var user = new User(UUID.randomUUID(), email, "hashed-password");

    when(userDao.findByEmail(email)).thenReturn(Optional.of(user));
    when(hashingService.matches("plain-password", "hashed-password")).thenReturn(true);
    when(accessTokenProvider.generateToken(email)).thenReturn("jwt-token");

    var result = service.login(command);

    assertEquals("jwt-token", result.accessToken());
    assertEquals("Bearer", result.tokenType());
    verify(userDao).findByEmail(email);
    verify(hashingService).matches("plain-password", "hashed-password");
    verify(accessTokenProvider).generateToken(email);
  }

  @Test
  void loginWithInvalidEmailThrowsException() {
    var email = "john@example.com";
    var command = new LoginCommand(email, "plain-password");

    when(userDao.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(BadCredentialsException.class, () -> service.login(command));
  }

  @Test
  void loginWithInvalidPasswordThrowsException() {
    var email = "john@example.com";
    var command = new LoginCommand(email, "plain-password");
    var user = new User(UUID.randomUUID(), email, "hashed-password");

    when(userDao.findByEmail(email)).thenReturn(Optional.of(user));
    when(hashingService.matches("plain-password", "hashed-password")).thenReturn(false);

    assertThrows(BadCredentialsException.class, () -> service.login(command));
  }
}
