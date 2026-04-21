package code.bootstrap.config;

import code.modules.users.ports.in.AuthenticateUser;
import code.modules.users.ports.out.HashingService;
import code.modules.users.ports.out.UserDao;
import code.modules.users.services.UserAuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersModuleConfig {

  @Bean
  public AuthenticateUser authenticateUser(UserDao userDao, HashingService hashingService) {
    return new UserAuthenticationService(userDao, hashingService);
  }
}
