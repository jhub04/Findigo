package stud.ntnu.no.idatt2105.Findigo.controller;

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
public class AuthController {
  private final UserService userService;

  /**
   * Handles user registration requests.
   *
   * <p>Accepts a {@link RegisterRequest} object containing user details
   * and registers the user in the system.</p>
   *
   * <p>Returns an HTTP 500 response if an error occurs during registration.</p>
   *
   * @param registerRequest the user registration request containing necessary details.
   * @return a {@link ResponseEntity} containing a success message or an error message.
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
    try {
      return ResponseEntity.ok(userService.register(registerRequest));
    } catch (Error e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * Handles user login requests.
   *
   * <p>Accepts an {@link AuthRequest} object containing login credentials
   * and authenticates the user.</p>
   *
   * @param authRequest the authentication request containing user credentials.
   * @return a {@link ResponseEntity} containing an authentication token if successful.
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(userService.authenticate(authRequest));
  }
}
