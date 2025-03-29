package stud.ntnu.no.idatt2105.Findigo.exception;

import io.jsonwebtoken.security.InvalidKeyException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.UsernameAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Global exception handler for handling different types of exceptions
 * throughout the application.
 * <p>
 * This class is annotated with {@link RestControllerAdvice}, which allows it
 * to handle exceptions thrown by any controller in the application.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles the {@link UsernameAlreadyExistsException} and returns a custom error response.
   *
   * @param e the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with status 409 (Conflict)
   */
  @ExceptionHandler(UsernameAlreadyExistsException.class)
  public ResponseEntity<ErrorDetail> handleUsernameAlreadyExistsException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.CONFLICT, e, request);
  }

  /**
   * Handles the {@link AuthenticationException} and returns a custom error response.
   *
   * @param e the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with status 400 (Bad Request)
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorDetail> handleAuthenticationException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e, request);
  }

  /**
   * Handles the {@link UsernameNotFoundException} and returns a custom error response.
   *
   * @param e the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with status 404 (Not Found)
   */
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorDetail> handleUsernameNotFoundException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.NOT_FOUND, e, request);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorDetail> handleNoSuchElementException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.NOT_FOUND, e, request);
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ErrorDetail> handleCategoryNotFoundException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.NOT_FOUND, e, request);
  }


  /**
   * Handles the {@link InvalidKeyException} and returns a custom error response.
   *
   * @param e the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with status 500 (Internal Server Error)
   */
  @ExceptionHandler(InvalidKeyException.class)
  public ResponseEntity<ErrorDetail> handleInvalidKeyException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
  }

  /**
   * Creates an error response entity based on the exception details.
   *
   * @param status the HTTP status to be returned
   * @param e the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with the provided status
   */
  public ResponseEntity<ErrorDetail> createErrorResponseEntity(HttpStatus status, Exception e, WebRequest request) {
    ErrorDetail error = new ErrorDetail(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        e.toString(),
        e.getMessage(),
        request.getDescription(false)
    );
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handles the {@link IllegalArgumentException} and returns a custom error response.
   *
   * @param ex the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with status 400 (Bad Request)
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDetail> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
  }

  /**
   * Handles any generic exception and returns a custom error response.
   *
   * @param ex the exception that was thrown
   * @param request the web request that triggered the exception
   * @return a {@link ResponseEntity} containing an {@link ErrorDetail} with status 500 (Internal Server Error)
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetail> handleGenericException(Exception ex, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
  }
}
