package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

/**
 * The class represents a custom exception to indicate that a user with given username
 * is already in use by another user.
 *
 * @author Scott du Plessis
 * @version 1.0
 */
public class UsernameAlreadyExistsException extends RuntimeException{

  /**
   * Constructs an instance of the class with the specified error message.
   *
   * @param message The error message.
   */
  public UsernameAlreadyExistsException(String message) {
    super(message);
  }
}
