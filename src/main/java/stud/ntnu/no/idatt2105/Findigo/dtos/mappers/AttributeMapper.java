package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.experimental.UtilityClass;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

/**
 * Utility class for mapping {@link Attribute} entities to {@link AttributeResponse} DTOs and vice versa.
 */
@UtilityClass
public class AttributeMapper {

  /**
   * Converts an {@link AttributeRequest} DTO to an {@link Attribute} entity.
   *
   * @param attributeRequest the {@link AttributeRequest} DTO to convert
   * @param category the {@link Category} associated with the attribute
   * @return an {@link Attribute} entity containing the attribute details
   */
  public static Attribute toEntity(AttributeRequest attributeRequest, Category category) {
    return new Attribute()
            .setAttributeName(attributeRequest.getName())
            .setDataType(attributeRequest.getType())
            .setCategory(category);
  }

  /**
   * Converts an {@link Attribute} entity to an {@link AttributeResponse} DTO.
   *
   * @param attribute the {@link Attribute} entity to convert
   * @return an {@link AttributeResponse} DTO with the attribute details
   */
  public static AttributeResponse toDto(Attribute attribute) {
    return new AttributeResponse(
            attribute.getId(),
            attribute.getAttributeName(),
            attribute.getDataType()
    );
  }
}
