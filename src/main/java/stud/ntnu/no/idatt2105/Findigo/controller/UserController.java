package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.List;
import java.util.Optional;

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
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    logger.info("Fetching all users");
    List<User> users = userService.getAllUsers();
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
  @GetMapping("/id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    logger.info("Fetching user by ID: {}", id);
    User user = userService.getUserById(id);
    logger.info("User found with ID " + id);
    return ResponseEntity.ok(user);
  }

  /**
   * Retrieves a user by their username.
   * <p>
   * Accessible only by administrators.
   * </p>
   *
   * @param username The username of the user.
   * @return A {@link ResponseEntity} containing the user, if found.
   */
  @Operation(summary = "Get user by username", description = "Fetches a user by their username. Accessible only by admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "403", description = "Forbidden - Access denied")
  })
  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
    logger.info("Fetching user by username: {}", username);
    User user = userService.getUserByUsername(username);

    return ResponseEntity.ok(user);
  }

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
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<UserResponse> getCurrentUser() {
    logger.info("Fetching current user profile");
    UserResponse userResponse = userService.getCurrentUser();
    logger.info("Fetched profile for user: {}", userResponse.getUsername());
    return ResponseEntity.ok(userResponse);
  }
}
