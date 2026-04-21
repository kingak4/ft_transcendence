package code.modules.users.services;

import code.modules.users.domain.User;
import code.modules.users.ports.in.RegisterUseCase;
import code.modules.users.ports.out.HashingService;
import code.modules.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class RegisterService implements RegisterUseCase {

  private final UserDao userDao;
  private final HashingService hashingService;

  @Override
  public RegisteredUser register(RegisterCommand command) {
    String hash = hashingService.encode(command.rawPassword());
    // TODO check if user exists?
    User newUser = new User(UUID.randomUUID(), command.email(), hash);
    userDao.createUser(newUser);
    return new RegisteredUser(newUser.id(), newUser.email());
  }
}