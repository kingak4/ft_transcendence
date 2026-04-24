package code.users.entrypoints.api;

import code.users.domain.model.UserId;
import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.ports.in.UpdateUsernameUseCase;
import jakarta.validation.Valid;
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
  public static final String UPDATE_USERNAME_ENDPOINT = "/{userId}/username";
  private final UpdateUsernameUseCase updateUsernameUseCase;
  private final UsersApiMapper mapper;

  @PatchMapping(UPDATE_USERNAME_ENDPOINT)
  public ResponseEntity<Void> updateUsername(
      @PathVariable UUID userId,
      @Valid @RequestBody UpdateUsernameRequest request) {
    updateUsernameUseCase.updateUsername(new UserId(userId), mapper.toCommand(request));
    return ResponseEntity.ok().build();
  }

  public record UpdateUsernameRequest(String username) {}
}