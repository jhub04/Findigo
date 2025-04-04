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
 * Implements {@link UserDetailsService} to provide authentication functionality.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

  /**
   * Constructs a new {@code CustomUserDetailsService} with the injected {@code UserRepository}.
   *
   * @param userRepository the repository used to retrieve user data.
   */
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Loads a user by their username.
   *
   * @param username the username of the user to be loaded.
   * @return the {@link UserDetails} of the authenticated user.
   * @throws UsernameNotFoundException if the user is not found in the database.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.info("Loading user by username: " + username);
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    //legge til for roller ogsa
  }
}
