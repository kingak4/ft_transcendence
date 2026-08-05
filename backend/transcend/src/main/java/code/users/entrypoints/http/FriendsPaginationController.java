package code.users.entrypoints.http;

import static code.users.entrypoints.http.FriendsController.FRIENDS_ENDPOINT;

import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(FRIENDS_ENDPOINT)
@RequiredArgsConstructor
public class FriendsPaginationController {

  private final ManageFriendsUseCase manageFriendsUseCase;
  private static final int MAX_PAGE_SIZE = 20;

  @GetMapping
  @Operation(summary = "Get list of friends with pagination")
  @ApiResponse(responseCode = "200", description = "List of friends")
  public ResponseEntity<Page<ManageFriendsUseCase.FriendResult>> getFriendList(
      Authentication authentication,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    if (page < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0");
    }
    if (size < 1 || size > MAX_PAGE_SIZE) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "size must be between 1 and " + MAX_PAGE_SIZE);
    }

    UUID userId = UUID.fromString(authentication.getName());
    Pageable pageable = PageRequest.of(page, size);
    Page<ManageFriendsUseCase.FriendResult> friends =
        manageFriendsUseCase.getFriendList(UserId.of(userId), pageable);
    return ResponseEntity.ok(friends);
  }
}
