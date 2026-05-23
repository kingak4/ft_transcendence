package code.users.entrypoints.websocket;

import code.users.domain.model.UserId;
import code.users.ports.in.ReadOnlineStatusUseCase;
import io.github.springwolf.core.asyncapi.annotations.AsyncMessage;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
class PresenceWebSocketController {

  private final ReadOnlineStatusUseCase readOnlineStatusUseCase;

  @MessageMapping("/presence/check")
  @SendTo("/topic/user/{userId}/presence")
  @AsyncPublisher(
      operation =
          @AsyncOperation(
              channelName = "/app/presence/check",
              description =
                  "Subscribe to check if a user is online. Authentication is enforced via JWT bearer token. "
                      + "Only authenticated users can call this endpoint. "
                      + "Returns the online status for a specific user in real-time.",
              message =
                  @AsyncMessage(
                      name = "CheckPresenceRequest",
                      description = "Request to check the online presence status of a user")))
  public PresenceStatusResponse checkPresence(
      @Payload CheckPresenceRequest request, SimpMessageHeaderAccessor headerAccessor) {
    log.debug("Received presence check request for userId: {}", request.userId());

    if (headerAccessor.getUser() == null) {
      log.warn("Unauthorized presence check attempt");
      return new PresenceStatusResponse(request.userId(), false, "UNAUTHORIZED");
    }

    try {
      UserId userId = UserId.of(UUID.fromString(request.userId()));
      boolean isOnline = readOnlineStatusUseCase.isOnline(userId);

      log.info("User {} presence status: {}", request.userId(), isOnline);
      return new PresenceStatusResponse(request.userId(), isOnline, "SUCCESS");
    } catch (IllegalArgumentException e) {
      log.error("Invalid userId format: {}", request.userId(), e);
      return new PresenceStatusResponse(request.userId(), false, "INVALID_USER_ID");
    } catch (Exception e) {
      log.error("Error checking presence for user: {}", request.userId(), e);
      return new PresenceStatusResponse(request.userId(), false, "ERROR");
    }
  }

  public record CheckPresenceRequest(String userId) {}

  public record PresenceStatusResponse(String userId, boolean isOnline, String status) {}
}
