package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;
import stud.ntnu.no.idatt2105.Findigo.service.RecommendationService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for handling listing-related operations.
 *
 * <p>Provides endpoints to create, retrieve, update, and delete listings, as well as retrieve recommended listings.</p>
 */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Endpoints for creating, retrieving, updating, and deleting listings")
public class ListingController {

  private static final int pageSize = 20;
  private static final Logger logger = LogManager.getLogger(ListingController.class);

  private final ListingService listingService;
  private final RecommendationService recommendationService;

  /**
   * Adds a new listing.
   *
   * @param request the details of the listing to be added
   * @return the created listing
   */
  @Operation(summary = "Add a new listing", description = "Creates a new listing for a specific user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Listing successfully created"),
          @ApiResponse(responseCode = "404", description = "User or category not found")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ListingResponse> addListing(@Valid @RequestBody ListingRequest request) {
    logger.info("Adding listing with description '{}'", request.getBriefDescription());
    ListingResponse listing = listingService.addListing(request);
    logger.info("Listing with description '{}' added", request.getBriefDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(listing);
  }

  /**
   * Retrieves all listings.
   *
   * @return list of all listings
   */
  @Operation(summary = "Get all listings", description = "Fetches all listings stored in the database")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listings fetched successfully"),
          @ApiResponse(responseCode = "404", description = "No listings found")
  })
  @GetMapping
  public ResponseEntity<List<ListingResponse>> getAllListings() {
    logger.info("Fetching all listings from database");
    List<ListingResponse> listings = listingService.getAllListings();
    logger.info("Fetched {} listings from database", listings.size());
    return ResponseEntity.ok(listings);
  }

  /**
   * Retrieves listings by category ID.
   *
   * @param categoryId the category ID
   * @return list of listings in the category
   */
  @Operation(summary = "Get listings by category", description = "Fetches all listings in a specific category")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listings fetched successfully"),
          @ApiResponse(responseCode = "404", description = "Category not found")
  })
  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<ListingResponse>> getListingsByCategory(
          @Parameter(description = "The ID of the category", example = "1") @PathVariable Long categoryId) {
    logger.info("Fetching listings in category with ID {}", categoryId);
    List<ListingResponse> listings = listingService.getListingsInCategory(categoryId);
    logger.info("Fetched {} listings in category with ID {}", listings.size(), categoryId);
    return ResponseEntity.ok(listings);
  }

  /**
   * Edits an existing listing.
   *
   * @param listingId the listing ID
   * @param request   the updated listing details
   * @return the updated listing
   */
  @Operation(summary = "Edit a listing", description = "Edits the details of an existing listing")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listing edited successfully"),
          @ApiResponse(responseCode = "404", description = "Listing, category, or attributes not found")
  })
  @PutMapping("/edit/{listingId}")
  public ResponseEntity<ListingResponse> editListing(
          @Parameter(description = "The ID of the listing to edit", example = "1") @PathVariable Long listingId,
          @RequestBody ListingRequest request) {
    logger.info("Editing listing with ID {}", listingId);
    ListingResponse listingResponse = listingService.editListing(listingId, request);
    logger.info("Listing with ID {} successfully edited", listingId);
    return ResponseEntity.ok(listingResponse);
  }

  /**
   * Deletes a listing by ID.
   *
   * @param listingID the listing ID
   * @return confirmation message
   */
  @Operation(summary = "Delete a listing", description = "Deletes a listing by its ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listing deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @DeleteMapping("/{listingID}")
  public ResponseEntity<String> deleteListing(
          @Parameter(description = "The ID of the listing to delete", example = "1") @PathVariable long listingID) {
    logger.info("Deleting listing with ID {}", listingID);
    listingService.deleteListing(listingID);
    logger.info("Listing with ID {} deleted", listingID);
    return ResponseEntity.ok("Listing deleted");
  }

  /**
   * Retrieves a listing by ID.
   *
   * @param listingId the listing ID
   * @return the requested listing
   */
  @Operation(summary = "Get listing by ID", description = "Fetches a listing by its unique ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listing fetched successfully"),
          @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @GetMapping("/{listingId}")
  public ResponseEntity<ListingResponse> getListingById(
          @Parameter(description = "The ID of the listing to fetch", example = "1") @PathVariable Long listingId) {
    logger.info("Fetching listing with ID {}", listingId);
    ListingResponse listingResponse = listingService.getListingById(listingId);
    logger.info("Listing with ID {} fetched", listingId);
    return ResponseEntity.ok(listingResponse);
  }

  /**
   * Retrieves recommended listings, paginated.
   *
   * @param pageNumber the page number
   * @return paginated recommended listings
   */
  @Operation(summary = "Get recommended listings", description = "Fetches a paginated list of recommended listings")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Recommended listings fetched successfully"),
          @ApiResponse(responseCode = "404", description = "No recommended listings found")
  })
  @GetMapping("/recommended/{pageNumber}")
  public ResponseEntity<Page<Listing>> getRecommendedListings(
          @Parameter(description = "The page number to retrieve", example = "0") @PathVariable int pageNumber) {
    logger.info("Fetching recommended listings, page {}", pageNumber);
    Page<Listing> recommendedListingsPage = recommendationService.getRecommendedListings(pageNumber, pageSize);
    logger.info("Recommended listings page {} fetched", pageNumber);
    return ResponseEntity.ok(recommendedListingsPage);
  }
}
