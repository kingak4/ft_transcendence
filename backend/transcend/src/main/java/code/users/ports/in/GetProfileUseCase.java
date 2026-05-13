package code.users.ports.in;

import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;

public interface GetProfileUseCase {
  UserDetails getDetails(UserId userId);

  byte[] getAvatar(UserId userId);
}
