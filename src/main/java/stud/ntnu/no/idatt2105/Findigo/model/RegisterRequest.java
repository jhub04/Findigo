package stud.ntnu.no.idatt2105.Findigo.model;

import lombok.Data;

import java.util.Set;

/**
 * Represents a request for user registration.
 * This class contains the necessary information to create a new user account.
 */
@Data
public class RegisterRequest {
  private String username;
  private String password;
  private Set<Role> roles;
}