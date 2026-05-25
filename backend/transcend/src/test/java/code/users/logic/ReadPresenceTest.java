package code.users.logic;

import code.users.domain.model.UserId;
import code.users.ports.in.ReadPresenceUseCase;
import code.users.ports.out.PresenceDao;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(ReadPresenceTest.ReadPresenceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ReadPresenceTest {

  @Configuration
  @Import(ReadPresence.class)
  static class ReadPresenceTestConfig {}

  private final ReadPresenceUseCase readPresenceUseCase;

  @MockitoBean private PresenceDao presenceDao;

  @Test
  void returnsOnlineStatusWhenUserIsOnline() {
    UserId userId = UserId.of(new UUID(0, 1));
    boolean result = readPresenceUseCase.isOnline(userId);
  }

  @Test
  void returnsOfflineStatusWhenUserIsNotOnline() {
    UserId userId = UserId.of(new UUID(0, 2));
    boolean result = readPresenceUseCase.isOnline(userId);
  }
}
