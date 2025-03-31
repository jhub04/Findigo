package stud.ntnu.no.idatt2105.Findigo.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryRequest {
  private String name;
  private List<AttributeRequest> attributes;
}
