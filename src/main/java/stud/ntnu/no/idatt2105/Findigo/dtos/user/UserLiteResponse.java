package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing basic user details.
 * <p>
 * Provides a lightweight representation of a user,
 * typically used for embedding in other responses (e.g., listings, messages).
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Lightweight DTO representing basic user details.")
public class UserLiteResponse {

  /**
   * The unique identifier of the user.
   */
  @Schema(description = "Unique identifier of the user", example = "1")
  private Long id;

  /**
   * The username of the user.
   */
  @Schema(description = "Username of the user", example = "john_doe")
  private String username;

  /**
   * The phone number of the user.
   */
  @Schema(description = "Phone number of the user", example = "12345678")
  private String phoneNumber;
}
