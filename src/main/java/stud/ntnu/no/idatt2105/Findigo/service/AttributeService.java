package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.AttributeMapper;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.CategoryMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.AttributeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service class responsible for handling category-related business logic.
 */
@Service
@RequiredArgsConstructor
public class AttributeService {

  private final AttributeRepository attributeRepository;
  private final CategoryService categoryService;
  private static final Logger logger = LogManager.getLogger(CategoryService.class);

  /**
   * Retrieves all categories from the database and maps them to CategoryResponse DTOs.
   *
   * @return a list of CategoryResponse objects representing all available categories.
   * @throws NoSuchElementException if no categories are found
   */
  public List<AttributeResponse> getAllAttributes() {
    List<Attribute> attributes = attributeRepository.findAll();

    return attributes.stream()
            .map(AttributeMapper::toDto)
            .toList();
  }

  /**
   * Creates a new category in the database.
   *
   * @param request the CategoryRequest object containing the category name and attributes
   * @return a CategoryResponse object representing the newly created category
   */
  public AttributeResponse createAttribute(AttributeRequest request) {
    if (attributeRepository.existsByAttributeName(request.getName())) {
      throw new CategoryAlreadyExistsException("Category with name " + request.getName() + " already exists");
    }

    Category category = categoryService.getCategoryById(request.getCategoryId());

    Attribute attribute = AttributeMapper.toEntity(request, category);
    return AttributeMapper.toDto(attributeRepository.save(attribute));
  }
}
