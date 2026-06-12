package code.users.bootstrap;

import code.users.domain.model.Avatar;
import code.users.domain.model.UserDetails;
import code.users.ports.out.UserDao;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAvatarInitializer implements CommandLineRunner {

  private final UserDao userDao;

  @Override
  public void run(String... args) {
    log.info("Initializing default avatar");
    try {
      ClassPathResource resource = new ClassPathResource("default-avatar.png");
      if (resource.exists()) {
        try (InputStream is = resource.getInputStream()) {
          byte[] content = is.readAllBytes();
          userDao.saveAvatar(UserDetails.DEFAULT_AVATAR_USER_ID, new Avatar(content));
        }
      } else {
        userDao.saveAvatar(UserDetails.DEFAULT_AVATAR_USER_ID, new Avatar(new byte[] {0}));
      }
    } catch (Exception e) {
      log.warn("Failed to initialize default avatar {}", e.getMessage());
    }
  }
}
