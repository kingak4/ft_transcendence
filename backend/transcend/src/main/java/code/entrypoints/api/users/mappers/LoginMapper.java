package code.entrypoints.api.users.mappers;

import code.bootstrap.config.SpringMapperConfig;
import code.entrypoints.api.users.LoginRestController;
import code.modules.users.ports.in.LoginUseCase.LoginCommand;
import code.modules.users.ports.in.LoginUseCase.LoginResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
public interface LoginMapper {

  @Mapping(source = "password", target = "rawPassword")
  LoginCommand toCommand(LoginRestController.LoginRequest request);

  LoginRestController.LoginResponse toResponse(LoginResult result);
}
