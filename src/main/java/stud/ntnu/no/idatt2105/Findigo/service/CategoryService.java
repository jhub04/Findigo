package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.CategoryMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    List<Category> categories = categoryRepository.findAll();
    if (categories.isEmpty()) {
      throw new NoSuchElementException("No categories found in database");
    }
    return categories.stream()
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
            .orElseThrow(() -> new NoSuchElementException("Couldn't find category with ID " + categoryID));
  }

  public CategoryResponse getCategoryDtoById(long categoryID) {
    Category category = categoryRepository.findById(categoryID)
            .orElseThrow(() -> new NoSuchElementException("Couldn't find category with ID " + categoryID));
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
      throw new CategoryAlreadyExistsException("Category with name " + req.getName() + " already exists");
    }

    Category category = CategoryMapper.toEntity(req);
    categoryRepository.save(category);
    return CategoryMapper.toDto(category);
  }
}
