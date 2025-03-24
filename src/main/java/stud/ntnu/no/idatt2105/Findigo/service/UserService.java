package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.JWTUtil;
import stud.ntnu.no.idatt2105.Findigo.model.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.model.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.model.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.model.User;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

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

  /**
   * Registers a new user in the system.
   *
   * @param request The {@link RegisterRequest} containing user details.
   * @return A success message upon successful registration.
   * @throws RuntimeException if a user with the given username already exists.
   */
  public String register(RegisterRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new RuntimeException("User already exists!");
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
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

    String token = jwtUtil.generateToken(userDetails);

    return new AuthResponse(token);
  }
}
