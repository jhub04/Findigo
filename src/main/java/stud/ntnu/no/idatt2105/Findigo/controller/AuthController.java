package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
   * Authenticate and return JWT token.
   */
  @Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT " +
      "token if valid")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful"),
      @ApiResponse(responseCode = "401", description = "Invalid username or password"),
      @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
    logger.info("Logging in user with username "+ authRequest.getUsername());
    AuthResponse response = userService.authenticate(authRequest);
    logger.info("Authenticated " +authRequest.getUsername() + " successfully");
    return ResponseEntity.ok(response);
  }
}
