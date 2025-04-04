package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.AttributeMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EditedValueUnchangedException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.AttributeRepository;

import java.util.List;
import java.util.NoSuchElementException;

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

  public Attribute getAttributeById(long attributeId) {
    return attributeRepository.findById(attributeId)
            .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.ATTRIBUTE_NOT_FOUND));
  }

  /**
   * Creates a new category in the database.
   *
   * @param request the CategoryRequest object containing the category name and attributes
   * @return a CategoryResponse object representing the newly created category
   */
  public AttributeResponse createAttribute(AttributeRequest request) {
    if (attributeRepository.existsByAttributeName(request.getName())) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.ATTRIBUTE_ALREADY_EXISTS);
    }

    Category category = categoryService.getCategoryById(request.getCategoryId());

    Attribute attribute = AttributeMapper.toEntity(request, category);
    return AttributeMapper.toDto(attributeRepository.save(attribute));
  }

  public void editAttribute(Long attributeId, AttributeRequest request) {
    Attribute attribute = getAttributeById(attributeId);

    if (attributeRepository.existsByAttributeName(attribute.getAttributeName())) {
      throw new EditedValueUnchangedException(CustomErrorMessage.ATTRIBUTE_NAME_UNCHANGED);
    }

    attribute.setAttributeName(request.getName());
    attribute.setDataType(request.getType());
    attribute.setCategory(categoryService.getCategoryById(request.getCategoryId()));

    attributeRepository.save(attribute);
  }

  public void deleteAttribute(Long attributeId) {
    if (!attributeRepository.existsById(attributeId)) {
      throw new EntityNotFoundException(CustomErrorMessage.ATTRIBUTE_NOT_FOUND);
    }
    attributeRepository.deleteById(attributeId);
  }
}
