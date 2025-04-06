package stud.ntnu.no.idatt2105.Findigo.exception;

import lombok.Getter;

/**
 * Enum representing custom error messages and their associated HTTP status codes.
 * <p>
 * Used throughout the application to provide consistent error handling and responses.
 * Each constant defines both a status code and a descriptive message.
 * </p>
 */
@Getter
public enum CustomErrorMessage {

  // --- Already Exists ---
  /**
   * Error when attempting to create a category that already exists.
   */
  CATEGORY_ALREADY_EXISTS(409, "Category already exists."),

  /**
   * Error when attempting to create a username that already exists.
   */
  USERNAME_ALREADY_EXISTS(409, "Username already exists."),

  /**
   * Error when attempting to create an attribute that already exists.
   */
  ATTRIBUTE_ALREADY_EXISTS(409, "Attribute already exists."),

  // --- Not Found ---
  /**
   * Error when the requested listing is not found.
   */
  LISTING_NOT_FOUND(404, "Listing not found."),

  /**
   * Error when the requested category is not found.
   */
  CATEGORY_NOT_FOUND(404, "Category not found."),

  /**
   * Error when the requested username is not found.
   */
  USERNAME_NOT_FOUND(404, "Username not found."),

  /**
   * Error when the requested attribute is not found.
   */
  ATTRIBUTE_NOT_FOUND(404, "Attribute not found"),

  /**
   * Error when no listings are found in the specified category.
   */
  LISTING_NOT_FOUND_IN_CATEGORY(404, "No listings found in the specified category."),

  // --- Unchanged ---
  /**
   * Error when the new category name is the same as the current name.
   */
  CATEGORY_NAME_UNCHANGED(400, "The new category name must be different from the current name."),

  /**
   * Error when the new attribute name is the same as the current name.
   */
  ATTRIBUTE_NAME_UNCHANGED(400, "The new attribute name must be different from the current name."),

  // --- Image Errors ---
  /**
   * Error when an image fails to upload.
   */
  IMAGE_UPLOAD_FAILED(500, "Failed to upload the image."),

  /**
   * Error when an image fails to download.
   */
  IMAGE_DOWNLOAD_FAILED(500, "Failed to download the image."),

  // --- Generic ---
  /**
   * Error when a user attempts an unauthorized operation.
   */
  UNAUTHORIZED_OPERATION(403, "You are not authorized to perform this operation."),

  /**
   * Error when the server cant delete the specified image
   */
  IMAGE_DELETE_FAILED(500, "Failed to delete the image."),

  /**
   * Error when an internal server error occurs.
   */
  INTERNAL_SERVER_ERROR(500, "An internal server error occurred.");

  /**
   * The HTTP status code associated with the error.
   */
  private final int status;

  /**
   * The descriptive error message.
   */
  private final String message;

  CustomErrorMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
