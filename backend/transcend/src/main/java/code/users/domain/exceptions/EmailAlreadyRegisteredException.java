package code.users.domain.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
  public EmailAlreadyRegisteredException(String email) {
    super(String.format("Email %s already registered.", email));
  }
}