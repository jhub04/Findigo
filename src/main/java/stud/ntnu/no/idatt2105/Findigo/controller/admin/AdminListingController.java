package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;

import java.util.List;

/**
 * Admin controller for managing listings.
 *
 * <p>Provides endpoints for administrators to:
 * <ul>
 *     <li>Retrieve all listings</li>
 *     <li>Edit existing listings</li>
 *     <li>Delete listings</li>
 * </ul>
 * Access is restricted to users with the 'ADMIN' role.
 * </p>
 */
@RestController
@RequestMapping("/api/admin/listings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Listings", description = "Operations for managing listings by admin users")
public class AdminListingController {

  private static final Logger logger = LogManager.getLogger(AdminListingController.class);

  private final ListingService listingService;

  /**
   * Edits an existing listing.
   *
   * @param listingId the ID of the listing to edit
   * @param request   the updated listing data
   * @return the updated listing details
   */
  @Operation(summary = "Edit an existing listing", description = "Updates an existing listing by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listing successfully updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "404", description = "Listing not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{listingId}")
  public ResponseEntity<ListingResponse> update(
          @Parameter(description = "ID of the listing to update", example = "1")
          @PathVariable Long listingId,
          @Validated @RequestBody ListingRequest request
  ) {
    logger.info("Admin: Updating listing with ID {}", listingId);
    ListingResponse updatedListing = listingService.editListingAsAdmin(listingId, request);
    logger.info("Admin: Listing updated with ID {}", listingId);
    return ResponseEntity.ok(updatedListing);
  }

  /**
   * Deletes an existing listing.
   *
   * @param listingId the ID of the listing to delete
   * @return a confirmation message
   */
  @Operation(summary = "Delete a listing", description = "Deletes a listing by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listing successfully deleted"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "404", description = "Listing not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{listingId}")
  public ResponseEntity<String> delete(
          @Parameter(description = "ID of the listing to delete", example = "1")
          @PathVariable Long listingId
  ) {
    logger.info("Admin: Deleting listing with ID {}", listingId);
    listingService.deleteListingAsAdmin(listingId);
    logger.info("Admin: Listing deleted with ID {}", listingId);
    return ResponseEntity.ok("Listing successfully deleted");
  }
}
