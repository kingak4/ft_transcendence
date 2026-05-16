package code.users.bootstrap;

import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.ports.in.RegisterUseCase;
import code.users.ports.in.RegisterUseCase.RegisterCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("dev | local")
@RequiredArgsConstructor
@Component
public class UserFixtureInitializer implements ApplicationRunner {
  private final RegisterUseCase registerUseCase;

  @Override
  public void run(ApplicationArguments args) {
    log.info("Initializing default user fixture");
    String defaultEmail = "user@email.com";
    String defaultPassword = "plain-password";
    try {
      registerUseCase.register(new RegisterCommand(defaultEmail, defaultPassword));
    } catch (EmailAlreadyRegisteredException ignored) {
    }
  }
}
