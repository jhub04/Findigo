package stud.ntnu.no.idatt2105.Findigo.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO for authentication response.
 * <p>
 * Contains the JWT token issued after successful authentication.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Response object containing the JWT token issued after successful authentication.")
public class AuthResponse {

  /**
   * The JWT token issued to the user upon successful authentication.
   */
  @Schema(description = "JWT token issued to the user upon successful authentication", example = "eyJhbGciOiJIUzI1...")
  private String token;
}
