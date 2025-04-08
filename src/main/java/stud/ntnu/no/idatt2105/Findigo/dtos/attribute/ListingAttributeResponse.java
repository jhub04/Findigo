package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning a listing attribute with its name and value.
 * <p>
 * Used to represent attributes in listing responses.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents an attribute associated with a listing, including its name and value.")
public class ListingAttributeResponse {

  /**
   * The name of the attribute.
   */
  @Schema(description = "The name of the attribute", example = "Color")
  private String name;

  /**
   * The value assigned to the attribute.
   */
  @Schema(description = "The value of the attribute", example = "Red")
  private String value;
}
