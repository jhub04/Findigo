package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.MyUserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;

import java.util.List;

/**
 * Controller for handling user-related operations.
 *
 * <p>Provides endpoints for profile management, managing favorites, and retrieving user-specific data.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing user profiles, favorites, and personal listings")
public class UserController {

  private static final Logger logger = LogManager.getLogger(UserController.class);

  private final UserService userService;
  private final SecurityUtil securityUtil;

  /**
   * Retrieves the profile of the currently authenticated user.
   *
   * @return the profile of the logged-in user
   */
  @Operation(summary = "Get current user profile", description = "Fetches the profile of the currently authenticated user.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched user profile"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - User is not logged in")
  })
  @GetMapping("/profile")
  public ResponseEntity<UserResponse> getCurrentUser() {
    logger.info("Fetching current user profile");
    UserResponse userResponse = userService.getCurrentDtoUser();
    logger.info("Fetched profile for user '{}'", userResponse.getUsername());
    return ResponseEntity.ok(userResponse);
  }

  /**
   * Edits the profile details (username and/or password) of the current user.
   *
   * @param request the new profile details
   * @return success message if updated
   */
  @Operation(summary = "Edit user profile", description = "Edits username and/or password of the logged-in user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully edited user profile"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Cannot edit another user's profile")
  })
  @PutMapping("/me/edit")
  public ResponseEntity<String> update(@RequestBody MyUserRequest request) {
    logger.info("Editing profile for user '{}'", request.getUsername());
    userService.editMyUserDetails(request);
    logger.info("Profile updated for user '{}'", request.getUsername());
    return ResponseEntity.ok("User updated");
  }

  /**
   * Retrieves all favorites for the current user.
   *
   * @return list of favorite listings
   */
  @Operation(summary = "Get favorites", description = "Fetches all favorite listings for the current user.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched all favorites"),
          @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/favorites")
  public ResponseEntity<List<ListingResponse>> getFavorites() {
    logger.info("Fetching favorites for current user");
    List<ListingResponse> favorites = userService.getFavorites();
    logger.info("Fetched {} favorites for current user", favorites.size());
    return ResponseEntity.ok(favorites);
  }

  /**
   * Adds a listing to the current user's favorites.
   *
   * @param listingId the ID of the listing
   * @return the added favorite listing
   */
  @Operation(summary = "Add listing to favorites", description = "Adds a listing to the current user's favorites.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully added listing to favorites"),
          @ApiResponse(responseCode = "404", description = "Listing or user not found")
  })
  @PostMapping("/favorites/{listingId}")
  public ResponseEntity<ListingResponse> addNewFavorite(
          @Parameter(description = "The ID of the listing to add to favorites", example = "1") @PathVariable long listingId) {
    logger.info("Adding listing with ID {} to favorites", listingId);
    ListingResponse favoritedListing = userService.addFavorite(listingId);
    logger.info("Added listing with ID {} to favorites", listingId);
    return ResponseEntity.ok(favoritedListing);
  }

  /**
   * Deletes a listing from the current user's favorites.
   *
   * @param listingId the ID of the listing
   * @return the removed favorite listing
   */
  @Operation(summary = "Delete listing from favorites", description = "Removes a listing from the current user's favorites.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully removed listing from favorites"),
          @ApiResponse(responseCode = "404", description = "Listing or user not found")
  })
  @DeleteMapping("/favorites/{listingId}")
  public ResponseEntity<ListingResponse> deleteFavorite(
          @Parameter(description = "The ID of the listing to remove from favorites", example = "1") @PathVariable long listingId) {
    logger.info("Removing listing with ID {} from favorites", listingId);
    ListingResponse deletedFavorite = userService.deleteFavorite(listingId);
    logger.info("Removed listing with ID {} from favorites", listingId);
    return ResponseEntity.ok(deletedFavorite);
  }

  /**
   * Retrieves active listings created by the current user.
   *
   * @return list of user's own active listings
   */
  @Operation(summary = "Get own active listings", description = "Fetches all active listings created by the current user.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched user's listings")
  })
  @GetMapping("/me/activeListings")
  public ResponseEntity<List<ListingResponse>> getMyActiveListings() {
    logger.info("Fetching listings for current user");
    List<ListingResponse> myListings = userService.getMyActiveListings();
    logger.info("Fetched {} listings for current user", myListings.size());
    return ResponseEntity.ok(myListings);
  }

  /**
   * Retrieves archived listings created by the current user.
   *
   * @return list of user's own archived listings
   */
  @Operation(summary = "Get own archived listings", description = "Fetches all archived listings created by the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully fetched user's listings")
  })
  @GetMapping("/me/archivedListings")
  public ResponseEntity<List<ListingResponse>> getMyArchivedListings() {
    logger.info("Fetching listings for current user");
    List<ListingResponse> myListings = userService.getMyArchivedListings();
    logger.info("Fetched {} listings for current user", myListings.size());
    return ResponseEntity.ok(myListings);
  }

  /**
   * Retrieves sold listings created by the current user.
   *
   * @return list of user's sold listings
   */
  @Operation(summary = "Get sold listings", description = "Fetches all sold listings created by the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully fetched user's listings")

  })
  @GetMapping("/me/soldListings")
  public ResponseEntity<List<ListingResponse>> getMySoldListings() {
    logger.info("Fetching listings for current user");
    List<ListingResponse> myListings = userService.getMySoldListings();
    logger.info("Fetched {} listings for current user", myListings.size());
    return ResponseEntity.ok(myListings);
  }
}
