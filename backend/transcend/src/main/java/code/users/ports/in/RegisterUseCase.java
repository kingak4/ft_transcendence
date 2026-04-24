package code.users.ports.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public interface RegisterUseCase {

  RegisteredUser register(@Valid RegisterCommand command);

  record RegisterCommand(
      @NotBlank(message = "Email cannot be blank")
      @Email(message = "Email should be valid")
      String email,
      @NotBlank(message = "Password cannot be blank")
      @Size(min = 8, message = "Password must be at least 8 characters long")
      String rawPassword
  ) {}

  record RegisteredUser(UUID id) {}
}