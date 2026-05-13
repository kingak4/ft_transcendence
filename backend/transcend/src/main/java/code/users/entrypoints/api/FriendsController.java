package code.users.entrypoints.api;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/friends")
@RequiredArgsConstructor
@Tag(name = "Friends", description = "Endpoints for managing user friends")
public class FriendsController {

  private final ManageFriendsUseCase manageFriendsUseCase;

  @PostMapping("/{friendId}")
  @Operation(summary = "Add a friend")
  public ResponseEntity<Void> addFriend(
      @PathVariable UUID userId,
      @PathVariable UUID friendId) {
    manageFriendsUseCase.addFriend(new UserId(userId), new UserId(friendId));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{friendId}")
  @Operation(summary = "Remove a friend")
  public ResponseEntity<Void> removeFriend(
      @PathVariable UUID userId,
      @PathVariable UUID friendId) {
    manageFriendsUseCase.removeFriend(new UserId(userId), new UserId(friendId));
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Get list of friends with pagination")
  public ResponseEntity<Map<FriendId, UserDetails>> getFriendList(
      @PathVariable UUID userId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Map<FriendId, UserDetails> friends = manageFriendsUseCase.getFriendList(new UserId(userId), page, size);
    return ResponseEntity.ok(friends);
  }
}