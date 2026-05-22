package code.shared.exceptions;

public class NotImplementedException extends RuntimeException {
  public static final String MESSAGE = "Functionality not yet implemented";

  public NotImplementedException() {
    super(MESSAGE);
  }
}
