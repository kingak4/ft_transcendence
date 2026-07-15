package code.users.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ownershipValidator")
@RequiredArgsConstructor
public class OwnershipValidator {

  public boolean isSameUser(Authentication authentication, Object userId) {
    if (authentication == null || authentication.getName() == null || userId == null) {
      return false;
    }
    return authentication.getName().equals(userId.toString());
  }
}