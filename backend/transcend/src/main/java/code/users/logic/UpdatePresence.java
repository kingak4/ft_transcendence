package code.users.logic;

import code.users.ports.in.UpdateUserStatusUseCase;
import code.users.ports.out.PresenceDao;
import java.util.UUID;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class UpdateUserStatus implements UpdateUserStatusUseCase {

  private final PresenceDao presenceDao;

  @Override
  public void setUserOnline(SetUserOnlineCommand command) {
    UUID userId = command.userId();
    String sessionId = command.sessionId();
    String deviceInfo = command.deviceInfo();

    presenceDao.setSessionOnline(userId, sessionId, deviceInfo);
  }

  @Override
  public void setUserOffline(SetUserOfflineCommand command) {
    UUID userId = command.userId();
    String sessionId = command.sessionId();
    presenceDao.removeSession(userId, sessionId);
  }
}