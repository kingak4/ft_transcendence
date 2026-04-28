package code.bootstrap.config;

import code.users.domain.model.User;
import code.users.domain.model.UserId;
import code.users.infrastructure.persistence.UserEntity;
import code.users.infrastructure.persistence.UserEntityMapper;
import code.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFixtureInitializer implements ApplicationRunner {
  private final UserJpaRepository userJpaRepository;
  private final UserEntityMapper userEntityMapper;

  @Override
  public void run(ApplicationArguments args) {
    String defaultEmail = "user@email.com";

    boolean exists = userJpaRepository.findByEmail(defaultEmail).isPresent();

    if (!exists) {
      User defaultUser =
          User.builder()
              .id(UserId.generate())
              .email(defaultEmail)
              .password("$2a$10$AXw0YvIyeQmI.HBhlXCIDOx.3bWg4M7/rwOm7U7m7wAuJvSi5FEhS")
              .details(null)
              .build();

      UserEntity defaultEntity = userEntityMapper.toEntity(defaultUser);
      userJpaRepository.save(defaultEntity);
    }
  }
}
