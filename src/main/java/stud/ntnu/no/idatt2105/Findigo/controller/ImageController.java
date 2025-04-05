package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.service.ImageService;

/**
 * Controller for handling image-related operations.
 *
 * <p>Provides endpoints for uploading and downloading images associated with listings.</p>
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Images", description = "Endpoints for uploading and downloading images for listings")
public class ImageController {

  private static final Logger logger = LogManager.getLogger(ImageController.class);

  private final ImageService imageService;

  /**
   * Uploads an image to a listing.
   *
   * @param listingId The ID of the listing to upload the image to.
   * @param file      The image file to upload.
   * @return A response entity with the number of images in the listing.
   */
  @Operation(summary = "Upload an image to a listing", description = "Uploads an image to a listing.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Image uploaded successfully, returning the number of images in the listing"),
          @ApiResponse(responseCode = "404", description = "Listing not found"),
          @ApiResponse(responseCode = "500", description = "Image upload failed")
  })
  @PostMapping("/upload/{listingId}")
  public ResponseEntity<?> uploadImageToListing(
          @Parameter(description = "The ID of the listing to upload the image to.", example = "1") @PathVariable Long listingId,
          @RequestParam("file") MultipartFile file) {
    logger.info("Uploading file '{}' to listing with ID {}", file.getOriginalFilename(), listingId);
    int numberOfImages = imageService.uploadImageToListing(listingId, file);
    logger.info("File '{}' successfully uploaded to listing with ID {}", file.getOriginalFilename(), listingId);
    return ResponseEntity.ok(numberOfImages);
  }

  /**
   * Downloads an image from a listing.
   *
   * @param listingId  The ID of the listing.
   * @param imageIndex The index of the image to download.
   * @return A response entity containing the image.
   */
  @Operation(summary = "Download an image from a listing", description = "Downloads a specific image from a listing given its index and listing ID.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Image successfully downloaded"),
          @ApiResponse(responseCode = "404", description = "Listing not found"),
          @ApiResponse(responseCode = "400", description = "Image index out of bounds"),
          @ApiResponse(responseCode = "500", description = "Image download failed")
  })
  @GetMapping("/download/{listingId}/{imageIndex}")
  public ResponseEntity<?> getImagesFromListing(
          @Parameter(description = "The ID of the listing to download the image from.", example = "1") @PathVariable Long listingId,
          @Parameter(description = "The index of the image to download", example = "0") @PathVariable int imageIndex) {
    logger.info("Downloading image at index {} from listing with ID {}", imageIndex, listingId);
    Resource image = imageService.downloadImageFromListing(listingId, imageIndex);
    logger.info("Image at index {} from listing with ID {} successfully downloaded", imageIndex, listingId);
    return ResponseEntity.ok(image);
  }
}
