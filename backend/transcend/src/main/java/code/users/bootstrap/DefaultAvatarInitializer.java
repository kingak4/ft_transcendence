package code.users.bootstrap;

import static code.users.domain.model.Role.USER;

import code.users.domain.model.Avatar;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
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
      byte[] content = loadDefaultAvatarContent();
      userDao.saveAvatar(new Avatar(UserDetails.DEFAULT_AVATAR_ID, content));
      ensureDefaultAvatarUserExists();
    } catch (Exception e) {
      log.warn("Failed to initialize default avatar: {}", e.getMessage(), e);
    }
  }

  private void ensureDefaultAvatarUserExists() {
    if (userDao.findById(UserDetails.DEFAULT_AVATAR_ID).isPresent()) {
      throw new RuntimeException("Avatar does not exist after initialization");
    }
  }

  private byte[] loadDefaultAvatarContent() throws Exception {
    ClassPathResource resource = new ClassPathResource("default-avatar.png");
    if (!resource.exists()) {
      return new byte[] {0};
    }

    try (InputStream is = resource.getInputStream()) {
      return is.readAllBytes();
    }
  }
}