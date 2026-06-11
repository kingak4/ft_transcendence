package code.users.entrypoints.http;

import code.users.domain.model.AvatarId;
import code.users.domain.model.UserId;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.in.UpdateAvatarUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static code.users.entrypoints.http.AvatarController.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@ApiResponses(
    value = {
        @ApiResponse(
            responseCode = "404",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
class AvatarController {

  private final UpdateAvatarUseCase updateAvatarUseCase;
  private final GetProfileUseCase getProfileUseCase;

  public static final String BASE_URL = "users";
  public static final String UPDATE_AVATAR_ENDPOINT = "/avatar";
  public static final String AVATAR_ENDPOINT = "/avatar/{avatarId}";

  @PostMapping(value = UPDATE_AVATAR_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a profile avatar")
  @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully")
  public ResponseEntity<Void> uploadAvatar(
      Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UUID userId = UUID.fromString(authentication.getName());
    updateAvatarUseCase.updateAvatar(
        UserId.of(userId),
        new UpdateAvatarUseCase.UpdateAvatarCommand(file.getOriginalFilename(), file.getBytes()));
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = AVATAR_ENDPOINT, produces = MediaType.IMAGE_JPEG_VALUE)
  @Operation(summary = "Get the profile avatar of the user")
  @ApiResponse(responseCode = "200", description = "User's profile avatar")
  public ResponseEntity<byte[]> getAvatar(@PathVariable UUID avatarId) {
    byte[] avatar = getProfileUseCase.getAvatar(AvatarId.of(avatarId)).content();
    return ResponseEntity.ok(avatar);
  }
}