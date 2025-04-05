package stud.ntnu.no.idatt2105.Findigo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

/**
 * Service implementation for loading user details from the database.
 * <p>
 * Implements {@link UserDetailsService} to provide authentication functionality
 * required by Spring Security.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

  private final UserRepository userRepository;

  /**
   * Constructs a new {@code CustomUserDetailsService} with the injected {@code UserRepository}.
   *
   * @param userRepository the repository used to retrieve user data
   */
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Loads a user by their username.
   * <p>
   * This method is used by Spring Security during authentication.
   * If the user is not found, a {@link UsernameNotFoundException} is thrown.
   * </p>
   *
   * @param username the username of the user to be loaded
   * @return the {@link UserDetails} of the authenticated user
   * @throws UsernameNotFoundException if the user is not found in the database
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.info("Attempting to load user by username: {}", username);
    return userRepository.findByUsername(username)
            .orElseThrow(() -> {
              logger.warn("User not found with username: {}", username);
              return new UsernameNotFoundException("User not found: " + username);
            });
  }
}
