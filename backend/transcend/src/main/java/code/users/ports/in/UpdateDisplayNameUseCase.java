package code.users.ports.in;

import code.users.domain.model.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface UpdateDisplayNameUseCase {

  void updateDisplayName(
      UserId userId, @Valid UpdateDisplayNameUseCase.UpdateDisplayNameCommand command);

  record UpdateDisplayNameCommand(
      @NotBlank(message = "DisplayName cannot be blank")
          @Size(min = 3, max = 20, message = "DisplayName must be between 3 and 20 characters")
          @Pattern(
              regexp = "^[a-zA-Z0-9_.]+$",
              message = "DisplayName can only contain letters, numbers, underscores, or periods")
          String displayName) {}
}
