package stud.ntnu.no.idatt2105.Findigo.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;

import java.util.Set;

/**
 * DTO for user registration requests.
 * <p>
 * Contains necessary information for creating a new user account.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for user registration, containing username, password, and roles.")
public class RegisterRequest {

  /**
   * The desired username for the new account.
   */
  @NotBlank(message = "Username cannot be blank")
  @Schema(description = "The desired username for the new user", example = "newuser")
  private String username;

  /**
   * The desired password for the new account.
   */
  @NotBlank(message = "Password cannot be blank")
  @Schema(description = "The desired password for the new user", example = "strongpassword123")
  private String password;

  /**
   * The roles assigned to the new user.
   */
  @NotEmpty(message = "At least one role must be specified")
  @Schema(description = "Set of roles assigned to the user (e.g., USER, ADMIN)", example = "[\"USER\"]")
  private Set<Role> roles;
}
