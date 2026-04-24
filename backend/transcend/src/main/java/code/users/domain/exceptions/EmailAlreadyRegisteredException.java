package code.users.domain.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
  public static final String MESSAGE = "Email %s already registered.";

  public EmailAlreadyRegisteredException(String email) {
    super(String.format(MESSAGE, email));
  }
}
