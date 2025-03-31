package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an attribute definition.
 * This class is used to transfer attribute-related data between different
 * layers of the application.
 * <p>
 * An attribute definition consists of an ID, a name, and a type.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDefinitionDto {

  /**
   * The unique identifier for the attribute definition.
   */
  private Long id;

  /**
   * The name of the attribute.
   */
  private String name;

  /**
   * The type of the attribute (e.g., string, integer, boolean).
   */
  private String type;
}
