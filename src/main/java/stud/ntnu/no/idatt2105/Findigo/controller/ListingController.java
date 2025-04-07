package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.FilterListingsRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.sale.SaleResponse;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;
import stud.ntnu.no.idatt2105.Findigo.service.RecommendationService;

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
  private final int pageSize = 15;
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

    //Do not paginate
    logger.info("Fetching all listings in database");
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
    //TODO paginate... kalles denne på i frontend når man henter lisitngs i kategori på main page?
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
  public ResponseEntity<ListingResponse> update(
          @Parameter(description = "The ID of the listing to edit", example = "1") @PathVariable Long listingId,
          @RequestBody ListingRequest request) {
    logger.info("Editing listing with ID {}", listingId);
    logger.info("Request attributes: {}", request.getAttributes());
    ListingResponse listingResponse = listingService.editMyListing(listingId, request);
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
  public ResponseEntity<Page<ListingResponse>> getRecommendedListings(
      @Parameter(description = "the page number to retrieve") @PathVariable int pageNumber) {
    logger.info("Getting recommended listings, page " + pageNumber);
    Page<ListingResponse> recommendedListingsPage = recommendationService.getRecommendedListings(pageNumber - 1, pageSize);
    logger.info("Recommended listings fetched: ");
    for (ListingResponse listing : recommendedListingsPage.getContent()) {
      logger.info("Listing ID: " + listing.getId() + ", Description: " + listing.getBriefDescription());
    }
    return ResponseEntity.ok(recommendedListingsPage);
  }


  /**
   * Retrieves all listings filtered by the provided criteria.
   *
   * @param filterListingsRequest the filtering criteria
   * @return a ResponseEntity containing the filtered listings
   */
  @Operation(summary = "Get filtered listings", description = "Fetches all listings filtered by the provided criteria")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Filtered listings fetched successfully"),
      @ApiResponse(responseCode = "404", description = "Current user not found")
  })
  @PostMapping("/all")
  public ResponseEntity<List<ListingResponse>> getAllListingsFiltered(
      @RequestBody FilterListingsRequest filterListingsRequest) {
    logger.info("Fetching filtered listings in database");
    List<ListingResponse> filteredListings = listingService.getAllFilteredListings(filterListingsRequest);
    logger.info("Fetched all filtered listings in database");
    return ResponseEntity.ok(filteredListings);
  }

  /**
   * Retrieves a paginated list of filtered listings.
   *
   * @param pageNumber the page number to retrieve
   * @return a ResponseEntity containing the paginated list of filtered listings
   */
  @Operation(summary = "Get filtered listings", description = "Fetches a paginated list of filtered listings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Filtered listings fetched successfully"),
      @ApiResponse(responseCode = "404", description = "Current user not found"),
      @ApiResponse(responseCode = "400", description = "Invalid page number")
  })
  @PostMapping("/all/{pageNumber}")
  public ResponseEntity<Page<ListingResponse>> getListingsFiltered(
      @Parameter(description = "The page number to retrieve") @PathVariable int pageNumber,
      @RequestBody FilterListingsRequest filterListingsRequest) {
    logger.info("Getting filtered listings, page " + pageNumber);
    Page<ListingResponse> filteredListingsPage = listingService.getFilteredListings(pageNumber - 1, pageSize, filterListingsRequest);
    logger.info("Filtered listings fetched: ");
    for (ListingResponse listing : filteredListingsPage.getContent()) {
      logger.info("Listing ID: " + listing.getId() + ", Description: " + listing.getBriefDescription());
    }
    return ResponseEntity.ok(filteredListingsPage);
  }

  /**
   * Marks a listing as sold.
   *
   * @param listingId the ID of the listing to mark as sold
   * @return a ResponseEntity indicating the result of the operation
   */
  @Operation(summary = "Mark listing as sold", description = "Marks a listing as sold")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listing sold and a SaleResponse returned"),
      @ApiResponse(responseCode = "404", description = "Listing not found"),
  })
  @PutMapping("/sell/{listingId}")
  public ResponseEntity<?> markListingAsSold(@PathVariable long listingId) {
    logger.info("Marking listing with ID {} as sold", listingId);
    SaleResponse saleResponse = listingService.markListingAsSold(listingId);
    logger.info("Listing with ID {} marked as sold", listingId);
    return ResponseEntity.ok(saleResponse);
  }

  /**
   * Marks a listing as archived.
   *
   * @param listingId the ID of the listing to mark as archived
   * @return a ResponseEntity indicating the result of the operation
   */
  @Operation(summary = "Mark listing as archived", description = "Marks a listing as archived")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Listing archived successfully"),
      @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @PutMapping("/archive/{listingId}")
  public ResponseEntity<?> markListingAsArchived(@PathVariable long listingId) {
    logger.info("Marking listing with ID {} as archived", listingId);
    listingService.markListingAsArchived(listingId);
    logger.info("Listing with ID {} marked as archived", listingId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Marks a listing as active.
   *
   * @param listingId the ID of the listing to mark as active
   * @return a ResponseEntity indicating the result of the operation
   */
  @Operation(summary = "Mark listing as active", description = "Marks a listing as active")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Listing activated successfully"),
      @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @PutMapping("/activate/{listingId}")
  public ResponseEntity<?> markListingAsActive(@PathVariable long listingId) {
    logger.info("Marking listing with ID {} as active", listingId);
    listingService.markListingAsActive(listingId);
    logger.info("Listing with ID {} marked as active", listingId);
    return ResponseEntity.noContent().build();
  }






}
