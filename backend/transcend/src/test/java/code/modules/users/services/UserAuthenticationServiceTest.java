package code.modules.users.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.modules.users.domain.User;
import code.modules.users.ports.in.AuthenticateUser.AuthCommand;
import code.modules.users.ports.out.HashingService;
import code.modules.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

  @Mock private UserDao userDao;

  @Mock private HashingService hashingService;

  @InjectMocks private UserAuthenticationService service;

  @Test
  void authenticateReturnsAuthResultWhenCredentialsAreValid() {
    // given
    var userId = UUID.randomUUID();
    var user = new User(userId, "john@example.com", "encoded-password");
    var command = new AuthCommand("john@example.com", "plain-password");

    when(userDao.findByEmail(command.email())).thenReturn(Optional.of(user));
    when(hashingService.matches(command.rawPassword(), user.password())).thenReturn(true);

    // when
    var result = service.authenticate(command);

    // then
    assertEquals(userId, result.userId());
    assertEquals("john@example.com", result.email());
    verify(userDao).findByEmail(command.email());
    verify(hashingService).matches(command.rawPassword(), user.password());
  }

  @Test
  void authenticateThrowsWhenUserDoesNotExist() {
    // given
    var command = new AuthCommand("missing@example.com", "plain-password");
    when(userDao.findByEmail(command.email())).thenReturn(Optional.empty());

    // when
    assertThrows(BadCredentialsException.class, () -> service.authenticate(command));

    // then
    verify(userDao).findByEmail(command.email());
    verify(hashingService, never()).matches(anyString(), anyString());
  }

  @Test
  void authenticateThrowsWhenPasswordDoesNotMatch() {
    // given
    var user = new User(UUID.randomUUID(), "john@example.com", "encoded-password");
    var command = new AuthCommand("john@example.com", "wrong-password");

    when(userDao.findByEmail(command.email())).thenReturn(Optional.of(user));
    when(hashingService.matches(command.rawPassword(), user.password())).thenReturn(false);

    // when
    assertThrows(BadCredentialsException.class, () -> service.authenticate(command));

    // then
    verify(userDao).findByEmail(command.email());
    verify(hashingService).matches(command.rawPassword(), user.password());
  }
}
