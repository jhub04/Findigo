package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
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
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.AttributeRepository;

import java.util.List;

/**
 * Service class responsible for handling attribute-related business logic.
 * <p>
 * Provides functionality for retrieving, creating, editing, and deleting attributes.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AttributeService {

  private final AttributeRepository attributeRepository;
  private final CategoryService categoryService;

  private static final Logger logger = LogManager.getLogger(AttributeService.class);

  /**
   * Retrieves all attributes from the database and maps them to AttributeResponse DTOs.
   *
   * @return a list of {@link AttributeResponse} objects representing all available attributes
   */
  public List<AttributeResponse> getAllAttributes() {
    logger.info("Fetching all attributes");
    List<Attribute> attributes = attributeRepository.findAll();
    logger.info("Fetched {} attributes", attributes.size());
    return attributes.stream()
            .map(AttributeMapper::toDto)
            .toList();
  }

  /**
   * Retrieves a specific attribute by its ID.
   *
   * @param attributeId the ID of the attribute to retrieve
   * @return the {@link Attribute} entity if found
   * @throws AppEntityNotFoundException if the attribute is not found
   */
  @Transactional
  public Attribute getAttributeById(long attributeId) {
    logger.info("Fetching attribute with ID {}", attributeId);
    return attributeRepository.findById(attributeId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.ATTRIBUTE_NOT_FOUND));
  }

  /**
   * Creates a new attribute and associates it with a category.
   *
   * @param request the {@link AttributeRequest} object containing the attribute details
   * @return an {@link AttributeResponse} representing the newly created attribute
   * @throws EntityAlreadyExistsException if an attribute with the same name already exists
   */
  public AttributeResponse createAttribute(AttributeRequest request) {
    logger.info("Creating attribute with name {}", request.getName());

    Category category = categoryService.getCategoryById(request.getCategoryId());
    Attribute attribute = AttributeMapper.toEntity(request, category);
    Attribute savedAttribute = attributeRepository.save(attribute);

    logger.info("Attribute created with ID {}", savedAttribute.getId());
    return AttributeMapper.toDto(savedAttribute);
  }

  /**
   * Edits an existing attribute.
   *
   * @param attributeId the ID of the attribute to edit
   * @param request     the {@link AttributeRequest} containing the new attribute details
   * @throws EditedValueUnchangedException if the new name is the same as the existing name
   * @throws AppEntityNotFoundException    if the attribute is not found
   */
  @Transactional
  public void editAttribute(Long attributeId, AttributeRequest request) {
    logger.info("Editing attribute with ID {}", attributeId);
    Attribute attribute = getAttributeById(attributeId);

    if (attributeRepository.existsByAttributeName(request.getName())) {
      throw new EditedValueUnchangedException(CustomErrorMessage.ATTRIBUTE_NAME_UNCHANGED);
    }

    attribute.setAttributeName(request.getName());
    attribute.setDataType(request.getType());
    attribute.setCategory(categoryService.getCategoryById(request.getCategoryId()));

    attributeRepository.save(attribute);
    logger.info("Attribute with ID {} edited successfully", attributeId);
  }

  /**
   * Deletes an attribute by its ID.
   *
   * @param attributeId the ID of the attribute to delete
   * @throws AppEntityNotFoundException if the attribute is not found
   */
  public void deleteAttribute(Long attributeId) {
    logger.info("Deleting attribute with ID {}", attributeId);
    if (!attributeRepository.existsById(attributeId)) {
      throw new AppEntityNotFoundException(CustomErrorMessage.ATTRIBUTE_NOT_FOUND);
    }
    attributeRepository.deleteById(attributeId);
    logger.info("Attribute with ID {} deleted successfully", attributeId);
  }
}
