package code.bootstrap.exceptions;

import java.util.List;

public class IllegalProfileException extends RuntimeException {
  public IllegalProfileException(List<String> active, List<String> allowed) {
    super(String.format("Active: %s. Allowed: %s", active, allowed));
  }
}