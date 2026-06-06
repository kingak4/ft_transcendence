package code.users.entrypoints.http;

import static code.users.entrypoints.http.FriendsController.FRIENDS_ENDPOINT;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FRIENDS_ENDPOINT)
@RequiredArgsConstructor
public class FriendsPaginationController {

  private final ManageFriendsUseCase manageFriendsUseCase;

  @GetMapping
  @Operation(summary = "Get list of friends with pagination")
  @ApiResponse(responseCode = "200", description = "List of friends")
  public ResponseEntity<Map<FriendId, UserDetails>> getFriendList(
      Authentication authentication,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    UUID userId = UUID.fromString(authentication.getName());
    Map<FriendId, UserDetails> friends =
        manageFriendsUseCase.getFriendList(UserId.of(userId), page, size);
    return ResponseEntity.ok(friends);
  }
}
