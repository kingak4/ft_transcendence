package code.users.services;

import static code.users.domain.model.UserFixtures.EMAIL_FIXTURE;
import static code.users.domain.model.UserFixtures.HASH_FIXTURE;
import static code.users.domain.model.UserFixtures.PASSWORD_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
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
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringJUnitConfig(RegisterServiceTest.RegisterServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class RegisterServiceTest {

  @Configuration
  @Import(RegisterService.class)
  static class RegisterServiceTestConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
    }
  }

  private final RegisterUseCase service;

  @MockitoBean private UserDao userDao;
  @MockitoBean private HashingService hashingService;

  @Test
  void registerCreatesUserAndReturnsId() {
    // given
    var command = new RegisterCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE);

    when(hashingService.encode(PASSWORD_FIXTURE)).thenReturn(HASH_FIXTURE);
    when(userDao.findByEmail(EMAIL_FIXTURE)).thenReturn(Optional.empty());

    // when
    var result = service.register(command);

    // then
    assertNotNull(result.id());
    verify(hashingService).encode(PASSWORD_FIXTURE);
    verify(userDao).findByEmail(EMAIL_FIXTURE);
    verify(userDao).createUser(any(User.class));
  }

  @Test
  void registerThrowsWhenEmailAlreadyExists() {
    // given
    var command = new RegisterCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE);
    var existingUser = aDefaultUser();

    when(hashingService.encode(PASSWORD_FIXTURE)).thenReturn(HASH_FIXTURE);
    when(userDao.findByEmail(EMAIL_FIXTURE)).thenReturn(Optional.of(existingUser));

    // when & then
    assertThrows(EmailAlreadyRegisteredException.class, () -> service.register(command));
    verify(userDao).findByEmail(EMAIL_FIXTURE);
  }

  @Test
  void registerThrowsWhenCommandIsInvalid() {
    // given
    var command = new RegisterCommand("invalid-email", "short");
    // when & then
    assertThrows(
        jakarta.validation.ConstraintViolationException.class, () -> service.register(command));
  }
}
