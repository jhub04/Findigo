package stud.ntnu.no.idatt2105.Findigo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.config.SecurityUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.CategoryMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EditedValueUnchangedException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.AttributeRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;

/**
 * Service class responsible for handling category-related business logic.
 * <p>
 * Provides functionality to create, retrieve, edit, and delete categories.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  private static final Logger logger = LogManager.getLogger(CategoryService.class);

  private final CategoryRepository categoryRepository;
  private final SecurityUtil securityUtil;

  /**
   * Retrieves all categories from the database and maps them to CategoryResponse DTOs.
   *
   * @return a list of {@link CategoryResponse} objects representing all available categories
   */
  @Transactional
  public List<CategoryResponse> getAllCategories() {
    logger.info("Fetching all categories");
    List<CategoryResponse> categories = categoryRepository.findAll().stream()
            .map(CategoryMapper::toDto)
            .toList();
    logger.info("Fetched {} categories", categories.size());
    return categories;
  }

  /**
   * Retrieves a specific category entity by its ID.
   *
   * @param categoryId the ID of the category to retrieve
   * @return the {@link Category} entity
   * @throws AppEntityNotFoundException if the category is not found
   */
  public Category getCategoryById(long categoryId) {
    logger.info("Fetching category with ID {}", categoryId);
    return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));
  }

  /**
   * Retrieves a specific category as a DTO by its ID.
   *
   * @param categoryId the ID of the category to retrieve
   * @return the {@link CategoryResponse} DTO
   * @throws AppEntityNotFoundException if the category is not found
   */
  public CategoryResponse getCategoryDtoById(long categoryId) {
    logger.info("Fetching category DTO with ID {}", categoryId);
    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));
    logger.info("Category DTO with ID {} fetched successfully", categoryId);
    return CategoryMapper.toDto(category);
  }

  /**
   * Creates a new category in the database.
   *
   * @param request the {@link CategoryRequest} object containing the category details
   * @return a {@link CategoryResponse} representing the newly created category
   * @throws EntityAlreadyExistsException if a category with the same name already exists
   */
  public CategoryResponse createCategory(CategoryRequest request) {
    securityUtil.checkAdminAccess();

    logger.info("Creating category with name '{}'", request.getName());
    if (categoryRepository.existsByCategoryName(request.getName())) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.CATEGORY_ALREADY_EXISTS);
    }

    Category category = CategoryMapper.toEntity(request);
    categoryRepository.save(category);
    logger.info("Category with name '{}' created successfully", request.getName());
    return CategoryMapper.toDto(category);
  }

  /**
   * Edits an existing category.
   *
   * @param categoryId the ID of the category to edit
   * @param request    the {@link CategoryRequest} containing the new category details
   * @throws EditedValueUnchangedException if the new category name is the same as the current one
   * @throws AppEntityNotFoundException    if the category is not found
   */
  public void editCategory(Long categoryId, CategoryRequest request) {
    securityUtil.checkAdminAccess();

    logger.info("Editing category with ID {}", categoryId);
    Category category = getCategoryById(categoryId);

    if (categoryRepository.existsByCategoryName(request.getName())) {
      throw new EditedValueUnchangedException(CustomErrorMessage.CATEGORY_NAME_UNCHANGED);
    }

    category.setCategoryName(request.getName());
    categoryRepository.save(category);
    logger.info("Category with ID {} edited successfully", categoryId);
  }

  /**
   * Deletes an existing category from the database.
   * <p>
   * This operation is transactional to ensure data consistency.
   * </p>
   *
   * @param categoryId the ID of the category to delete
   * @throws AppEntityNotFoundException if the category is not found
   */
  @Transactional
  public void deleteCategory(Long categoryId) {
    securityUtil.checkAdminAccess();

    logger.info("Deleting category with ID {}", categoryId);
    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));
    categoryRepository.delete(category);
    logger.info("Category with ID {} deleted successfully", categoryId);
  }
}
