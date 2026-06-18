package code.users.entrypoints.websocket;

import static code.shared.config.WebSocketTest.TIMEOUT;
import static code.shared.domain.model.WebSocketFixtures.ID_FIXTURE;
import static code.shared.domain.model.WebSocketFixtures.SESSION_FIXTURE;
import static code.users.entrypoints.websocket.UserWebSocketConfig.userPresenceTopic;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

import code.users.ports.in.UpdatePresenceUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
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
    // Given
    UUID userId = ID_FIXTURE;
    String sessionId = SESSION_FIXTURE;
    String userAgent = "test-agent";

    // When
    listener.handleWebSocketConnectListener(connectEvent(sessionId, userId, userAgent));

    // Then
    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () -> {
              verify(updatePresenceUseCase)
                  .setUserOnline(
                      new UpdatePresenceUseCase.SetUserOnlineCommand(sessionId, userId, userAgent));
              verify(messagingTemplate)
                  .convertAndSend(
                      Mockito.eq(userPresenceTopic(userId)),
                      Mockito.eq(
                          new PresenceWebSocketController.PresenceStatusResponse(userId, true)));
            });

    clearInvocations(updatePresenceUseCase, messagingTemplate);

    // When
    listener.handleWebSocketDisconnectListener(disconnectEvent(sessionId, userId));

    // Then
    await()
        .atMost(TIMEOUT)
        .untilAsserted(
            () -> {
              verify(updatePresenceUseCase)
                  .setUserOffline(
                      new UpdatePresenceUseCase.SetUserOfflineCommand(sessionId, userId));
              verify(messagingTemplate)
                  .convertAndSend(
                      Mockito.eq(userPresenceTopic(userId)),
                      Mockito.eq(
                          new PresenceWebSocketController.PresenceStatusResponse(userId, false)));
            });
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
    var principal = new UsernamePasswordAuthenticationToken(userId.toString(), null);
    accessor.setUser(principal);

    return new SessionDisconnectEvent(
        this, message(accessor), sessionId, CloseStatus.NORMAL, principal);
  }

  private Message<byte[]> message(StompHeaderAccessor accessor) {
    return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
  }
}