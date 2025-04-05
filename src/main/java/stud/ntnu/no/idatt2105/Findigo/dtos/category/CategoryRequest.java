package stud.ntnu.no.idatt2105.Findigo.dtos.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a category.
 * <p>
 * Contains the necessary information to define a category.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for creating or updating a category, containing the category name.")
public class CategoryRequest {

  /**
   * The name of the category.
   */
  @NotBlank(message = "Category name must not be blank")
  @Schema(description = "The name of the category", example = "Electronics")
  private String name;
}
