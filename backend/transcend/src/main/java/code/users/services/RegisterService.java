package code.users.services;

import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.domain.model.User;
import code.users.ports.in.RegisterUseCase;
import code.users.ports.out.HashingService;
import code.users.ports.out.UserDao;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterService implements RegisterUseCase {

  private final UserDao userDao;
  private final HashingService hashingService;

  @Override
  public RegisteredUser register(@NonNull RegisterCommand command) {
    String hash = hashingService.encode(command.rawPassword());
    if (userDao.findByEmail(command.email()).isPresent())
      throw new EmailAlreadyRegisteredException(command.email());
    User newUser = new User(UUID.randomUUID(), command.email(), hash);
    userDao.createUser(newUser);
    return new RegisteredUser(newUser.id());
  }
}
