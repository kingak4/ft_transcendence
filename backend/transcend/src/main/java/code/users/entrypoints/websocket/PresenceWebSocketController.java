package code.users.entrypoints.websocket;

import code.users.domain.model.UserId;
import code.users.ports.in.ReadPresenceUseCase;
import io.github.springwolf.bindings.stomp.annotations.StompAsyncOperationBinding;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PresenceWebSocketController {

  public static final String PRESENCE_CHECK = "/presence/check";

  @StompAsyncOperationBinding
  @MessageMapping(PRESENCE_CHECK)
  public void checkPresence(@Payload CheckPresenceRequest request) {
    UserId userId = UserId.of(request.userId());
    boolean isOnline = readPresenceUseCase.isOnline(userId);
    messagingTemplate.convertAndSend(
        WebSocketConfiguration.userPresenceTopic(userId.val()),
        new PresenceStatusResponse(userId.val(), isOnline));
  }

  private final ReadPresenceUseCase readPresenceUseCase;
  private final SimpMessagingTemplate messagingTemplate;

  public record CheckPresenceRequest(UUID userId) {}

  public record PresenceStatusResponse(UUID userId, boolean isOnline) {}
}
