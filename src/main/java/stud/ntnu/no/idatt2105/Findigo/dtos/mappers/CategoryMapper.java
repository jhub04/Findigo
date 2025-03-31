package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.NoArgsConstructor;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

import java.util.List;

/**
 * Utility class for mapping {@link Category} entities to {@link CategoryResponse} DTOs.
 * <p>
 * This class is responsible for converting a {@code Category} entity, including its attributes,
 * into a {@code CategoryResponse} that can be returned in API responses.
 * </p>
 *
 * <p>Since this is a stateless utility class, it has a private constructor and only
 * contains static methods.</p>
 */
@NoArgsConstructor
public class CategoryMapper {

  /**
   * Converts a {@link Category} entity to a {@link CategoryResponse} DTO.
   *
   * @param category the {@code Category} entity to convert
   * @return a {@code CategoryResponse} containing the category ID, name, and attributes
   */
  public static CategoryResponse toDto(Category category) {
    List<AttributeResponse> attributeDtos = category.getAttributes().stream()
        .map(attr -> new AttributeResponse(
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

  /**
   * Converts a {@link CategoryRequest} DTO to a {@link Category} entity.
   * @param category the {@code CategoryRequest} DTO to convert
   * @return a {@code Category} entity containing the category name and attributes
   */
  public static Category toEntity(CategoryRequest category) {
    Category newCategory = new Category()
        .setCategoryName(category.getName());
    newCategory.setAttributes(
            category.getAttributes().stream()
                .map(attributeRequest -> AttributeMapper.toEntity(attributeRequest, newCategory))
                .toList()
        );
    return newCategory;
  }
}
