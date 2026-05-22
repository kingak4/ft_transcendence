package code.users.entrypoints.http;

import code.users.entrypoints.http.mappers.UsersApiMapper;
import code.users.ports.in.LoginUseCase;
import code.users.ports.in.LoginUseCase.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(LoginController.BASE_URL)
@RequiredArgsConstructor
public class LoginController {

  public static final String BASE_URL = "users";
  public static final String LOGIN_ENDPOINT = "login";

  private final LoginUseCase loginUseCase;
  private final UsersApiMapper mapper;

  @PostMapping(LOGIN_ENDPOINT)
  @Operation(summary = "Authenticate and issue JWT")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "401",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "404",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  @SecurityRequirements()
  @PermitAll
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResult result = loginUseCase.login(mapper.toCommand(request));
    return ResponseEntity.status(HttpStatus.OK).body(mapper.toResponse(result));
  }

  public record LoginRequest(String email, String password) {}

  public record LoginResponse(String accessToken, String tokenType, String userId) {}
}
