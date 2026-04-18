package code.modules.users.ports.in;

public interface LoginUseCase {

  LoginResult login(LoginCommand command);

  record LoginCommand(String email, String rawPassword) {}

  record LoginResult(String accessToken, String tokenType) {}
}
