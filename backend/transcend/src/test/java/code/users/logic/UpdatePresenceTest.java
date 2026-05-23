package code.users.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdatePresenceUseCase;
import code.users.ports.out.PresenceDao;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(UpdatePresenceTest.UpdatePresenceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UpdatePresenceTest {

  @Configuration
  @Import(UpdatePresence.class)
  static class UpdatePresenceTestConfig {}

  private final UpdatePresenceUseCase service;

  @MockitoBean private PresenceDao presenceDao;

  @Test
  void setUserOnline_ConvertsCommandAndDelegatesToDao() {
    String sessionId = "sess-1";
    UUID userUuid = UUID.randomUUID();
    String deviceInfo = "web-client";

    var command = new UpdatePresenceUseCase.SetUserOnlineCommand(sessionId, userUuid, deviceInfo);

    service.setUserOnline(command);

    ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
    verify(presenceDao, times(1)).setSessionOnline(captor.capture());

    Session saved = captor.getValue();
    assertThat(saved.getId().val()).isEqualTo(sessionId);
    assertThat(saved.getUserId().val()).isEqualTo(userUuid);
    assertThat(saved.getDeviceInfo()).isEqualTo(deviceInfo);
  }

  @Test
  void setUserOffline_DelegatesToDao() {
    String sessionId = "sess-off";
    UUID userUuid = UUID.randomUUID();

    var command = new UpdatePresenceUseCase.SetUserOfflineCommand(sessionId, userUuid);

    service.setUserOffline(command);

    ArgumentCaptor<UserId> userCaptor = ArgumentCaptor.forClass(UserId.class);
    ArgumentCaptor<SessionId> sessionCaptor = ArgumentCaptor.forClass(SessionId.class);

    verify(presenceDao, times(1)).removeSession(userCaptor.capture(), sessionCaptor.capture());

    assertThat(userCaptor.getValue().val()).isEqualTo(userUuid);
    assertThat(sessionCaptor.getValue().val()).isEqualTo(sessionId);
  }
}
