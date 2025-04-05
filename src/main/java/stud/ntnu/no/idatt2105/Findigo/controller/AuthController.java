package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.Map;

/**
 * Controller for handling authentication-related endpoints.
 *
 * <p>Provides endpoints for user registration and authentication.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentications", description = "Endpoints for user registration and login")
public class AuthController {

  private static final Logger logger = LogManager.getLogger(AuthController.class);

  private final UserService userService;

  /**
   * Register a new user.
   *
   * @param registerRequest the registration request containing user details.
   * @return a response entity with the registration status.
   */
  @Operation(summary = "Register new user", description = "Creates a new user account based on provided details")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User registered successfully"),
          @ApiResponse(responseCode = "409", description = "User with the given username already exists")
  })
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Validated RegisterRequest registerRequest) {
    logger.info("Auth: Attempting to register user with username '{}'", registerRequest.getUsername());
    String registerStatus = userService.register(registerRequest);
    logger.info("Auth: User registered successfully with username '{}'", registerRequest.getUsername());
    return ResponseEntity.ok(registerStatus);
  }

  /**
   * Authenticate and return JWT token as cookie.
   *
   * @param authRequest the authentication request containing username and password
   * @param response    the HTTP response to set the cookie
   * @return a response entity with the authentication status and JWT token as a cookie
   */
  @Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT token as cookie if valid")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Login successful"),
          @ApiResponse(responseCode = "401", description = "Invalid username or password"),
          @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/login")
  public ResponseEntity<String> authenticate(@RequestBody @Validated AuthRequest authRequest, HttpServletResponse response) {
    logger.info("Auth: Authenticating user with username '{}'", authRequest.getUsername());
    ResponseCookie cookie = userService.authenticateAndGetCookie(authRequest);
    logger.debug("Auth: JWT token created for username '{}': {}", authRequest.getUsername(), cookie.getValue());
    response.addHeader("Set-Cookie", cookie.toString());
    logger.info("Auth: JWT token set as cookie for username '{}'", authRequest.getUsername());
    return ResponseEntity.ok("Login successful");
  }

  /**
   * Logout the user by returning an invalid logout cookie.
   *
   * @param response the HTTP response to set the logout cookie
   * @return a response entity indicating successful logout
   */
  @Operation(summary = "Logout", description = "Logs out the user by invalidating the JWT token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Logout successful"),
          @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    logger.info("Auth: Logging out user");
    ResponseCookie logoutCookie = userService.createLogoutCookie();
    response.addHeader("Set-Cookie", logoutCookie.toString());
    logger.info("Auth: User logged out successfully");
    return ResponseEntity.ok().build();
  }

  /**
   * Check authentication status by validating JWT token from cookie.
   *
   * @param token the JWT token from the cookie
   * @return a response entity with the authentication status
   */
  @Operation(summary = "Check authentication status", description = "Checks if the user is authenticated by validating the JWT token from the cookie")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Checked if user was authenticated, can return true or false"),
  })
  @GetMapping("/auth-status")
  public ResponseEntity<Map<String, Boolean>> checkAuthStatus(@CookieValue(name = "auth-token", required = false) String token) {
    logger.info("Auth: Checking authentication status");
    boolean isAuthenticated = userService.validateToken(token);
    logger.info("Auth: Authentication status is {}", isAuthenticated);
    return ResponseEntity.ok(Map.of("authenticated", isAuthenticated));
  }
}
