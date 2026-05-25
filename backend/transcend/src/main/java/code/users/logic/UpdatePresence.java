package code.users.logic;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdatePresenceUseCase;
import code.users.ports.out.PresenceDao;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class UpdatePresence implements UpdatePresenceUseCase {

  private final PresenceDao presenceDao;

  @Override
  public void setUserOnline(SetUserOnlineCommand command) {
    Session session =
        Session.builder()
            .id(SessionId.of(command.sessionId()))
            .userId(new UserId(command.userId()))
            .deviceInfo(command.deviceInfo())
            .createdAt(OffsetDateTime.now())
            .build();

    presenceDao.setSessionOnline(session);
  }

  @Override
  public void setUserOffline(SetUserOfflineCommand command) {
    UserId userId = UserId.of(command.userId());
    SessionId sessionId = SessionId.of(command.sessionId());
    presenceDao.removeSession(userId, sessionId);
  }
}
