package stud.ntnu.no.idatt2105.Findigo.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * A class representing the details of an error response returned from the backend.
 * <p>
 * This class contains relevant information about the error, including the HTTP status,
 * exception type, error message, timestamp, and the request path.
 * </p>
 */
@Data
public class ErrorDetail {

  /**
   * The timestamp when the error occurred.
   */
  private LocalDateTime timestamp;

  /**
   * The HTTP status code associated with the error.
   */
  private int status;

  /**
   * The type of exception that was thrown.
   */
  private String exceptionType;

  /**
   * The HTTP status message associated with the error.
   */
  private String httpStatusMessage;

  /**
   * A detailed error message describing the issue.
   */
  private String message;

  /**
   * The path of the request that triggered the error.
   */
  private String path;

  /**
   * Constructs an {@link ErrorDetail} with the specified details.
   *
   * @param timestamp the time when the error occurred
   * @param status the HTTP status code
   * @param httpStatusMessage the HTTP status message
   * @param exceptionType the type of exception thrown
   * @param message the error message
   * @param path the path of the request that caused the error
   */
  public ErrorDetail(LocalDateTime timestamp, int status, String httpStatusMessage, String exceptionType, String message, String path) {
    this.timestamp = timestamp;
    this.status = status;
    this.httpStatusMessage = httpStatusMessage;
    this.exceptionType = exceptionType;
    this.message = message;
    this.path = path;
  }
}
