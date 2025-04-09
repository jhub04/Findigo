package stud.ntnu.no.idatt2105.Findigo.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing detailed user information.
 * <p>
 * Used for returning full user details in API responses, including user's listings.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing detailed user information, including user's listings.")
public class UserResponse {

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

  /**
   * The list of listings associated with the user.
   */
  @Schema(description = "List of listings created by the user")
  private List<ListingResponse> listings;

  /**
   * List with roles associated with the user
   */
  @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
  private List<String> roles;
}
