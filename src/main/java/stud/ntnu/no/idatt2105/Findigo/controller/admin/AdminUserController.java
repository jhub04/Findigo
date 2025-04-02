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
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.controller.ListingController;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
@Tag(name = "Listings: admin", description = "Get listings from database with admin permission")
public class AdminUserController {

  private static final Logger logger = LogManager.getLogger(ListingController.class);
  private final UserService userService;

  @GetMapping("/{userId}/listings")
  public ResponseEntity<List<ListingResponse>> getUserListings(
          @Parameter(description = "The id of the user listings") @PathVariable long userId
  ) {
    logger.info("Admin: fetching listings for user with id {}", userId);

    return ResponseEntity.ok(userService.getUserListings(userId));
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<UserLiteResponse> createUser(
          @RequestBody UserRequest request
  ) {
    UserLiteResponse createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @PutMapping("/edit/{userId}")
  public ResponseEntity<String> editUser(
          @Parameter(description = "The id of the user listings") @PathVariable long userId,
          @RequestBody UserRequest request
          ) {
    userService.editUserDetails(request, userId);
    return ResponseEntity.ok("User successfully updated");
  }

  /**
   * Retrieves a list of all users.
   * <p>
   * Accessible only by administrators.
   * </p>
   *
   * @return A {@link ResponseEntity} containing a list of users.
   */
  @Operation(summary = "Get all users", description = "Fetches all users in the system. Accessible only by admins.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched all users"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied")
  })
  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    logger.info("Fetching all users");
    List<UserResponse> users = userService.getAllUsers();
    logger.info("Fetched {} users", users.size());
    return ResponseEntity.ok(users);
  }

  /**
   * Retrieves a user by their unique ID.
   * <p>
   * Accessible only by administrators.
   * </p>
   *
   * @param id The unique ID of the user.
   * @return A {@link ResponseEntity} containing the user, if found.
   */
  @Operation(summary = "Get user by ID", description = "Fetches a user by their unique ID. Accessible only by admins.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User found"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied")
  })
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(
          @Parameter(description = "The unique ID of the user to be fetched") @PathVariable Long id) {
    logger.info("Fetching user by ID: {}", id);
    UserResponse user = userService.getUserDtoById(id);
    logger.info("User found with ID " + id);
    return ResponseEntity.ok(user);
  }

}
