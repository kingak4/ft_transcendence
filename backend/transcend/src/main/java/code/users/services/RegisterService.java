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
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
class RegisterService implements RegisterUseCase {

  private final UserDao userDao;
  private final HashingService hashingService;

  @Override
  public RegisteredUser register(@NonNull RegisterCommand command) {
    String hash = hashingService.encode(command.rawPassword());
    if (userDao.findByEmail(command.email()).isPresent())
      throw new EmailAlreadyRegisteredException(command.email());
    User newUser =
        User.builder().id(UUID.randomUUID()).email(command.email()).password(hash).build();
    userDao.createUser(newUser);
    return new RegisteredUser(newUser.getId());
  }
}
