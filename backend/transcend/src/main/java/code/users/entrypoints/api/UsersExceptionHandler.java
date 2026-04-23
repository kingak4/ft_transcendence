package code.users.entrypoints.api;

import code.shared.exceptions.GlobalExceptionHandler;
import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.domain.exceptions.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UsersExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex) {
    return GlobalExceptionHandler.buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ResponseEntity<Object> handleEmailAlreadyRegisteredException(
      EmailAlreadyRegisteredException ex) {
    return GlobalExceptionHandler.buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }
}
