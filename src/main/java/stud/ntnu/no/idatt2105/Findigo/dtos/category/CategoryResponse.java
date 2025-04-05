package stud.ntnu.no.idatt2105.Findigo.dtos.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;

import java.util.List;

/**
 * DTO for returning category details, including its ID, name, and attributes.
 * <p>
 * Provides a complete view of a category, including associated attributes.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing category details including ID, name, and associated attributes.")
public class CategoryResponse {

  /**
   * The unique identifier of the category.
   */
  @Schema(description = "Unique identifier of the category", example = "1")
  private Long id;

  /**
   * The name of the category.
   */
  @Schema(description = "Name of the category", example = "Electronics")
  private String name;

  /**
   * The list of attributes associated with the category.
   */
  @Schema(description = "List of attributes associated with the category")
  private List<AttributeResponse> attributes;
}
