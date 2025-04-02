package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.JWTUtil;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.UserMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.UsernameAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;

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
  private final ListingService listingService;
  private final UserMapper userMapper;
  private final SecurityUtil securityUtil;

  /**
   * Registers a new user in the system.
   *
   * @param request The {@link RegisterRequest} containing user details.
   * @return A success message upon successful registration.
   * @throws RuntimeException if a user with the given username already exists.
   */
  public String register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
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
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(userMapper::toDTO).toList();
  }

  /**
   * Get user by ID.
   * @param id the id of the user to get
   * @return the user with the given id
   * @throws NoSuchElementException if no user with the given id is found
   */
  public User getUserById(Long id) {
    //TODO user this method where it should be used
    return userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No user with the given id: " + id + " was found"));
  }

  public UserResponse getUserDtoById(Long id) {
    //TODO user this method where it should be used
    User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No user with the given id: " + id + " was found"));

    return userMapper.toDTO(user);
  }

  /**
   * Edit user details. Only the user itself can edit its own details.
   *
   * @param request the new user details.
   * @throws AccessDeniedException if the current logged-in user is not the same as the user being edited.
   * @throws UsernameAlreadyExistsException if the new username is already taken.
   * @throws NoSuchElementException if no user with the given id is found.
   */
  public void editMyUserDetails(UserRequest request) {
    User currentUser = securityUtil.getCurrentUser();

    // Username check
    if (!currentUser.getUsername().equals(request.getUsername())) {
      if (userRepository.existsByUsername(request.getUsername())) {
        logger.error("Failed to update user: username '{}' already taken", request.getUsername());
        throw new UsernameAlreadyExistsException("Username already taken: " + request.getUsername());
      }

      logger.info("Changing username from '{}' to '{}'", currentUser.getUsername(), request.getUsername());
      currentUser.setUsername(request.getUsername());
    } else {
      logger.info("No change in username for user ID {}", currentUser.getId());
    }

    // Always update password
    currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
    logger.info("Password updated for user ID {}", currentUser.getId());

    userRepository.save(currentUser);
  }

  public void editUserDetails(UserRequest request, Long userId) {
    User user = getUserById(userId);

    // Check if username is changed
    if (!user.getUsername().equals(request.getUsername())) {
      if (userRepository.existsByUsername(request.getUsername())) {
        throw new UsernameAlreadyExistsException("Username already taken: " + request.getUsername());
      }
      logger.info("Changed username for user ID {} from '{}' to '{}'", userId, user.getUsername(), request.getUsername());
      user.setUsername(request.getUsername());
    } else {
      logger.info("No change in username for user ID {}", userId);
    }

    // Update password if changed
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
      logger.info("Password updated for user ID {}", userId);
    }

    // Update roles
    if (request.getRoles() != null && !request.getRoles().isEmpty()) {
      user.setRoles(request.getRoles());
      logger.info("Roles updated for user ID {}: {}", userId, request.getRoles());
    }

    userRepository.save(user);
    logger.info("User ID {} successfully updated", userId);
  }

  public List<ListingResponse> getMyListings() {
    User currentUser = securityUtil.getCurrentUser();
    return getListingsUtil(currentUser);
  } //TODO: admin og isowner autentisering i service også

  public List<ListingResponse> getUserListings(Long id) {
    User user = getUserById(id);

    return getListingsUtil(user);
  }

  public UserLiteResponse createUser(UserRequest req) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new UsernameAlreadyExistsException("User with username " + req.getUsername() + " already exists");
    }

    User user = userMapper.toEntity(req);
    return userMapper.toLiteDto(userRepository.save(user));
  }

  public UserResponse getCurrentUser() {
    User user = securityUtil.getCurrentUser();
    return userMapper.toDTO(user);
  }

  private List<ListingResponse> getListingsUtil(User user) {
    return user.getListings().stream()
            .map(ListingMapper::toDto)
            .toList();
  }
}

