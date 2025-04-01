package stud.ntnu.no.idatt2105.Findigo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

@Component
public class SecurityUtil {
  private final UserRepository userRepository;

  public SecurityUtil(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

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

  public boolean isCurrentUserOrAdmin(User user) {
    return isCurrentUser(user) || isAdmin();
  }
}

