package code.users.bootstrap;

import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.ports.out.UserDao;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class DefaultAvatarInitializer implements ApplicationRunner {

  private final UserDao userDao;

  private void ensureDefaultAvatarUserExists() {
    if (userDao.findById(AvatarId.DEFAULT_AVATAR_ID).isEmpty()) {
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

  @Override
  public void run(ApplicationArguments args) {
    log.info("Initializing default avatar");
    try {
      byte[] content = loadDefaultAvatarContent();
      userDao.saveAvatar(new Avatar(AvatarId.DEFAULT_AVATAR_ID, content));
      ensureDefaultAvatarUserExists();
    } catch (Exception e) {
      log.warn("Failed to initialize default avatar: {}", e.getMessage(), e);
    }
  }
}
