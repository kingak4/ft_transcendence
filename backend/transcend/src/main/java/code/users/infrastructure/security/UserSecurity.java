package code.users.infrastructure.security;

import code.users.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

  public boolean isSameUser(Authentication authentication, UserId userId) {
    if (authentication == null || authentication.getName() == null) {
      return false;
    }
    String userIdFromAuth = authentication.getName();
    return userIdFromAuth.equals(userId.getVal().toString());
  }
}
