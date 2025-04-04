package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;

/**
 * Exception thrown when an edit operation does not result in any changes.
 */
@Getter
public class EditedValueUnchangedException extends RuntimeException {
  private final CustomErrorMessage errorMessage;
  /**
   * Constructs an instance of the class with the specified error message.
   *
   * @param errorMessage The error message.
   */
  public EditedValueUnchangedException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
}
