package stud.ntnu.no.idatt2105.Findigo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  // Get all users (Only Admin)
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  // Get user by ID (Only Admin)
  @GetMapping("/id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  // Get user by Username (Only Admin)
  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Optional<User>> getUserByUsername(@PathVariable String username) {
    return ResponseEntity.ok(userService.getUserByUsername(username));
  }

  // Get profile of the logged-in user (Any authenticated user)
  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<UserResponse> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }

}
