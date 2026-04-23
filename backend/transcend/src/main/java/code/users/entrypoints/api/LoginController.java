package code.users.entrypoints.api;

import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.ports.in.LoginUseCase;
import code.users.ports.in.LoginUseCase.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class LoginController {

  private final LoginUseCase loginUseCase;
  private final UsersApiMapper mapper;

  @PostMapping("login")
  @Operation(summary = "Authenticate and issue JWT")
  @SecurityRequirements
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    LoginResult result = loginUseCase.login(mapper.toCommand(request));
    return mapper.toResponse(result);
  }

  public record LoginRequest(String email, String password) {}

  public record LoginResponse(String accessToken, String tokenType) {}
}
