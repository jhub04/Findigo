package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
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
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.UserMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.ListingRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
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
  private final ListingRepository listingRepository; //TODO: ikke bruk listingrepo i userservice
  private static final Logger logger = LogManager.getLogger(UserService.class);
  private final ListingService listingService;
  private final UserMapper userMapper;
  private final SecurityUtil securityUtil;
  @Value("${security.jwt.access-token-expiration}")
  private long accessTokenExpiration;


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
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
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
            .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));
  }

  public UserResponse getUserDtoById(Long id) {
    //TODO user this method where it should be used
    User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));

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
        throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
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
        throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
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

  @Transactional
  public List<ListingResponse> getMyListings() {
    User currentUser = securityUtil.getCurrentUser();
    return getListingsUtil(currentUser);
  } //TODO: admin og isowner autentisering i service også

  public List<ListingResponse> getUserListings(Long id) {
    User user = getUserById(id);

    return getListingsUtil(user);
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

  /**
   * Add a listing to the current user's favorites.
   *
   * @param listingId the id of the listing to add to favorites.
   */
  public ListingResponse addFavorite(long listingId) {
    User currentUser = getCurrentUser();
    logger.info("Got current user " + currentUser);
    Listing favorite = listingRepository.findById(listingId)
        .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));
    logger.info("Found listing " + favorite.getId());

    currentUser.addFavorite(favorite);

    logger.info("Added favorite");
    userRepository.save(currentUser);

    logger.info("Saves in db");
    return ListingMapper.toDto(favorite);
  }


  public ListingResponse deleteFavorite(long listingId) {
    User currentUser = getCurrentUser();
    Listing favorite = listingRepository.findById(listingId)
        .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));
    currentUser.removeFavorite(favorite);
    userRepository.save(currentUser);
    return ListingMapper.toDto(favorite);
  }

  public UserLiteResponse createUser(UserRequest req) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
    }

    User user = userMapper.toEntity(req);
    return userMapper.toLiteDto(userRepository.save(user));
  }

  public User getCurrentUser() {
    return securityUtil.getCurrentUser();
  }

  public UserResponse getCurrentDtoUser() {
    return userMapper.toDTO(securityUtil.getCurrentUser());
  }

  private List<ListingResponse> getListingsUtil(User user) {
    return user.getListings().stream()
            .map(ListingMapper::toDto)
            .toList();
  }

  public ResponseCookie authenticateAndGetCookie(AuthRequest authRequest) {
    AuthResponse token = authenticate(authRequest);

    return ResponseCookie.from("auth-token", token.getToken())
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(Duration.ofSeconds(accessTokenExpiration))
        .build();
  }

  public ResponseCookie createLogoutCookie() {
    return ResponseCookie.from("auth-token", "")
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(Duration.ofSeconds(0))
        .build();
  }

  public boolean validateToken(String token) {
    return token != null && jwtUtil.isTokenValid(token);
  }
}

