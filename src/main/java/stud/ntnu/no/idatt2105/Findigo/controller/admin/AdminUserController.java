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
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.List;

/**
 * Admin controller for managing users.
 *
 * <p>Provides endpoints for administrators to:
 * <ul>
 *     <li>Create new users</li>
 *     <li>Retrieve all users</li>
 *     <li>Retrieve individual users</li>
 *     <li>Update user details</li>
 *     <li>Retrieve user listings</li>
 * </ul>
 * Access is restricted to users with the 'ADMIN' role.
 * </p>
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Users", description = "Operations for managing users by admin users")
public class AdminUserController {

  private static final Logger logger = LogManager.getLogger(AdminUserController.class);

  private final UserService userService;

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  @Operation(summary = "Get all users", description = "Fetches all users in the system. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched all users"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  public ResponseEntity<List<UserResponse>> findAll() {
    logger.info("Admin: Fetching all users");
    List<UserResponse> users = userService.getAllUsers();
    logger.info("Admin: Retrieved {} users", users.size());
    return ResponseEntity.ok(users);
  }

  /**
   * Retrieves a user by their unique ID.
   *
   * @param id the ID of the user
   * @return the user details
   */
  @Operation(summary = "Get user by ID", description = "Fetches a user by their unique ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User found"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findById(
          @Parameter(description = "The unique ID of the user to be fetched", example = "1")
          @PathVariable Long id
  ) {
    logger.info("Admin: Fetching user by ID {}", id);
    UserResponse user = userService.getUserDtoById(id);
    logger.info("Admin: Retrieved user with ID {}", id);
    return ResponseEntity.ok(user);
  }

  /**
   * Creates a new user.
   *
   * @param request the user data
   * @return the created user
   */
  @Operation(summary = "Create a new user", description = "Creates a new user with the provided data. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "User successfully created"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<UserLiteResponse> create(
          @Validated @RequestBody UserRequest request
  ) {
    logger.info("Admin: Creating new user with username '{}'", request.getUsername());
    UserLiteResponse createdUser = userService.createUser(request);
    logger.info("Admin: User created with ID {}", createdUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  /**
   * Updates an existing user.
   *
   * @param userId  the ID of the user to update
   * @param request the updated user data
   * @return confirmation message
   */
  @Operation(summary = "Update an existing user", description = "Updates an existing user by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User successfully updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{userId}")
  public ResponseEntity<String> update(
          @Parameter(description = "The ID of the user to update", example = "1")
          @PathVariable long userId,
          @Validated @RequestBody UserRequest request
  ) {
    logger.info("Admin: Updating user with ID {}", userId);
    userService.editUserDetails(request, userId);
    logger.info("Admin: User updated with ID {}", userId);
    return ResponseEntity.ok("User successfully updated");
  }

  /**
   * Retrieves listings associated with a specific user.
   *
   * @param userId the ID of the user
   * @return list of user's listings
   */
  @Operation(summary = "Get user's listings", description = "Fetches all listings associated with a specific user. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User's listings retrieved successfully"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{userId}/listings")
  public ResponseEntity<List<ListingResponse>> getUserListings(
          @Parameter(description = "The ID of the user to retrieve listings for", example = "1")
          @PathVariable long userId
  ) {
    logger.info("Admin: Fetching listings for user with ID {}", userId);
    List<ListingResponse> listings = userService.getUserListings(userId);
    logger.info("Admin: Retrieved {} listings for user with ID {}", listings.size(), userId);
    return ResponseEntity.ok(listings);
  }
}
