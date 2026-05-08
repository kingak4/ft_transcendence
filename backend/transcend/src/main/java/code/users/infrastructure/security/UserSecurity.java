package code.users.infrastructure.security;

import code.users.domain.model.UserId;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

  private final UserDao userDao;

  public boolean isSameUser(Authentication authentication, UserId userId) {
    if (authentication == null || authentication.getName() == null) {
      return false;
    }
    String email = authentication.getName();
    return userDao.findByEmail(email).map(user -> user.getId().equals(userId)).orElse(false);
  }
}
