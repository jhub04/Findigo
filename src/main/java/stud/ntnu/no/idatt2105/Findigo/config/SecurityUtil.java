package stud.ntnu.no.idatt2105.Findigo.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
  private final UserRepository userRepository;

  @Transactional
  public User getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = (principal instanceof UserDetails userDetails)
            ? userDetails.getUsername()
            : principal.toString();

    return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public boolean isAdmin() {
    return getCurrentUser().getRoles().stream()
            .anyMatch(role -> role.name().equals("ADMIN"));
  }

  public boolean isCurrentUser(User user) {
    return getCurrentUser().getId().equals(user.getId());
  }

  public boolean isListingOwner(Listing listing) {
    return getCurrentUser().getId().equals(listing.getUser().getId());
  }

  public boolean isCurrentUserOrAdmin(User user) {
    return isCurrentUser(user) || isAdmin();
  }
}

