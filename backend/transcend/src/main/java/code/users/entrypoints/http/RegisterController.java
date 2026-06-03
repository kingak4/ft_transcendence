package code.users.entrypoints.http;

import code.users.entrypoints.http.mappers.UsersApiMapper;
import code.users.ports.in.RegisterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.annotation.security.PermitAll;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RegisterController.BASE_URL)
@RequiredArgsConstructor
public class RegisterController {

  public static final String BASE_URL = "users";
  public static final String REGISTER_ENDPOINT = "/register";

  private final RegisterUseCase registerUseCase;
  private final UsersApiMapper mapper;

  @PostMapping(REGISTER_ENDPOINT)
  @Operation(summary = "Register a new API user")
  @SecurityRequirements()
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(
            responseCode = "409",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  @PermitAll
  public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
    var result = registerUseCase.register(mapper.toCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
  }

  public record RegisterRequest(String email, String password) {}

  public record RegisterResponse(UUID id) {}
}
