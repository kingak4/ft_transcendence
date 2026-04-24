package code.users.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.InvalidCredentialsException;
import code.users.domain.model.User;
import code.users.ports.in.LoginUseCase;
import code.users.ports.in.LoginUseCase.LoginCommand;
import code.users.ports.out.AccessTokenProvider;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(LoginServiceTest.LoginServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class LoginServiceTest {

  @MockBean private UserDao userDao;

  @MockBean private HashingService hashingService;

  @MockBean private AccessTokenProvider accessTokenProvider;

  private final LoginUseCase service;

  @Configuration
  @Import(LoginService.class)
  static class LoginServiceTestConfig {}

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

    assertThrows(InvalidCredentialsException.class, () -> service.login(command));
  }

  @Test
  void loginWithInvalidPasswordThrowsException() {
    var email = "john@example.com";
    var command = new LoginCommand(email, "plain-password");
    var user = new User(UUID.randomUUID(), email, "hashed-password");

    when(userDao.findByEmail(email)).thenReturn(Optional.of(user));
    when(hashingService.matches("plain-password", "hashed-password")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> service.login(command));
  }
}
