package stud.ntnu.no.idatt2105.Findigo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
  //TODO delete
  private final HttpStatus status;

  public CustomException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

}
