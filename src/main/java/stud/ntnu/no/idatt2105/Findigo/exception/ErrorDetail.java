package stud.ntnu.no.idatt2105.Findigo.exception;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents the details of an error response returned from the backend.
 * <p>
 * Contains information such as timestamp, HTTP status code and message, exception type,
 * detailed error message, and the request path.
 * Used to provide clients with clear error information.
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
   * Constructs an instance of {@link ErrorDetail} with the specified details.
   *
   * @param timestamp         the time when the error occurred
   * @param status            the HTTP status code
   * @param httpStatusMessage the HTTP status message
   * @param exceptionType     the type of exception thrown
   * @param message           the error message
   * @param path              the path of the request that caused the error
   */
  public ErrorDetail(
          LocalDateTime timestamp,
          int status,
          String httpStatusMessage,
          String exceptionType,
          String message,
          String path
  ) {
    this.timestamp = timestamp;
    this.status = status;
    this.httpStatusMessage = httpStatusMessage;
    this.exceptionType = exceptionType;
    this.message = message;
    this.path = path;
  }
}
