package code.users.entrypoints.api;

import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.ports.in.RegisterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RegisterController.BASE_URL)
@RequiredArgsConstructor
public class RegisterController {

  public static final String BASE_URL = "users";
  public static final String REGISTER_ENDPOINT = "register";

  private final RegisterUseCase registerUseCase;
  private final UsersApiMapper mapper;

  @PostMapping(REGISTER_ENDPOINT)
  @Operation(summary = "Register a new API user")
  @SecurityRequirements
  public RegisterResponse login(@Valid @RequestBody RegisterRequest request) {
    var result = registerUseCase.register(mapper.toCommand(request));
    return mapper.toResponse(result);
  }

  public record RegisterRequest(String email, String password) {}

  public record RegisterResponse(UUID id) {}
}
