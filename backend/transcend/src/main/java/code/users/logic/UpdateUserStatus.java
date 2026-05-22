package code.users.logic;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateUserStatusUseCase;
import code.users.ports.out.SessionDao;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class UpdateUserStatus implements UpdateUserStatusUseCase {

  private final SessionDao sessionDao;

  @Override
  @PreAuthorize(
      "hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.userId())")
  public void setUserOnline(SetUserOnlineCommand command) {
    Session session =
        Session.builder()
            .id(SessionId.of(command.sessionId()))
            .userId(new UserId(command.userId()))
            .deviceInfo(command.deviceInfo())
            .createdAt(OffsetDateTime.now())
            .build();

    sessionDao.saveSession(session);
  }

  @Override
  @PreAuthorize(
      "hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.userId())")
  public void setUserOffline(SetUserOfflineCommand command) {
    SessionId sessionId = SessionId.of(command.sessionId());

    sessionDao
        .findById(sessionId)
        .ifPresent(
            session -> {
              sessionDao.deleteSession(sessionId);
            });
  }
}
