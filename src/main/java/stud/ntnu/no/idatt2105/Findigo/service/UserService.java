package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.JWTUtil;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.ListingMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.MyUserRequest;
import stud.ntnu.no.idatt2105.Findigo.entities.FavoriteListings;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.entities.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.UserMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.AdminUserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.repository.*;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for handling user operations such as authentication,
 * registration, profile updates, and favorite listings management.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserRolesRepository  userRolesRepository;
  private final JWTUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;
  private final ListingRepository listingRepository; // TODO: Avoid using listingRepository in UserService
  private final FavoriteListingsRepository favoriteListingsRepository;
  private final SecurityUtil securityUtil;
  private final UserMapper userMapper;
  private final ListingMapper listingMapper;
  private final SaleRepository saleRepository;

  private static final Logger logger = LogManager.getLogger(UserService.class);

  @Value("${security.jwt.access-token-expiration}")
  private long accessTokenExpiration;

  /**
   * Registers a new user.
   *
   * @param request the registration details
   * @return success message upon successful registration
   */
  public String register(AuthRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      logger.error("Username '{}' is already taken", request.getUsername());
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
    }

    User user = new User()
        .setUsername(request.getUsername())
        .setPassword(passwordEncoder.encode(request.getPassword()));

    UserRoles userRole = new UserRoles()
        .setUser(user)
        .setRole(Role.ROLE_USER); //Standard role for new users

    user.getUserRoles().add(userRole);

    userRepository.save(user);
    return "User registered successfully!";
  }

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param request the authentication request
   * @return the authentication response with token
   */
  public AuthResponse authenticate(AuthRequest request) {
    logger.info("Authenticating user '{}'", request.getUsername());

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    String token = jwtUtil.generateToken(userDetails);

    logger.info("JWT token successfully generated for user '{}'", request.getUsername());

    return new AuthResponse(token);
  }

  /**
   * Retrieves all users.
   *
   * @return list of user responses
   */
  @Transactional
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
            .map(userMapper::toDTO)
            .toList();
  }

  /**
   * Retrieves a user by ID.
   *
   * @param id the user ID
   * @return the user entity
   */
  public User getUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));
  }

  /**
   * Retrieves a user by ID as DTO.
   *
   * @param id the user ID
   * @return the user response DTO
   */
  @Transactional()
  public UserResponse getUserDtoById(Long id) {
    User user = getUserById(id);
    return userMapper.toDTO(user);
  }

  /**
   * Edits the current user's details.
   *
   * @param request the new user details
   */
  @Transactional
  public void editMyUserDetails(MyUserRequest request) {
    User currentUser = securityUtil.getCurrentUser();

    if (!currentUser.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
      logger.error("Username '{}' already exists", request.getUsername());
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
    }

    if (request.getPassword() == null || request.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }

    currentUser.setUsername(request.getUsername());
    currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
    currentUser.setPhoneNumber(request.getPhoneNumber());

    userRepository.save(currentUser);
    logger.info("User ID {} details updated", currentUser.getId());
  }

  /**
   * Edits another user's details (admin only).
   *
   * @param request the new user details
   * @param userId the target user ID
   */
  public void editUserDetails(AdminUserRequest request, Long userId) {
    User user = getUserById(userId);

    if (!user.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
    }

    user.setUsername(request.getUsername());
    user.setPhoneNumber(request.getPhoneNumber());

    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    if (request.getRoles() != null && !request.getRoles().isEmpty()) {
      userRolesRepository.deleteByUser(user);
      for (Role role: request.getRoles()) {
        UserRoles userRole = new UserRoles()
                .setUser(user)
                .setRole(role);
        userRolesRepository.save(userRole);
      }
    }

    userRepository.save(user);
    logger.info("User ID {} updated successfully by admin", userId);
  }

  /**
   * Retrieves listings for the current user.
   *
   * @return list of listing responses
   */
  @Transactional
  public List<ListingResponse> getMyActiveListings() {
    return getMyListingWithStatus(ListingStatus.ACTIVE);
  }

  public List<ListingResponse> getMyListingWithStatus(ListingStatus listingStatus) {
    User currentUser = securityUtil.getCurrentUser();

    List<Listing> allMyListings = currentUser.getListings();

    List<Listing> filteredListings = allMyListings
        .stream()
        .filter(listing -> listing.getListingStatus() == listingStatus)
        .toList();

    return filteredListings.stream()
        .map(listingMapper::toDto)
        .toList();
  }

  /**
   * Retrieves listings for a specific user.
   *
   * @param id the user ID
   * @return list of listing responses
   */
  @Transactional
  public List<ListingResponse> getUserListings(Long id) {
    User user = getUserById(id);
    return getListingsUtil(user);
  }

  /**
   * Adds a listing to the current user's favorites.
   *
   * @param listingId the listing ID
   * @return the listing response
   */
  @Transactional
  public ListingResponse addFavorite(long listingId) {
    User currentUser = getCurrentUser();
    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    FavoriteListings favorite = new FavoriteListings()
            .setListing(listing)
            .setUser(currentUser);

    favoriteListingsRepository.save(favorite);
    return listingMapper.toDto(listing);
  }

  /**
   * Removes a listing from the current user's favorites.
   *
   * @param listingId the listing ID
   * @return the listing response
   */
  @Transactional
  public ListingResponse deleteFavorite(long listingId) {
    User currentUser = getCurrentUser();
    Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.LISTING_NOT_FOUND));

    FavoriteListings favorite = favoriteListingsRepository.findByUserAndListing(currentUser, listing)
            .orElseThrow(() -> new NoSuchElementException("No favorite listing with id " + listingId));

    favoriteListingsRepository.delete(favorite);
    return listingMapper.toDto(listing);
  }

  /**
   * Creates a new user (admin only).
   *
   * @param request the user request
   * @return the user lite response
   */
  public UserLiteResponse createUser(AdminUserRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
    }

    User user = userMapper.toEntity(request);
    return userMapper.toLiteDto(user);
  }

  /**
   * Retrieves the current user.
   *
   * @return the user entity
   */
  public User getCurrentUser() {
    return securityUtil.getCurrentUser();
  }

  /**
   * Retrieves the current user as DTO.
   *
   * @return the user response DTO
   */
  @Transactional
  public UserResponse getCurrentDtoUser() {
    return userMapper.toDTO(getCurrentUser());
  }

  /**
   * Utility method to retrieve listings for a given user.
   *
   * @param user the user entity
   * @return list of listing responses
   */
  private List<ListingResponse> getListingsUtil(User user) {
    return user.getListings().stream()
            .map(listingMapper::toDto)
            .toList();
  }

  /**
   * Authenticates a user and returns a JWT token as a secure cookie.
   *
   * @param request the authentication request
   * @return the response cookie containing the JWT token
   */
  public ResponseCookie authenticateAndGetCookie(AuthRequest request) {
    AuthResponse token = authenticate(request);

    return ResponseCookie.from("auth-token", token.getToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ofSeconds(accessTokenExpiration))
            .build();
  }

  /**
   * Creates a cookie to invalidate the JWT token (logout).
   *
   * @return the response cookie for logout
   */
  public ResponseCookie createLogoutCookie() {
    return ResponseCookie.from("auth-token", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ZERO)
            .build();
  }

  /**
   * Validates a JWT token.
   *
   * @param token the JWT token
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    return token != null && jwtUtil.isTokenValid(token);
  }

  /**
   * Retrieves the current user's favorite listings.
   *
   * @return list of listing responses
   */
  @Transactional
  public List<ListingResponse> getFavorites() {
    User currentUser = getCurrentUser();

    return favoriteListingsRepository.findAllByUser(currentUser).stream()
            .map(FavoriteListings::getListing)
            .map(listingMapper::toDto)
            .toList();
  }

  /**
   * Retrieves the current user's archived listings.
   * @return list of listing responses
   */
  @Transactional
  public List<ListingResponse> getMyArchivedListings() {
    return getMyListingWithStatus(ListingStatus.ARCHIVED);
  }

  /**
   * Retrieves the current user's sold listings.
   *
   * @return list of listing responses
   */
  @Transactional
  public List<ListingResponse> getMySoldListings() {
    return getMyListingWithStatus(ListingStatus.SOLD);
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USERNAME_NOT_FOUND));
  }
}
