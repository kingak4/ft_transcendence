package code.chat.entrypoints;

import code.chat.domain.exception.ChatNotFoundException;
import code.chat.domain.exception.EmptyMessageException;
import code.chat.domain.exception.MessageNotFoundException;
import code.chat.domain.exception.NotMessageOwnerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class ChatExceptionHandler {

  @ExceptionHandler(ChatNotFoundException.class)
  public ProblemDetail handleNotFoundException(ChatNotFoundException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(EmptyMessageException.class)
  public ProblemDetail handleEmptyMessageException(EmptyMessageException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ProblemDetail handleMessageNotFoundException(MessageNotFoundException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(NotMessageOwnerException.class)
  public ProblemDetail handleNotMessageOwnerException(NotMessageOwnerException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
  }
}