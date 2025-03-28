package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

/**
 * The class represents a custom exception to indicate that a category with given category name
 * doesn't exist.
 *
 * @author Scott du Plessis
 * @version 1.0
 */
public class CategoryNotFoundException extends RuntimeException{
  /**
   * Constructs an instance of the class with the specified error message.
   *
   * @param message The error message.
   */
  public CategoryNotFoundException(String message) {
    super(message);
  }
}
