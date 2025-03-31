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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.EditListingDto;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;

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

  private static final Logger logger = LogManager.getLogger(ListingController.class);
  private final ListingService listingService;

  /**
   * Adds a new listing for a specified user.
   *
   * @param username the username of the user adding the listing
   * @param request  the details of the listing to be added
   * @return a ResponseEntity containing the created listing
   */
  @Operation(summary = "Add a new listing", description = "Creates a new listing for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Listing successfully created"),
      @ApiResponse(responseCode = "404", description = "User or category in the ListingRequest not found")
  })
  @PostMapping("/{username}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Listing> addListing(
      @Parameter(description = "the username of the user adding the listing") @PathVariable String username,
      @Valid @RequestBody ListingRequest request) {
    //TODO use iserid instead of username
    logger.info("Adding listing from user " + username + " with listing description " + request.getBriefDescription());
    Listing listing = listingService.addListing(username, request);//TODO this should be listing response object
    logger.info("Listing with description " + request.getBriefDescription() + " added");
    return ResponseEntity.status(HttpStatus.CREATED).body(listing);
  }

  /**
   * Retrieves all listings created by a specified user.
   *
   * @param username the username of the user whose listings are being retrieved
   * @return a ResponseEntity containing a list of listing responses
   */
  @Operation(summary = "Get listings by user", description = "Fetches all listings associated with a given username")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listings fetched successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/{username}")
  public ResponseEntity<List<ListingResponse>> getUserListings(
      @Parameter(description = "The username of the user whose listings are being retrieved") @PathVariable String username) {
    logger.info("Getting listings from user " + username);
    List<ListingResponse> listings = listingService.getUserListings(username);
    logger.info("Fetched listings from user " + username);
    return ResponseEntity.ok(listings);
  }

  /**
   * Retrieves all listings available in the database.
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
    logger.info("Fetching all listings in database");
    List<ListingResponse> listings = listingService.getAllListings();
    logger.info("Fetched all listings in database");
    return ResponseEntity.ok(listings);
  }

  /**
   * Edits an existing listing with the provided details.
   *
   * @param listingDto the DTO containing the updated listing information
   * @return a ResponseEntity containing the updated listing response
   */
  @Operation(summary = "Edit a listing", description = "Edits the values in the database of a given listing")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listing edited successfully"),
      @ApiResponse(responseCode = "404", description = "If no listing, category or attributes are found")
  })
  @PutMapping("/edit")
  public ResponseEntity<?> editListing(@RequestBody EditListingDto listingDto) {
    logger.info("Editing listing with listing id " + listingDto.getId());
    ListingResponse listingResponse = listingService.editListing(listingDto);
    logger.info("Listing with listing id " + listingDto.getId() + " successfully edited");
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
  public ResponseEntity<?> deleteListing(
      @Parameter(description = "ID of the listing to be deleted") @PathVariable long listingID) {
    logger.info("Deleting listing with id " + listingID);
    listingService.deleteListing(listingID);
    return ResponseEntity.ok("Listing deleted");
  }
}
