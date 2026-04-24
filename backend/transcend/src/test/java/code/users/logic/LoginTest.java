package code.users.logic;

import static code.users.domain.model.UserFixtures.EMAIL_FIXTURE;
import static code.users.domain.model.UserFixtures.HASH_FIXTURE;
import static code.users.domain.model.UserFixtures.PASSWORD_FIXTURE;
import static code.users.domain.model.UserFixtures.TOKEN_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.InvalidCredentialsException;
import code.users.ports.in.LoginUseCase;
import code.users.ports.in.LoginUseCase.LoginCommand;
import code.users.ports.out.AccessTokenProvider;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(LoginTest.LoginServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class LoginTest {

  @Configuration
  @Import(Login.class)
  static class LoginServiceTestConfig {}

  private final LoginUseCase service;

  @MockitoBean private UserDao userDao;
  @MockitoBean private HashingService hashingService;
  @MockitoBean private AccessTokenProvider accessTokenProvider;

  @Test
  void loginAuthenticatesAndReturnsBearerToken() {
    // given
    var command = new LoginCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE);
    var user = aDefaultUser();

    when(userDao.findByEmail(EMAIL_FIXTURE)).thenReturn(Optional.of(user));
    when(hashingService.matches(PASSWORD_FIXTURE, HASH_FIXTURE)).thenReturn(true);
    when(accessTokenProvider.generateToken(EMAIL_FIXTURE)).thenReturn(TOKEN_FIXTURE);

    // when
    var result = service.login(command);

    // then
    assertEquals(TOKEN_FIXTURE, result.accessToken());
    assertEquals("Bearer", result.tokenType());
    verify(userDao).findByEmail(EMAIL_FIXTURE);
    verify(hashingService).matches(PASSWORD_FIXTURE, HASH_FIXTURE);
    verify(accessTokenProvider).generateToken(EMAIL_FIXTURE);
  }

  @Test
  void loginWithInvalidEmailThrowsException() {
    // given
    var command = new LoginCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE);

    when(userDao.findByEmail(EMAIL_FIXTURE)).thenReturn(Optional.empty());

    // when & then
    assertThrows(InvalidCredentialsException.class, () -> service.login(command));
  }

  @Test
  void loginWithInvalidPasswordThrowsException() {
    // given
    var command = new LoginCommand(EMAIL_FIXTURE, "wrong-password");
    var user = aDefaultUser();
    when(userDao.findByEmail(EMAIL_FIXTURE)).thenReturn(Optional.of(user));
    when(hashingService.matches("wrong-password", PASSWORD_FIXTURE)).thenReturn(false);

    // when & then
    assertThrows(InvalidCredentialsException.class, () -> service.login(command));
  }
}