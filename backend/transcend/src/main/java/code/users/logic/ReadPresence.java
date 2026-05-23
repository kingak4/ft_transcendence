package code.users.logic;

import code.users.domain.model.UserId;
import code.users.ports.in.ReadOnlineStatusUseCase;
import code.users.ports.out.PresenceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadPresence implements ReadOnlineStatusUseCase {

  private final PresenceDao presenceDao;

  @Override
  public boolean isOnline(UserId userId) {
    return presenceDao.isUserOnline(userId);
  }
}
