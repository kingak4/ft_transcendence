package code.users.ports.in;

import code.users.infrastructure.validation.ValidPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public interface RegisterUseCase {

  RegisteredUser register(@Valid RegisterCommand command);

  record RegisterCommand(
      @NotBlank(message = "Email cannot be blank")
          @Email(message = "Email should be valid")
          @Pattern(regexp = "^\\S+$", message = "Email cannot contain spaces")
          String email,
      @NotBlank(message = "Password cannot be blank") @ValidPassword String rawPassword) {}

  record RegisteredUser(UUID id) {}
}
