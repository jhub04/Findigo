package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.JWTUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.EditUserDto;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.UsernameAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;
  private static final Logger logger = LogManager.getLogger(UserService.class);

  /**
   * Registers a new user in the system.
   *
   * @param request The {@link RegisterRequest} containing user details.
   * @return A success message upon successful registration.
   * @throws RuntimeException if a user with the given username already exists.
   */
  public String register(RegisterRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      logger.error("Couldn't register " + request.getUsername() + ". Username already taken");
      throw new UsernameAlreadyExistsException("User with username " + request.getUsername() + " already exists");
    }

    User user = new User()
        .setUsername(request.getUsername())
        .setPassword(passwordEncoder.encode(request.getPassword()))
        .setRoles(request.getRoles());

    userRepository.save(user);
    return "User registered successfully!";
  }

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param request The {@link AuthRequest} containing username and password.
   * @return An {@link AuthResponse} containing the generated JWT token.
   */
  public AuthResponse authenticate(AuthRequest request) {
    logger.info("Authenticating user " + request.getUsername());
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    logger.info(request.getUsername() + " credentials authenticated");

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

    logger.info("Trying to create jwt token for " + request.getUsername());
    String token = jwtUtil.generateToken(userDetails);
    logger.info("Token created successfully for " + request.getUsername());

    return new AuthResponse(token);
  }

  /**
   * Retrieves all users from the database. Admin only.
   * @return a list of all users in the database
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Get user by ID.
   * @param id the id of the user to get
   * @return the user with the given id
   * @throws NoSuchElementException if no user with the given id is found
   */
  public User getUserById(Long id) {
    //TODO user this method where it should be used
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new NoSuchElementException("No user with the given id: " + id + " was found");
    }
    return user.get();
  }

  /**
   * Get user by username.
   * @param username the username of the user to get
   * @return the user with the given username
   * @throws UsernameNotFoundException if no user with the given username is found
   */
  public User getUserByUsername(String username) {
    //TODO use this method where it need to be used (message service blant annet)
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("No user with username '" + username + "' was found");
    }
    return user.get();
  }

  /**
   * Get the current logged-in user from the security context.
   *
   * @return the current logged-in user.
   */
  public User getCurrentUser() {
    //TODO use this where needed (message service)
    String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    return getUserByUsername(username);
  }

  /**
   * Edit user details. Only the user itself can edit its own details.
   *
   * @param userDto the new user details.
   * @throws AccessDeniedException if the current logged-in user is not the same as the user being edited.
   * @throws UsernameAlreadyExistsException if the new username is already taken.
   * @throws NoSuchElementException if no user with the given id is found.
   */
  public void editUserDetails(EditUserDto userDto) {
    User currentUser = getCurrentUser();

    User user = getUserById(userDto.getId());

    if (!(currentUser.getUsername().equals(user.getUsername()) || currentUser.getId().equals(user.getId()))) {
      throw new AccessDeniedException("The current logged in user is not the same as the user which is being edited");
    }

    if (user.getUsername().equals(userDto.getUsername())) {
      logger.info("No change in username detected");
    } else if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
      logger.error("Couldn't edit user ID" + userDto.getId() + ". Username already taken");
      throw new UsernameAlreadyExistsException("User with username " + userDto.getUsername() + " already exists");
    } else {
      String oldUsername = user.getUsername();
      user.setUsername(userDto.getUsername());
      logger.info("Changed user with id " + userDto.getId() + " username from " + oldUsername + " to " + userDto.getUsername());
    }

    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    logger.info("New password set for user with id " + userDto.getId());
    userRepository.save(user);
  }


  /**
   * Get all listings favorited by the current user.
   *
   * @return a set of all listings favorited by the current user.
   */
  public Set<Listing> getFavorites() {
    User currentUser = getCurrentUser();

    return currentUser.getFavoriteListings();
  }
}
