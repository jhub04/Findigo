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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  //global Try catch

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
    try {
      return ResponseEntity.ok(userService.register(registerRequest));
    } catch (Error e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(userService.authenticate(authRequest));
  }
}
