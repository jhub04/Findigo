package stud.ntnu.no.idatt2105.Findigo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the response returned after a successful authentication.
 * This class contains the authentication token issued to the user.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class AuthResponse {
  private String token;
}