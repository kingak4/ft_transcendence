package code.users.entrypoints.api.mappers;

import static code.users.entrypoints.api.LoginController.LoginRequest;
import static code.users.entrypoints.api.RegisterController.RegisterRequest;
import static code.users.entrypoints.api.UserDetailsController.UpdateUsernameRequest;
import static code.users.ports.in.RegisterUseCase.RegisterCommand;
import static code.users.ports.in.UpdateUsernameUseCase.UpdateUsernameCommand;

import code.users.domain.model.UserId;
import code.users.entrypoints.api.LoginController.LoginResponse;
import code.users.entrypoints.api.RegisterController.RegisterResponse;
import code.users.ports.in.LoginUseCase.LoginCommand;
import code.users.ports.in.LoginUseCase.LoginResult;
import code.users.ports.in.RegisterUseCase.RegisteredUser;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UsersApiMapper {

  default UUID map(UserId value) {
    if (value == null) {
      return null;
    }
    return value.getValue();
  }

  @Mapping(source = "password", target = "rawPassword")
  LoginCommand toCommand(LoginRequest request);

  LoginResponse toResponse(LoginResult result);

  @Mapping(source = "password", target = "rawPassword")
  RegisterCommand toCommand(RegisterRequest request);

  RegisterResponse toResponse(RegisteredUser result);

  UpdateUsernameCommand toCommand(UpdateUsernameRequest request);
}