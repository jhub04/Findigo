package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating an attribute.
 * <p>
 * Contains the name, type, and associated category ID of the attribute.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating or updating an attribute.")
public class AttributeRequest {

  /**
   * The name of the attribute.
   */
  @NotBlank
  @Schema(description = "The name of the attribute", example = "Color")
  private String name;

  /**
   * The data type of the attribute (e.g., string, integer, boolean).
   */
  @NotBlank
  @Schema(description = "The data type of the attribute", example = "String")
  private String type;

  /**
   * The ID of the category to which the attribute belongs.
   */
  @NotNull
  @Schema(description = "The ID of the category the attribute belongs to", example = "1")
  private Long categoryId;
}
