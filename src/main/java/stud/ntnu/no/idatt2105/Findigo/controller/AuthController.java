package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.model.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.model.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.model.RegisterRequest;
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
  private final UserService userService;

  /**
   * Register a new user.
   */
  @Operation(summary = "Register new user", description = "Creates a new user account based on " +
      "provided details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User registered successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error during registration")
  })
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
    try {
      return ResponseEntity.ok(userService.register(registerRequest));
    } catch (Error e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * Authenticate and return JWT token.
   */
  @Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT " +
      "token if valid")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful"),
      @ApiResponse(responseCode = "401", description = "Invalid username or password")
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(userService.authenticate(authRequest));
  }
}
