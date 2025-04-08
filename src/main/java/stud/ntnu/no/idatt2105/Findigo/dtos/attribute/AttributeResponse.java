package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an attribute definition.
 * <p>
 * Used to transfer attribute-related data between different layers of the application.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO representing an attribute definition.")
public class AttributeResponse {

  /**
   * The unique identifier for the attribute.
   */
  @Schema(description = "The unique identifier of the attribute", example = "1")
  private Long id;

  /**
   * The name of the attribute.
   */
  @Schema(description = "The name of the attribute", example = "Color")
  private String name;

  /**
   * The type of the attribute (e.g., string, integer, boolean).
   */
  @Schema(description = "The data type of the attribute", example = "String")
  private String type;
}
