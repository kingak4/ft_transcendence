package code.users.ports.in;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UpdatePresenceUseCase {
  void setUserOnline(SetUserOnlineCommand command);

  void setUserOffline(SetUserOfflineCommand command);

  record SetUserOnlineCommand(String sessionId, UUID userId, String deviceInfo) {}

  record SetUserOfflineCommand(String sessionId, UUID userId) {}
}