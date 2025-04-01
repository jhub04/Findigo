package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.service.ImageService;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
  private final ImageService imageService;
  private static final Logger logger = LogManager.getLogger(ImageController.class);
  /**
   * Uploads an image to a listing.
   * @param listingId The ID of the listing to upload the image to.
   * @param file The image file to upload.
   * @return A response entity with no content.
   */
  @Operation(summary = "Upload an image to a listing", description = "Uploads an image to a listing.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Listing successfully created, no response data"),
      @ApiResponse(responseCode = "500", description = "Image upload failed"),
      @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @PostMapping("/upload/{listingId}")
  public ResponseEntity<?> uploadImageToListing(
      @Parameter(description = "The ID of the listing to upload the image to.") @PathVariable Long listingId,
      @RequestParam("file") MultipartFile file) {
    logger.info("Uploading file with name " + file.getOriginalFilename() + " to listing with ID " + listingId);
    imageService.uploadImageToListing(listingId, file);
    logger.info("File with name " + file.getOriginalFilename() + " successfully uploaded to listing with ID " + listingId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Downloads an image from a listing.
   * @return
   */
  @GetMapping("/download/{listingId}")
  public ResponseEntity<?> getImagesFromListing(
      @Parameter(description = "The ID of the listing to download the images from.") @PathVariable Long listingId) {
    return null;
  }
}
