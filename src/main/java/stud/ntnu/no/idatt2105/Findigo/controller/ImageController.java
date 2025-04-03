package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.idatt2105.Findigo.service.ImageService;

import java.util.List;

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
   * @return A response entity with the amount of images in the listing.
   */
  @Operation(summary = "Upload an image to a listing", description = "Uploads an image to a listing.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listing successfully created, returning amount of images in the listing"),
      @ApiResponse(responseCode = "500", description = "Image upload failed"),
      @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @PostMapping("/upload/{listingId}")
  public ResponseEntity<?> uploadImageToListing(
      @Parameter(description = "The ID of the listing to upload the image to.") @PathVariable Long listingId,
      @RequestParam("file") MultipartFile file) {
    logger.info("Uploading file with name " + file.getOriginalFilename() + " to listing with ID " + listingId);
    int numberOfImages = imageService.uploadImageToListing(listingId, file);
    logger.info("File with name " + file.getOriginalFilename() + " successfully uploaded to listing with ID " + listingId);
    return ResponseEntity.ok(numberOfImages);
  }

  /**
   * Downloads an image from a listing. The images are returned as resources.
   * @return A response entity containing the image.
   */
  @Operation(summary = "Download an image from a listing", description = "Downloads an specific image from a listing given by its index and lsiting ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Images successfully downloaded"),
      @ApiResponse(responseCode = "500", description = "Image download failed"),
      @ApiResponse(responseCode = "404", description = "Listing not found"),
      @ApiResponse(responseCode = "400", description = "Image index out of bounds")
  })
  @GetMapping("/download/{listingId}/{imageIndex}")
  public ResponseEntity<?> getImagesFromListing(
      @Parameter(description = "The ID of the listing to download the images from.") @PathVariable Long listingId,
      @Parameter(description = "The index of the picture to be fetched") @PathVariable int imageIndex) {
    logger.info("Downloading images from listing with ID " + listingId);
    Resource image = imageService.downloadImageFromListing(listingId, imageIndex);
    logger.info("Images from listing with ID " + listingId + " successfully downloaded");
    return ResponseEntity.ok(image);
  }


}
