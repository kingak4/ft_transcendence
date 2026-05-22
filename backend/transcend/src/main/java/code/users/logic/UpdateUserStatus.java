package code.users.logic;

import code.users.ports.in.UpdateUserStatusUseCase;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class UpdateUserStatus implements UpdateUserStatusUseCase {
  private final Map<String, String> sessionToUserMap = new ConcurrentHashMap<>();

  @Override
  public void setUserOnline(String username, String sessionId) {
    log.info("User {} is online (session: {})", username, sessionId);
    sessionToUserMap.put(sessionId, username);
  }

  @Override
  public void setUserOffline(String sessionId) {
    String username = sessionToUserMap.remove(sessionId);
    if (username != null) {
      log.info("User {} is offline (session: {})", username, sessionId);
    } else {
      log.warn("Unknown user disconnected (session: {})", sessionId);
    }
  }
}
