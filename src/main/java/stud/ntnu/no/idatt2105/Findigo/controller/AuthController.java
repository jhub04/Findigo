package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;


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
  @Operation(summary = "Register new user", description = "Creates a new user account based on " +
      "provided details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User registered successfully"),
      @ApiResponse(responseCode = "409", description = "User with the given username already exists")
  })
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
    logger.info("Trying to register new user with username " + registerRequest.getUsername());
    String registerStatus = userService.register(registerRequest);
    logger.info("User with username " + registerRequest.getUsername() + " registered successfully");
    return ResponseEntity.ok(registerStatus);
  }

  /**
   * Authenticate and return JWT token as cookie.
   *
   * @param authRequest the authentication request containing username and password
   * @param response the HTTP response to set the cookie
   * @return a response entity with the authentication status and JWT token as a cookie
   */
  @Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT " +
      "token as cookie if valid")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful"),
      @ApiResponse(responseCode = "401", description = "Invalid username or password"),
      @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/login")
  public ResponseEntity<String> authenticate(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
    logger.info("Logging in user with username "+ authRequest.getUsername());
    ResponseCookie cookie = userService.authenticateAndGetCookie(authRequest);
    logger.info("Authenticated " +authRequest.getUsername() + " successfully");
    response.addHeader("Set-Cookie", cookie.toString());
    return ResponseEntity.ok("Login successful");
  }
}
