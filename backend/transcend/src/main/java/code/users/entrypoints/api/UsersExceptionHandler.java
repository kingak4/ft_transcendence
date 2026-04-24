package code.users.entrypoints.api;

import code.shared.exceptions.GlobalExceptionHandler;
import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.domain.exceptions.InvalidCredentialsException;
import code.users.domain.exceptions.UserNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
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

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Object> handleUserNotFoundException(
      UserNotFoundException ex) {
    return GlobalExceptionHandler.buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }
}