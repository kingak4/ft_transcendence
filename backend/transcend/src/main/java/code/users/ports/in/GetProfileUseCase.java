package code.users.ports.in;

import code.users.domain.model.Avatar;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;

public interface GetProfileUseCase {
  UserDetails getDetails(UserId userId);

  Avatar getAvatar(UserId userId);
}
