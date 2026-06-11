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
      ensureDefaultAvatarUserExists();
      byte[] content = loadDefaultAvatarContent();
      userDao.saveAvatar(UserDetails.DEFAULT_AVATAR_USER_ID, new Avatar(UserDetails.DEFAULT_AVATAR_ID, content));
    } catch (Exception e) {
      log.warn("Failed to initialize default avatar: {}", e.getMessage(), e);
    }
  }

  private void ensureDefaultAvatarUserExists() {
    UserId defaultAvatarUserId = UserDetails.DEFAULT_AVATAR_USER_ID;
    if (userDao.findById(defaultAvatarUserId).isPresent()) {
      return;
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