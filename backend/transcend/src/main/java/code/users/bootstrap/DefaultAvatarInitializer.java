package code.users.bootstrap;

import code.users.domain.model.Avatar;
import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAvatarInitializer implements CommandLineRunner {

  private final UserDao userDao;
  public static final UserId DEFAULT_AVATAR_USER_ID = new UserId(new UUID(0, 0));

  @Override
  public void run(String... args) throws Exception {
    ClassPathResource resource = new ClassPathResource("default-avatar.png");
    if (resource.exists()) {
      try (InputStream is = resource.getInputStream()) {
        byte[] content = is.readAllBytes();
        userDao.saveAvatar(DEFAULT_AVATAR_USER_ID, new Avatar(content));
      }
    } else {
      userDao.saveAvatar(DEFAULT_AVATAR_USER_ID, new Avatar(new byte[] {0}));
    }
  }
}
