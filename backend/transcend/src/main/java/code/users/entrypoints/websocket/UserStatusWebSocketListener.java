package code.users.entrypoints.websocket;

import code.users.ports.in.UpdatePresenceUseCase;
import code.users.ports.in.UpdatePresenceUseCase.SetUserOfflineCommand;
import code.users.ports.in.UpdatePresenceUseCase.SetUserOnlineCommand;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
class UserStatusWebSocketListener {

  private final UpdatePresenceUseCase updatePresenceUseCase;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();
    Principal principal = headerAccessor.getUser();

    if (sessionId != null && principal != null && principal.getName() != null) {
      try {
        UUID userId = UUID.fromString(principal.getName());
        String deviceInfo = extractDeviceInfo(headerAccessor);

        SetUserOnlineCommand command = new SetUserOnlineCommand(sessionId, userId, deviceInfo);
        updatePresenceUseCase.setUserOnline(command);
      } catch (IllegalArgumentException ignored) {
      }
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();
    Principal principal = headerAccessor.getUser();

    if (sessionId != null && principal != null) {
      UUID userId = UUID.fromString(principal.getName());
      SetUserOfflineCommand command = new SetUserOfflineCommand(sessionId, userId);
      updatePresenceUseCase.setUserOffline(command);
    }
  }

  private String extractDeviceInfo(StompHeaderAccessor headerAccessor) {
    String userAgent = headerAccessor.getFirstNativeHeader("user-agent");
    return userAgent != null ? userAgent : "Unknown Device";
  }
}
