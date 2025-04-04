package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.CategoryMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.exception.CustomErrorMessage;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EditedValueUnchangedException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class responsible for handling category-related business logic.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private static final Logger logger = LogManager.getLogger(CategoryService.class);

  /**
   * Retrieves all categories from the database and maps them to CategoryResponse DTOs.
   *
   * @return a list of CategoryResponse objects representing all available categories.
   * @throws NoSuchElementException if no categories are found
   */
  public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(CategoryMapper::toDto)
        .toList();
  }

  /**
   * Retrieves a categories associated with a category ID from the database and maps it to CategoryResponse DTOs.
   *
   * @return a list of CategoryResponse objects representing all available categories.
   * @throws NoSuchElementException if no category is found
   */
  public Category getCategoryById(long categoryID) {
    return categoryRepository.findById(categoryID)
            .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));
  }

  public CategoryResponse getCategoryDtoById(long categoryID) {
    Category category = categoryRepository.findById(categoryID)
            .orElseThrow(() -> new EntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND));

    return CategoryMapper.toDto(category);
  }

  /**
   * Creates a new category in the database.
   *
   * @param req the CategoryRequest object containing the category name and attributes
   * @return a CategoryResponse object representing the newly created category
   */
  public CategoryResponse createCategory(CategoryRequest req) {
    if (categoryRepository.existsByCategoryName(req.getName())) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.CATEGORY_ALREADY_EXISTS);
    }

    Category category = CategoryMapper.toEntity(req);
    categoryRepository.save(category);
    return CategoryMapper.toDto(category);
  }

  public void editCategory(Long categoryId, CategoryRequest request) {
    Category category = getCategoryById(categoryId);

    if (categoryRepository.existsByCategoryName(request.getName())) {
      throw new EditedValueUnchangedException(CustomErrorMessage.CATEGORY_NAME_UNCHANGED);
    }
    category.setCategoryName(request.getName());
    categoryRepository.save(category);
  }

  public void deleteCategory(Long categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new EntityNotFoundException(CustomErrorMessage.CATEGORY_NOT_FOUND);
    }
    categoryRepository.deleteById(categoryId);
  }
}
