package code.chat.entrypoints.http;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import code.chat.domain.model.UserId;
import code.chat.ports.in.GetChatMessagesUseCase;
import code.chat.ports.in.GetChatsUseCase;
import code.chat.ports.in.StartChatUseCase;
import io.swagger.v3.oas.annotations.Operation;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ChatController.BASE_URL)
@RequiredArgsConstructor
public class ChatController {
  public static final String BASE_URL = "/chats";
  public static final String START_CHAT_ENDPOINT = "/{recipientId}";
  public static final String CHAT_MESSAGES_ENDPOINT = "/{chatId}/messages";

  private final StartChatUseCase startChatUseCase;
  private final GetChatsUseCase getChatsUseCase;
  private final GetChatMessagesUseCase getChatMessagesUseCase;

  @PostMapping(START_CHAT_ENDPOINT)
  @Operation(summary = "Start a chat between two users")
  public ResponseEntity<StartChatResponse> startChat(
      @PathVariable UUID recipientId, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    ChatId chatId =
        startChatUseCase.startChat(
            new StartChatUseCase.StartChatCommand(UserId.of(userId), UserId.of(recipientId)));
    return ResponseEntity.status(HttpStatus.CREATED).body(new StartChatResponse(chatId.val()));
  }

  @GetMapping
  @Operation(summary = "Get chats for a user with pagination")
  public ResponseEntity<List<ChatResponse>> getChats(
      Authentication authentication,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    UUID userId = UUID.fromString(authentication.getName());
    List<ChatResponse> chats =
        getChatsUseCase.getChatList(UserId.of(userId), page, size).stream()
            .map(chatId -> new ChatResponse(chatId.val()))
            .toList();
    return ResponseEntity.ok(chats);
  }

  @GetMapping(CHAT_MESSAGES_ENDPOINT)
  @Operation(summary = "Get messages in a chat with pagination")
  public ResponseEntity<List<MessageResponse>> getChatMessages(
      @PathVariable UUID chatId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    List<MessageResponse> messages =
        getChatMessagesUseCase.getChatMessages(ChatId.of(chatId), page, size).stream()
            .map(ChatController::toResponse)
            .toList();
    return ResponseEntity.ok(messages);
  }

  private static MessageResponse toResponse(Message message) {
    return new MessageResponse(
        message.getId().val(),
        message.getSenderId().val(),
        message.getContent(),
        message.getCreatedAt());
  }

  public record StartChatResponse(UUID chatId) {}

  public record ChatResponse(UUID chatId) {}

  public record MessageResponse(
      UUID messageId, UUID senderId, String content, OffsetDateTime createdAt) {}
}
