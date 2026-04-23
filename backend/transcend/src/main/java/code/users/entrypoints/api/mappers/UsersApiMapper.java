package code.users.entrypoints.api.mappers;

import static code.users.entrypoints.api.LoginController.LoginRequest;
import static code.users.entrypoints.api.RegisterController.RegisterRequest;
import static code.users.ports.in.RegisterUseCase.RegisterCommand;

import code.bootstrap.config.SpringMapperConfig;
import code.users.entrypoints.api.LoginController.LoginResponse;
import code.users.entrypoints.api.RegisterController.RegisterResponse;
import code.users.ports.in.LoginUseCase.LoginCommand;
import code.users.ports.in.LoginUseCase.LoginResult;
import code.users.ports.in.RegisterUseCase.RegisteredUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
public interface UsersApiMapper {

  @Mapping(source = "password", target = "rawPassword")
  LoginCommand toCommand(LoginRequest request);

  LoginResponse toResponse(LoginResult result);

  RegisterCommand toCommand(RegisterRequest request);

  RegisterResponse toResponse(RegisteredUser result);
}
