package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

/**
 * Utility class for mapping {@link Attribute} entities to {@link AttributeResponse} DTOs.
 */
public class AttributeMapper {

  /**
   * Converts a {@link AttributeResponse} DTO to an {@link Attribute} entity.
   *
   * @param attributeDto the {@code AttributeDefinitionDto} DTO to convert
   * @return an {@code Attribute} containing the attribute ID, name, and type.
   */
  public static Attribute toEntity(AttributeRequest attributeDto, Category category) {
    return new Attribute()
        .setAttributeName(attributeDto.getName())
        .setDataType(attributeDto.getType())
        .setCategory(category);
  }

  public static AttributeResponse toDto(Attribute attribute) {
    return new AttributeResponse(
            attribute.getId(),
            attribute.getAttributeName(),
            attribute.getDataType()
    );
  }
}
