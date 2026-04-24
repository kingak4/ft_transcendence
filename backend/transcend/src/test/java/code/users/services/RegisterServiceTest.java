package code.users.services;

import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.domain.model.User;
import code.users.ports.in.RegisterUseCase;
import code.users.ports.in.RegisterUseCase.RegisterCommand;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(RegisterServiceTest.RegisterServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class RegisterServiceTest {

  @MockitoBean private UserDao userDao;
  @MockitoBean private HashingService hashingService;

  private final RegisterUseCase service;

  @Configuration
  @Import(RegisterService.class)
  static class RegisterServiceTestConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
  }

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
    var existingUser = new User(UUID.randomUUID(), email, "hashed-password", null);

    when(hashingService.encode("plain-password")).thenReturn("hashed-password");
    when(userDao.findByEmail(email)).thenReturn(Optional.of(existingUser));

    // when & then
    assertThrows(EmailAlreadyRegisteredException.class, () -> service.register(command));
    verify(userDao).findByEmail(email);
  }

  @Test
  void registerThrowsWhenCommandIsInvalid() {
    // given
    var command = new RegisterCommand("invalid-email", "short");
    // when & then
    assertThrows(jakarta.validation.ConstraintViolationException.class, () -> service.register(command));
  }
}