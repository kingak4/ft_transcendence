package code.users.entrypoints.http;

import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.entrypoints.http.mappers.UsersApiMapper;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@ApiResponses(
    value = {
      @ApiResponse(
          responseCode = "404",
          content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
public class UserDetailsController {

  public static final String BASE_URL = "users";
  public static final String UPDATE_DISPLAY_NAME_ENDPOINT = "/{userId}/display-name";
  public static final String AVATAR_ENDPOINT = "/{userId}/avatar";
  public static final String DETAILS_ENDPOINT = "/{userId}/details";

  private final UpdateDisplayNameUseCase updateDisplayNameUseCase;
  private final UpdateAvatarUseCase updateAvatarUseCase;
  private final GetProfileUseCase getProfileUseCase;
  private final UsersApiMapper mapper;

  @PatchMapping(UPDATE_DISPLAY_NAME_ENDPOINT)
  @Operation(summary = "Change the display name of the user")
  public ResponseEntity<Void> updateDisplayName(
      @PathVariable UUID userId, @RequestBody UpdateDisplayNameRequest request) {
    updateDisplayNameUseCase.updateDisplayName(UserId.of(userId), mapper.toCommand(request));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = AVATAR_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a profile avatar")
  public ResponseEntity<Void> uploadAvatar(
      @PathVariable UUID userId, @RequestParam("file") MultipartFile file) throws IOException {
    updateAvatarUseCase.updateAvatar(
        UserId.of(userId),
        new UpdateAvatarUseCase.UpdateAvatarCommand(file.getOriginalFilename(), file.getBytes()));
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = AVATAR_ENDPOINT, produces = MediaType.IMAGE_JPEG_VALUE)
  @Operation(summary = "Get the profile avatar of the user")
  public ResponseEntity<byte[]> getAvatar(@PathVariable UUID userId) {
    byte[] avatar = getProfileUseCase.getAvatar(UserId.of(userId)).content();
    return ResponseEntity.ok(avatar);
  }

  @GetMapping(DETAILS_ENDPOINT)
  @Operation(summary = "Get the details of the user")
  public ResponseEntity<GetUserDetailsResponse> getDetails(@PathVariable UUID userId) {
    UserDetails details = getProfileUseCase.getDetails(UserId.of(userId));
    return ResponseEntity.ok(mapper.toResponse(details));
  }

  public record UpdateDisplayNameRequest(String displayName) {}

  public record GetUserDetailsResponse(String displayName, String avatarUrl) {}
}
