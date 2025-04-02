package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for requesting an attribute with a name and type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeRequest {
  /**
   * The name of the attribute.
   */
  private String name;

  /**
   * The type of the attribute (e.g., string, integer, boolean).
   */
  private String type;

  private Long categoryId;
}
