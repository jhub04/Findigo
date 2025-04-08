package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.service.CategoryService;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;

import java.util.List;

/**
 * Controller for handling category-related operations.
 *
 * <p>Provides endpoints to retrieve all categories and specific category details.</p>
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for retrieving categories and listings filtered by category")
public class CategoryController {

  private static final Logger logger = LogManager.getLogger(CategoryController.class);

  private final CategoryService categoryService;
  private final ListingService listingService;

  /**
   * Retrieves all available categories.
   *
   * @return a ResponseEntity containing a list of {@link CategoryResponse} objects.
   */
  @Operation(summary = "Get all categories", description = "Fetches all categories that exist in the database.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
          @ApiResponse(responseCode = "404", description = "No categories found in the database")
  })
  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    logger.info("Fetching all categories");
    List<CategoryResponse> categories = categoryService.getAllCategories();
    logger.info("Retrieved {} categories", categories.size());
    return ResponseEntity.ok(categories);
  }

  /**
   * Retrieves details of a specific category by its ID.
   *
   * @param categoryID the ID of the category to retrieve
   * @return a ResponseEntity containing the details of the requested category
   */
  @Operation(summary = "Get a specific category", description = "Retrieves the details of a category by its ID.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Category found"),
          @ApiResponse(responseCode = "404", description = "Category not found")
  })
  @GetMapping("/{categoryID}")
  public ResponseEntity<CategoryResponse> getCategoryById(
          @Parameter(description = "The ID of the category to retrieve", example = "1") @PathVariable long categoryID) {
    logger.info("Fetching category with ID {}", categoryID);
    CategoryResponse category = categoryService.getCategoryDtoById(categoryID);
    logger.info("Category found with ID {}", categoryID);
    return ResponseEntity.ok(category);
  }
}
