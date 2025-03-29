package stud.ntnu.no.idatt2105.Findigo.dtos.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingAttributeRequest {
  //TODO javadoc
  private Long attributeId;
  private String value;
}
