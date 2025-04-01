package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

/**
 * The class represents a custom exception to indicate that a category with given category name
 */
public class CategoryAlreadyExistsException extends RuntimeException {
  /**
   * Constructs an instance of the class with the specified error message.
   *
   * @param message The error message.
   */
  public CategoryAlreadyExistsException(String message) {
    super(message);
  }
}
