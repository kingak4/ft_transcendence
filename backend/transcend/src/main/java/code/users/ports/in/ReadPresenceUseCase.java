package code.users.ports.in;

import code.users.domain.model.UserId;

public interface ReadPresenceUseCase {

  boolean isOnline(UserId userId);
}
