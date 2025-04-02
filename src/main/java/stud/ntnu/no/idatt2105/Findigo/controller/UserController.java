package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.UserMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.List;

/**
 * Controller for handling user-related operations.
 * <p>
 * This includes retrieving all users (admin only),
 * fetching users by ID or username (admin only), and
 * retrieving the profile of the currently authenticated user.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private static final Logger logger = LogManager.getLogger(UserController.class);
  private final UserService userService;
  private final UserMapper userMapper;
  private final SecurityUtil securityUtil;


  /**
   * Retrieves the profile of the currently authenticated user.
   * <p>
   * Accessible by any authenticated user.
   * </p>
   *
   * @return A {@link ResponseEntity} containing the profile of the logged-in user.
   */
  @Operation(summary = "Get current user profile", description = "Fetches the profile of the currently authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully fetched user profile"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - User is not logged in")
  })
  @GetMapping("/profile")
  public ResponseEntity<UserResponse> getCurrentUser() {
    logger.info("Fetching current user profile");
    return ResponseEntity.ok(userService.getCurrentUser());
  }

  /**
   * Edits the profile details (username and/or password) of the given user.
   * <p>
   * This endpoint allows a logged-in user to update their own profile information.
   * Users cannot edit another user's profile.
   * </p>
   *
   * @param request the new details to be updated (username and/or password)
   * @return a {@link ResponseEntity} with a success message if the update is successful
   */
  @Operation(summary = "Edits user profile", description = "Edits username and/or password of the given user, only accessible to the logged in user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully edited user profile"),
      @ApiResponse(responseCode = "403", description = "If the logged in user tries to edit profile of another user")
  })
  @PutMapping("/me/edit")
  public ResponseEntity<String> editUserDetails(
      @RequestBody UserRequest request) {
    logger.info("Editing user with username {}", request.getUsername());
    userService.editMyUserDetails(request);
    logger.info("User details edited of user with username {}", request.getUsername());
    return ResponseEntity.ok("User updated");
  }

  @GetMapping("/me/listings")
  public ResponseEntity<List<ListingResponse>> getMyListings() {
    logger.info("Fetching listings for current user");

    return ResponseEntity.ok(userService.getMyListings());
  }
}
