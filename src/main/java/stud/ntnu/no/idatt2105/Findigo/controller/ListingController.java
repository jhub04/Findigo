package stud.ntnu.no.idatt2105.Findigo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
public class ListingController {
  //TODO swagger doc

  private static final Logger logger = LogManager.getLogger(ListingController.class);
  private final ListingService listingService;

  /**
   * Adds a new listing for a specified user.
   *
   * @param username the username of the user adding the listing
   * @param request  the details of the listing to be added
   * @return a ResponseEntity containing the created listing
   */
  @PostMapping("/{username}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Listing> addListing(
      @PathVariable String username,
      @Valid @RequestBody ListingRequest request) {
    logger.info("Adding listing from user " + username + " with listing description " + request.getBriefDescription());
    Listing listing = listingService.addListing(username, request);
    logger.info("Listing with description " + request.getBriefDescription() + " added");
    return ResponseEntity.status(HttpStatus.CREATED).body(listing);
  }

  /**
   * Retrieves all listings created by a specified user.
   *
   * @param username the username of the user whose listings are being retrieved
   * @return a ResponseEntity containing a list of listing responses
   */
  @GetMapping("/{username}")
  public ResponseEntity<List<ListingResponse>> getUserListings(
      @PathVariable String username) {
    logger.info("Getting listings from user " + username);
    List<ListingResponse> listings = listingService.getUserListings(username);
    logger.info("Fetched listings from user " + username);
    return ResponseEntity.ok(listings);
  }

  @GetMapping("")
  public ResponseEntity<List<ListingResponse>> getAllListings() {
    logger.info("Fetching all listings in database");
    List<ListingResponse> listings = listingService.getAllListings();
    logger.info("Fetched all listings in database");
    return ResponseEntity.ok(listings);
  }


}
