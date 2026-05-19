package code.users.entrypoints.http.mappers;

import static code.users.entrypoints.http.LoginController.LoginRequest;
import static code.users.entrypoints.http.RegisterController.RegisterRequest;
import static code.users.ports.in.RegisterUseCase.RegisterCommand;
import static code.users.ports.in.UpdateDisplayNameUseCase.UpdateDisplayNameCommand;

import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.entrypoints.http.LoginController.LoginResponse;
import code.users.entrypoints.http.RegisterController.RegisterResponse;
import code.users.entrypoints.http.UserDetailsController;
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
    return value.getVal();
  }

  @Mapping(source = "password", target = "rawPassword")
  LoginCommand toCommand(LoginRequest request);

  LoginResponse toResponse(LoginResult result);

  @Mapping(source = "password", target = "rawPassword")
  RegisterCommand toCommand(RegisterRequest request);

  RegisterResponse toResponse(RegisteredUser result);

  UpdateDisplayNameCommand toCommand(UserDetailsController.UpdateDisplayNameRequest request);

  @Mapping(source = "online", target = "isOnline")
  UserDetailsController.GetUserDetailsResponse toResponse(UserDetails details);
}
