package code.users.entrypoints.http;

import static code.users.entrypoints.http.FriendsController.FRIENDS_ENDPOINT;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FRIENDS_ENDPOINT)
@ApiResponses(
    value = {
      @ApiResponse(
          responseCode = "404",
          content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
@RequiredArgsConstructor
public class FriendsController {
  public static final String FRIENDS_ENDPOINT = "/friends";
  public static final String FRIEND_ENDPOINT = "/{friendId}";

  private final ManageFriendsUseCase manageFriendsUseCase;

  @PostMapping(FRIEND_ENDPOINT)
  @Operation(summary = "Add a friend")
  @ApiResponse(responseCode = "201", description = "Friend added successfully")
  public ResponseEntity<Void> addFriend(
      Authentication authentication, @PathVariable UUID friendId) {
    UUID userId = UUID.fromString(authentication.getName());
    manageFriendsUseCase.addFriend(UserId.of(userId), FriendId.of(friendId));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping(FRIEND_ENDPOINT)
  @Operation(summary = "Remove a friend")
  @ApiResponse(responseCode = "204", description = "Friend removed successfully")
  public ResponseEntity<Void> removeFriend(
      Authentication authentication, @PathVariable UUID friendId) {
    UUID userId = UUID.fromString(authentication.getName());
    manageFriendsUseCase.removeFriend(UserId.of(userId), FriendId.of(friendId));
    return ResponseEntity.noContent().build();
  }
}
