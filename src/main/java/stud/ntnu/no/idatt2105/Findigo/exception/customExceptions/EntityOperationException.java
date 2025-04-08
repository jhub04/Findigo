package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;

/**
 * Exception thrown when a generic operation on an entity fails.
 * <p>
 * Used for unexpected errors during create, update, or delete operations
 * that do not fall under more specific exception categories.
 * Contains a {@link CustomErrorMessage} providing detailed error information.
 * </p>
 */
@Getter
public class EntityOperationException extends RuntimeException {

  /**
   * Custom error message containing details about the exception.
   */
  private final CustomErrorMessage errorMessage;

  /**
   * Constructs a new {@code EntityOperationException} with the specified error message.
   *
   * @param errorMessage the detailed custom error message
   */
  public EntityOperationException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
}
