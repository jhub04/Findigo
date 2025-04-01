package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for requesting a listing attribute with an ID and value.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingAttributeRequest {
  private Long attributeId;
  private String value;
}
