package code.entrypoints.api;

import code.modules.users.ports.in.LoginUser;
import code.modules.users.ports.in.LoginUser.LoginResult;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class LoginRestController {

  private final LoginUser loginUser;

  @PostMapping("login")
  @Operation(summary = "Authenticate and issue JWT")
  @SecurityRequirements
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
      LoginResult result = loginUser.login(
          new LoginUser.LoginCommand(request.email(), request.password())
      );
      return new LoginResponse(result.accessToken(), result.tokenType());
  }

  public record LoginRequest(
      String email,
      String password
  ) {
  }

  public record LoginResponse(
      String accessToken,
      String tokenType
  ) {
  }
}