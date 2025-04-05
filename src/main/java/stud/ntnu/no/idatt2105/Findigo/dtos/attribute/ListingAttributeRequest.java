package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning a specific attribute to a listing.
 * <p>
 * Contains the attribute ID and its corresponding value for the listing.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for assigning an attribute and its value to a listing.")
public class ListingAttributeRequest {

  /**
   * The unique identifier of the attribute.
   */
  @NotNull(message = "Attribute ID cannot be null")
  @Schema(description = "The ID of the attribute", example = "1")
  private Long attributeId;

  /**
   * The value of the attribute to be assigned to the listing.
   */
  @NotBlank(message = "Attribute value cannot be blank")
  @Schema(description = "The value of the attribute for the listing", example = "Red")
  private String value;
}
