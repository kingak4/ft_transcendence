package code.users.ports.in;

import code.users.domain.model.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface UpdateUsernameUseCase {

  void updateUsername(UserId userId, @Valid UpdateUsernameCommand command);

  record UpdateUsernameCommand(
      @NotBlank(message = "Username cannot be blank")
      @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
      @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Username can only contain letters, numbers, underscores, or periods")
      String username) {}
}
