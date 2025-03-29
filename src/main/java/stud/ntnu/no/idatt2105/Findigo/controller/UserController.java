package stud.ntnu.no.idatt2105.Findigo.controller;

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
  @GetMapping("/id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
    //TODO add better exception hamdling here
    logger.info("Fetching user by ID: {}", id);
    Optional<User> user = userService.getUserById(id);
    if (user.isPresent()) {
      logger.info("User found: {}", user.get().getUsername());
    } else {
      logger.warn("User with ID {} not found", id);
    }
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
  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Optional<User>> getUserByUsername(@PathVariable String username) {
    //TODO add better exception hamdling here
    logger.info("Fetching user by username: {}", username);
    Optional<User> user = userService.getUserByUsername(username);
    if (user.isPresent()) {
      logger.info("User found: {}", username);
    } else {
      logger.warn("User with username {} not found", username);
    }
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
  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<UserResponse> getCurrentUser() {
    logger.info("Fetching current user profile");
    UserResponse userResponse = userService.getCurrentUser();
    logger.info("Fetched profile for user: {}", userResponse.getUsername());
    return ResponseEntity.ok(userResponse);
  }
}
