package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;

/**
 * Exception for unauthorized operations by non-admin users.
 */

@Getter
public class UnauthorizedOperationException extends RuntimeException {

  private final CustomErrorMessage errorMessage;

  /**
   * Creates a new exception with the given error message.
   *
   * @param errorMessage the error message enum
   */
  public UnauthorizedOperationException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
  }