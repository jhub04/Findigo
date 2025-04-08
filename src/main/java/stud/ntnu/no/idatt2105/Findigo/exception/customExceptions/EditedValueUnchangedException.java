package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;

/**
 * Exception thrown when an edit operation does not result in any changes.
 * <p>
 * Used to indicate that an attempted update operation did not alter the existing value,
 * meaning the new value is identical to the current value.
 * Contains a {@link CustomErrorMessage} providing detailed error information.
 * </p>
 */
@Getter
public class EditedValueUnchangedException extends RuntimeException {

  /**
   * Custom error message containing details about the exception.
   */
  private final CustomErrorMessage errorMessage;

  /**
   * Constructs a new {@code EditedValueUnchangedException} with the specified error message.
   *
   * @param errorMessage the detailed custom error message
   */
  public EditedValueUnchangedException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
}
