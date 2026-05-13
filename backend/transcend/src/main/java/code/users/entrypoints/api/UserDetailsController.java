package code.users.entrypoints.api;

import org.springframework.http.ProblemDetail;
import code.users.domain.model.UserId;
import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.ports.in.UpdateDisplayNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserDetailsController.BASE_URL)
@RequiredArgsConstructor
public class UserDetailsController {

  public static final String BASE_URL = "users";
  public static final String UPDATE_DISPLAY_NAME_ENDPOINT = "/{userId}/display-name";
  private final UpdateDisplayNameUseCase updateDisplayNameUseCase;
  private final UsersApiMapper mapper;

  @PatchMapping(UPDATE_DISPLAY_NAME_ENDPOINT)
  @Operation(summary = "Change the display name of the user")
  public ResponseEntity<Void> updateDisplayName(
      @PathVariable UUID userId, @RequestBody UpdateDisplayNameRequest request) {
    updateDisplayNameUseCase.updateDisplayName(new UserId(userId), mapper.toCommand(request));
    return ResponseEntity.ok().build();
  }

  public record UpdateDisplayNameRequest(String displayName) {}
}
