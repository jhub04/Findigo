package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning a listing attribute with a name and value.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingAttributeResponse {
  private String name;
  private String value;
}
