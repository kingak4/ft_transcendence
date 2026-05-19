package code.users.entrypoints.websocket;

import code.users.ports.in.UpdateUserStatusUseCase;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
class UserStatusWebSocketListener {

  private final UpdateUserStatusUseCase updateUserStatusUseCase;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();

    // In a real application, the principal comes from the authenticated user
    Principal principal = headerAccessor.getUser();
    String username = (principal != null) ? principal.getName() : "anonymous-" + sessionId;

    // Fallback to checking headers if principal is not available (useful for simple testing)
    if (principal == null && headerAccessor.containsNativeHeader("username")) {
      username = headerAccessor.getFirstNativeHeader("username");
    }

    if (sessionId != null && username != null) {
      updateUserStatusUseCase.setUserOnline(username, sessionId);
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();

    if (sessionId != null) {
      updateUserStatusUseCase.setUserOffline(sessionId);
    }
  }
}
