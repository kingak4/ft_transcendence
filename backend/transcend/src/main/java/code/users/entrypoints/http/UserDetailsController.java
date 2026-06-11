package code.users.entrypoints.http;

import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.entrypoints.http.mappers.UsersApiMapper;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserDetailsController.BASE_URL)
@RequiredArgsConstructor
@ApiResponses(
    value = {
      @ApiResponse(
          responseCode = "404",
          content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
public class UserDetailsController {

  public static final String BASE_URL = "users";
  public static final String UPDATE_DISPLAY_NAME_ENDPOINT = "/display-name";
  public static final String DETAILS_ENDPOINT = "/{userId}/details";

  private final UpdateDisplayNameUseCase updateDisplayNameUseCase;
  private final GetProfileUseCase getProfileUseCase;
  private final UsersApiMapper mapper;

  @PatchMapping(UPDATE_DISPLAY_NAME_ENDPOINT)
  @Operation(summary = "Change the display name of the user")
  @ApiResponse(responseCode = "200", description = "Display name updated successfully")
  public ResponseEntity<Void> updateDisplayName(
      Authentication authentication, @RequestBody UpdateDisplayNameRequest request) {
    UUID userId = UUID.fromString(authentication.getName());
    updateDisplayNameUseCase.updateDisplayName(UserId.of(userId), mapper.toCommand(request));
    return ResponseEntity.ok().build();
  }

  @GetMapping(DETAILS_ENDPOINT)
  @Operation(summary = "Get the details of the user")
  @ApiResponse(responseCode = "200", description = "User's details")
  public ResponseEntity<GetUserDetailsResponse> getDetails(@PathVariable UUID userId) {
    UserDetails details = getProfileUseCase.getDetails(UserId.of(userId));
    return ResponseEntity.ok(mapper.toResponse(details));
  }

  public record UpdateDisplayNameRequest(String displayName) {}

  public record GetUserDetailsResponse(String displayName, UUID avatarId) {}
}
