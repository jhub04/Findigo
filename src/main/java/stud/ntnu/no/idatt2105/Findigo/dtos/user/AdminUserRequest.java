package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;

import java.util.Set;

/**
 * Data Transfer Object (DTO) for creating or updating a user.
 * <p>
 * Contains user registration or update details, including username, password, phone number, and roles.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for user creation or update, including username, password, phone number, and roles.")
public class AdminUserRequest {

  /**
   * The username of the user.
   */
  @NotBlank(message = "Username cannot be blank")
  @Schema(description = "The username of the user", example = "john_doe")
  private String username;

  /**
   * The password of the user.
   */
  @NotBlank(message = "Password cannot be blank")
  @Schema(description = "The password of the user", example = "securePassword123")
  private String password;

  /**
   * The phone number of the user.
   */
  @NotNull(message = "Phone number cannot be null")
  @Schema(description = "The phone number of the user", example = "12345678")
  private String phoneNumber;

  /**
   * The set of roles assigned to the user.
   */
  @NotEmpty(message = "User must have at least one role")
  @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
  private Set<Role> roles;
}
