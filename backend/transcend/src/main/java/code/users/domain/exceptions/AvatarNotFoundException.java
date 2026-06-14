package code.users.domain.exceptions;

public class AvatarNotFoundException extends RuntimeException {
  public static final String MESSAGE = "Avatar not found";

  public AvatarNotFoundException() {
    super(MESSAGE);
  }
}
