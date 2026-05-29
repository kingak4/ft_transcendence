package code.users.entrypoints.http;

import static code.users.entrypoints.http.FriendsController.FRIENDS_ENDPOINT;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FRIENDS_ENDPOINT)
@RequiredArgsConstructor
public class FriendsController {
  public static final String FRIENDS_ENDPOINT = "/users/{userId}/friends";
  public static final String FRIEND_ENDPOINT = "/{friendId}";

  private final ManageFriendsUseCase manageFriendsUseCase;

  @PostMapping(FRIEND_ENDPOINT)
  @Operation(summary = "Add a friend")
  public ResponseEntity<Void> addFriend(@PathVariable UUID userId, @PathVariable UUID friendId) {
    manageFriendsUseCase.addFriend(UserId.of(userId), FriendId.of(friendId));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping(FRIEND_ENDPOINT)
  @Operation(summary = "Remove a friend")
  public ResponseEntity<Void> removeFriend(@PathVariable UUID userId, @PathVariable UUID friendId) {
    manageFriendsUseCase.removeFriend(UserId.of(userId), FriendId.of(friendId));
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Get list of friends with pagination")
  public ResponseEntity<Map<FriendId, UserDetails>> getFriendList(
      @PathVariable UUID userId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Map<FriendId, UserDetails> friends =
        manageFriendsUseCase.getFriendList(UserId.of(userId), page, size);
    return ResponseEntity.ok(friends);
  }
}
