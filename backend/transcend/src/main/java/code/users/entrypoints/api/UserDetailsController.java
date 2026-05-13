package code.users.entrypoints.api;

import code.users.domain.model.UserId;
import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(UserDetailsController.BASE_URL)
@RequiredArgsConstructor
public class UserDetailsController {

  public static final String BASE_URL = "users";
  public static final String UPDATE_DISPLAY_NAME_ENDPOINT = "/{userId}/display-name";
  public static final String UPDATE_AVATAR_ENDPOINT = "/{userId}/avatar";

  private final UpdateDisplayNameUseCase updateDisplayNameUseCase;
  private final UpdateAvatarUseCase updateAvatarUseCase;
  private final UsersApiMapper mapper;

  @PatchMapping(UPDATE_DISPLAY_NAME_ENDPOINT)
  @Operation(summary = "Change the display name of the user")
  public ResponseEntity<Void> updateDisplayName(
      @PathVariable UUID userId, @RequestBody UpdateDisplayNameRequest request) {
    updateDisplayNameUseCase.updateDisplayName(new UserId(userId), mapper.toCommand(request));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = UPDATE_AVATAR_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a profile avatar")
  public ResponseEntity<Void> uploadAvatar(
      @PathVariable UUID userId, @RequestParam("file") MultipartFile file) throws IOException {
    updateAvatarUseCase.updateAvatar(
        new UserId(userId),
        new UpdateAvatarUseCase.UpdateAvatarCommand(file.getOriginalFilename(), file.getBytes()));
    return ResponseEntity.ok().build();
  }

  public record UpdateDisplayNameRequest(String displayName) {}
}
