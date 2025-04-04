package stud.ntnu.no.idatt2105.Findigo.exception;

import lombok.Getter;

@Getter
public enum CustomErrorMessage {

  // --- Already Exists ---
  CATEGORY_ALREADY_EXISTS(409, "Category already exists."),
  USERNAME_ALREADY_EXISTS(409, "Username already exists."),
  ATTRIBUTE_ALREADY_EXISTS(409, "Attribute already exists."),

  // --- Not Found ---
  LISTING_NOT_FOUND(404, "Listing not found."),
  CATEGORY_NOT_FOUND(404, "Category not found."),
  USERNAME_NOT_FOUND(404, "Username not found."),
  ATTRIBUTE_NOT_FOUND(404, "Attribute not found"),
  LISTING_NOT_FOUND_IN_CATEGORY(404, "No listings found in the specified category."),

  // --- Unchanged ---
  CATEGORY_NAME_UNCHANGED(400, "The new category name must be different from the current name."),
  ATTRIBUTE_NAME_UNCHANGED(400, "The new attribute name must be different from the current name."),


  // --- Image Errors ---
  IMAGE_UPLOAD_FAILED(500, "Failed to upload the image."),
  IMAGE_DOWNLOAD_FAILED(500, "Failed to download the image."),

  // --- Generic ---
  UNAUTHORIZED_OPERATION(403, "You are not authorized to perform this operation."),
  INTERNAL_SERVER_ERROR(500, "An internal server error occurred.");

  private final int status;
  private final String message;

  CustomErrorMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
