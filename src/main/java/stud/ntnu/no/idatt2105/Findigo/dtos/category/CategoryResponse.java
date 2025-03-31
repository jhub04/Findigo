package stud.ntnu.no.idatt2105.Findigo.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeDefinitionDto;

import java.util.List;

/**
 * DTO for returning category details, including its ID, name, and attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
  private Long id;
  private String name;
  private List<AttributeDefinitionDto> attributes;
}
