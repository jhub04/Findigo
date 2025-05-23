package stud.ntnu.no.idatt2105.Findigo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.UnauthorizedOperationException;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.Optional;

/**
 * Utility class for security-related operations.
 *
 * <p>This class provides helper methods for:
 * <ul>
 *     <li>Retrieving the currently authenticated user</li>
 *     <li>Checking user roles and ownership of resources</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

  private final UserRepository userRepository;

  /**
   * Checks if there is an authenticated user in the security context.
   *
   * <p>This method verifies that the authentication object is not null,
   * is marked as authenticated, and that the principal is not anonymous.</p>
   *
   * @return {@code true} if a user is authenticated, {@code false} otherwise
   */
  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null &&
            authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal());
  }


  /**
   * Returns the current authenticated user if available, otherwise empty.
   *
   * @return an Optional containing the user, or empty if not authenticated
   */
  public Optional<User> getCurrentUserIfAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null ||
            !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
      return Optional.empty();
    }

    String username = authentication.getName();
    return userRepository.findByUsername(username);
  }


  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @return the authenticated {@link User}
   * @throws RuntimeException if the user is not found in the repository
   */
  public User getCurrentUser() {
    return getCurrentUserIfAuthenticated()
            .orElseThrow(() -> new UnauthorizedOperationException(CustomErrorMessage.UNAUTHORIZED_OPERATION));
  }

  /**
   * Checks if the currently authenticated user has the ADMIN role.
   *
   * @return {@code true} if the user has the ADMIN role, {@code false} otherwise
   */
  public boolean isAdmin() {
    return getCurrentUser().getUserRoles().stream()
            .anyMatch(role -> role.getRole().toString().equals("ROLE_ADMIN"));
  }

  /**
   * Checks if the provided user matches the currently authenticated user.
   *
   * @param user the user to compare with the current user
   * @return {@code true} if the provided user is the current user, {@code false} otherwise
   */
  public boolean isCurrentUser(User user) {
    return getCurrentUser().getId().equals(user.getId());
  }

  /**
   * Checks if the currently authenticated user is the owner of the specified listing.
   *
   * @param listing the listing to check ownership of
   * @return {@code true} if the current user owns the listing, {@code false} otherwise
   */
  public boolean isListingOwner(Listing listing) {
    return getCurrentUser().getId().equals(listing.getUser().getId());
  }

  /**
   * Checks if the currently authenticated user is either the provided user or has the ADMIN role.
   *
   * @param user the user to compare with the current user
   * @return {@code true} if the current user matches or is an admin, {@code false} otherwise
   */
  public boolean isCurrentUserOrAdmin(User user) {
    return isCurrentUser(user) || isAdmin();
  }
}
