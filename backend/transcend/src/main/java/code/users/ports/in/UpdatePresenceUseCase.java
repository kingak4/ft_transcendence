package code.users.ports.in;

import java.util.UUID;

public interface UpdatePresenceUseCase {
  void setUserOnline(SetUserOnlineCommand command);

  void setUserOffline(SetUserOfflineCommand command);

  record SetUserOnlineCommand(String sessionId, UUID userId, String deviceInfo) {}

  record SetUserOfflineCommand(String sessionId, UUID userId) {}
}
