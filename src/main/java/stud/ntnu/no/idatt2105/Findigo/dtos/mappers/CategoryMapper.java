package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import lombok.experimental.UtilityClass;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

import java.util.List;

/**
 * Utility class for mapping {@link Category} entities to {@link CategoryResponse} DTOs and vice versa.
 * <p>
 * This class is responsible for converting {@link Category} entities, including their attributes,
 * into {@link CategoryResponse} objects for API responses, and from {@link CategoryRequest} DTOs into entities.
 * </p>
 */
@UtilityClass
public class CategoryMapper {

  /**
   * Converts a {@link Category} entity to a {@link CategoryResponse} DTO.
   *
   * @param category the {@link Category} entity to convert
   * @return a {@link CategoryResponse} containing the category ID, name, and attributes
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
   *
   * @param categoryRequest the {@link CategoryRequest} DTO to convert
   * @return a {@link Category} entity containing the category name
   */
  public static Category toEntity(CategoryRequest categoryRequest) {
    return new Category()
            .setCategoryName(categoryRequest.getName());
  }
}
