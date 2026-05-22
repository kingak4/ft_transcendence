package code.users.ports.in;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UpdateUserStatusUseCase {
  @PreAuthorize(
      "hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.userId())")
  void setUserOnline(SetUserOnlineCommand command);

  @PreAuthorize(
      "hasRole('ADMIN') or @ownershipValidator.isSameUser(authentication, #command.userId())")
  void setUserOffline(SetUserOfflineCommand command);

  record SetUserOnlineCommand(String sessionId, UUID userId, String deviceInfo) {}

  record SetUserOfflineCommand(String sessionId, UUID userId) {}
}