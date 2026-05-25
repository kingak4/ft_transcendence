package code.users.entrypoints.websocket;

import static code.users.entrypoints.websocket.WebSocketConfiguration.userPresenceTopic;
import static org.mockito.Mockito.verify;

import code.users.domain.model.UserFixtures;
import code.users.ports.in.UpdatePresenceUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@ExtendWith(MockitoExtension.class)
class UserStatusWebSocketListenerTest {

  @Mock private UpdatePresenceUseCase updatePresenceUseCase;
  @Mock private SimpMessagingTemplate messagingTemplate;

  @InjectMocks private UserStatusWebSocketListener listener;

  @Test
  void shouldBroadcastPresenceOnConnectAndDisconnect() {
    UUID userId = UserFixtures.ID_FIXTURE;
    String sessionId = UserFixtures.SESSION_FIXTURE;

    listener.handleWebSocketConnectListener(connectEvent(sessionId, userId, "test-agent"));
    verify(updatePresenceUseCase)
        .setUserOnline(
            new UpdatePresenceUseCase.SetUserOnlineCommand(sessionId, userId, "test-agent"));
    verify(messagingTemplate)
        .convertAndSend(
            userPresenceTopic(userId),
            new PresenceWebSocketController.PresenceStatusResponse(userId, true));

    listener.handleWebSocketDisconnectListener(disconnectEvent(sessionId, userId));
    verify(updatePresenceUseCase)
        .setUserOffline(new UpdatePresenceUseCase.SetUserOfflineCommand(sessionId, userId));
    verify(messagingTemplate)
        .convertAndSend(
            userPresenceTopic(userId),
            new PresenceWebSocketController.PresenceStatusResponse(userId, false));
  }

  private SessionConnectEvent connectEvent(String sessionId, UUID userId, String userAgent) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setSessionId(sessionId);
    accessor.setUser(new UsernamePasswordAuthenticationToken(userId.toString(), null));
    accessor.addNativeHeader("user-agent", userAgent);
    return new SessionConnectEvent(this, message(accessor));
  }

  private SessionDisconnectEvent disconnectEvent(String sessionId, UUID userId) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
    accessor.setSessionId(sessionId);
    UsernamePasswordAuthenticationToken principal =
        new UsernamePasswordAuthenticationToken(userId.toString(), null);
    accessor.setUser(principal);
    return new SessionDisconnectEvent(
        this, message(accessor), sessionId, CloseStatus.NORMAL, principal);
  }

  private Message<byte[]> message(StompHeaderAccessor accessor) {
    MessageHeaders headers = accessor.getMessageHeaders();
    return MessageBuilder.createMessage(new byte[0], headers);
  }
}
