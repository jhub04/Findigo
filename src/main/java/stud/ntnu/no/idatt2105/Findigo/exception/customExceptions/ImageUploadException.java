package stud.ntnu.no.idatt2105.Findigo.exception.customExceptions;

/**
 * The class represents a custom exception to indicate that an image could not be uploaded.
 *
 * @version 1.0
 */
public class ImageUploadException extends RuntimeException {
    /**
     * Constructs an instance of the class with the specified error message.
     *
     * @param message The error message.
     */
    public ImageUploadException(String message) {
        super(message);
    }
}
