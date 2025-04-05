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
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;
import stud.ntnu.no.idatt2105.Findigo.service.RecommendationService;

import java.util.List;

/**
 * Controller for handling listing-related operations.
 * <p>
 * This controller provides endpoints to create and retrieve user listings.
 * </p>
 */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Get listings from database")
public class ListingController {
  private final int pageSize = 15;
  private static final Logger logger = LogManager.getLogger(ListingController.class);
  private final ListingService listingService;
  private final RecommendationService recommendationService;
  /**
   * Adds a new listing for a specified user.
   *
   * @param request  the details of the listing to be added
   * @return a ResponseEntity containing the created listing
   */
  @Operation(summary = "Add a new listing", description = "Creates a new listing for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Listing successfully created"),
      @ApiResponse(responseCode = "404", description = "User or category in the ListingRequest not found")
  })
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ListingResponse> addListing(
      @Valid @RequestBody ListingRequest request) {
    logger.info("Adding listing from user with listing description " + request.getBriefDescription());
    ListingResponse listing = listingService.addListing(request);
    logger.info("Listing with description " + request.getBriefDescription() + " added");
    return ResponseEntity.status(HttpStatus.CREATED).body(listing);
  }

  /**
   * Retrieves all listings available in the database except for the calling users listings.
   *
   * @return a ResponseEntity containing a list of all listings
   */
  @Operation(summary = "Get all listings", description = "Fetches all listings stored in the database")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listings fetched successfully"),
      @ApiResponse(responseCode = "404", description = "If no listings are found")
  })
  @GetMapping("")
  public ResponseEntity<List<ListingResponse>> getAllListings() {

    //Do not paginate
    logger.info("Fetching all listings in database");
    List<ListingResponse> listings = listingService.getAllListings();
    logger.info("Fetched all listings in database");
    return ResponseEntity.ok(listings);
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<ListingResponse>> getListingsByCategory(
          @PathVariable Long categoryId
  ) {
    //TODO get not your lisitngs
    List<ListingResponse> listings = listingService.getListingsInCategory(categoryId);
    return ResponseEntity.ok(listings);
  }

  /**
   * Edits an existing listing with the provided details.
   *
   * @return a ResponseEntity containing the updated listing response
   */
  @Operation(summary = "Edit a listing", description = "Edits the values in the database of a given listing")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listing edited successfully"),
      @ApiResponse(responseCode = "404", description = "If no listing, category or attributes are found")
  })
  @PutMapping("/edit/{listingId}")
  public ResponseEntity<ListingResponse> editListing(
          @PathVariable Long listingId,
          @RequestBody ListingRequest request
  ) {
    logger.info("Editing listing with listing id {}", listingId);
    ListingResponse listingResponse = listingService.editListing(listingId, request);
    logger.info("Listing with listing id {} successfully edited", listingId);
    return ResponseEntity.ok(listingResponse);
  }

  /**
   * Deletes a listing with the given ID.
   *
   * @param listingID the ID of the listing to be deleted
   * @return a ResponseEntity indicating the result of the deletion
   */
  @Operation(summary = "Delete a listing", description = "Deletes the listing with the given ID from the database")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listing deleted successfully"),
      @ApiResponse(responseCode = "404", description = "If no listing with the given ID is found")
  })
  @DeleteMapping("/{listingID}")
  public ResponseEntity<String> deleteListing(
      @Parameter(description = "ID of the listing to be deleted") @PathVariable long listingID) {
    logger.info("Deleting listing with id {}", listingID);
    listingService.deleteListing(listingID);
    return ResponseEntity.ok("Listing deleted");
  }

  /**
   * Retrieves a specific listing by its ID.
   *
   * @param listingId the ID of the listing to retrieve
   * @return a ResponseEntity containing the listing if found
   */
  @Operation(summary = "Get listing by ID", description = "Fetches a single listing by its unique ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Listing fetched successfully"),
          @ApiResponse(responseCode = "404", description = "Listing not found")
  })
  @GetMapping("/{listingId}")
  public ResponseEntity<ListingResponse> getListingById(@PathVariable Long listingId) {
    //kalles når bruker går inn på listing, legg til i browse hsitory
    logger.info("Fetching listing in database");
    ListingResponse listingResponse = listingService.getListingById(listingId);
    logger.info("Fetched all listings in database");
    return ResponseEntity.ok(listingResponse);
  }

  /**
   * Retrieves a paginated list of recommended listings.
   *
   * @param pageNumber the page number to retrieve
   * @return a ResponseEntity containing the paginated list of recommended listings
   */
  @Operation(summary = "Get recommended listings", description = "Fetches a paginated list of recommended listings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Recommended listings fetched successfully"),
      @ApiResponse(responseCode = "404", description = "If no recommended listings are found")
  })//TODO skriv riktig response for ikke finne bruker
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
      @ApiResponse(responseCode = "404", description = "If no filtered listings are found")
  })//TODO skriv riktig response for feil
  @GetMapping("/all")
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
      @ApiResponse(responseCode = "404", description = "If no filtered listings are found")
  })//TODO skriv riktig response for feil
  @GetMapping("/all/{pageNumber}")
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




}
