package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeDefinitionDto;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

import java.util.List;

@NoArgsConstructor
public class CategoryMapper {
  //TODO javadoc

  public static CategoryResponse toDto(Category category) {
    List<AttributeDefinitionDto> attributeDtos = category.getAttributes().stream()
            .map(attr -> new AttributeDefinitionDto(
                    attr.getId(),
                    attr.getAttributeName(),
                    attr.getDataType()
            ))
            .toList();

    return new CategoryResponse(
            category.getId(),
            category.getCategoryName(),
            attributeDtos
    );
  }
}