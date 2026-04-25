package code.users.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.domain.model.User;
import code.users.ports.in.RegisterUseCase;
import code.users.ports.in.RegisterUseCase.RegisterCommand;
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

@SpringJUnitConfig(RegisterServiceTest.RegisterServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class RegisterServiceTest {

  @MockBean private UserDao userDao;

  @MockBean private HashingService hashingService;

  private final RegisterUseCase service;

  @Configuration
  @Import(RegisterService.class)
  static class RegisterServiceTestConfig {}

  @Test
  void registerCreatesUserAndReturnsId() {
    // given
    var email = "john@example.com";
    var command = new RegisterCommand(email, "plain-password");

    when(hashingService.encode("plain-password")).thenReturn("hashed-password");
    when(userDao.findByEmail(email)).thenReturn(Optional.empty());

    // when
    var result = service.register(command);

    // then
    assertNotNull(result.id());
    verify(hashingService).encode("plain-password");
    verify(userDao).findByEmail(email);
    verify(userDao).createUser(any(User.class));
  }

  @Test
  void registerThrowsWhenEmailAlreadyExists() {
    // given
    var email = "john@example.com";
    var command = new RegisterCommand(email, "plain-password");
    var existingUser = new User(UUID.randomUUID(), email, "hashed-password");

    when(hashingService.encode("plain-password")).thenReturn("hashed-password");
    when(userDao.findByEmail(email)).thenReturn(Optional.of(existingUser));

    // when
    assertThrows(EmailAlreadyRegisteredException.class, () -> service.register(command));

    // then
    verify(userDao).findByEmail(email);
  }
}
