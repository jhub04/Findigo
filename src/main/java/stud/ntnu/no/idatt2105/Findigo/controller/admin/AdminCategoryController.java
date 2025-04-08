package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.service.CategoryService;

import java.util.List;

/**
 * Admin controller for managing categories.
 *
 * <p>Provides endpoints for administrators to:
 * <ul>
 *     <li>Create new categories</li>
 *     <li>Retrieve all categories</li>
 *     <li>Update existing categories</li>
 *     <li>Delete categories</li>
 * </ul>
 * Access is restricted to users with the 'ADMIN' role.
 * </p>
 */
@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Categories", description = "Operations for managing categories by admin users")
public class AdminCategoryController {

  private static final Logger logger = LogManager.getLogger(AdminCategoryController.class);

  private final CategoryService categoryService;

  /**
   * Creates a new category.
   *
   * @param request the category data
   * @return the created category with HTTP 201 status
   */
  @Operation(summary = "Create a new category", description = "Creates a new category with the provided data. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Category successfully created"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<CategoryResponse> create(
          @Validated @RequestBody CategoryRequest request
  ) {
    logger.info("Admin: Creating new category with name '{}'", request.getName());
    CategoryResponse createdCategory = categoryService.createCategory(request);
    logger.info("Admin: Category created with ID {}", createdCategory.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
  }

  /**
   * Retrieves all categories.
   *
   * @return the list of all categories
   */
  @Operation(summary = "Get all categories", description = "Retrieves a list of all existing categories. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  public ResponseEntity<List<CategoryResponse>> findAll() {
    logger.info("Admin: Fetching all categories");
    List<CategoryResponse> categories = categoryService.getAllCategories();
    logger.info("Admin: Retrieved {} categories", categories.size());
    return ResponseEntity.ok(categories);
  }

  /**
   * Updates an existing category.
   *
   * @param categoryId the ID of the category to update
   * @param request    the updated category data
   * @return a confirmation message
   */
  @Operation(summary = "Update an existing category", description = "Updates an existing category by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Category successfully updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "404", description = "Category not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{categoryId}")
  public ResponseEntity<String> update(
          @Parameter(description = "ID of the category to update", example = "1")
          @PathVariable Long categoryId,
          @Validated @RequestBody CategoryRequest request
  ) {
    logger.info("Admin: Updating category with ID {}", categoryId);
    categoryService.editCategory(categoryId, request);
    logger.info("Admin: Category updated with ID {}", categoryId);
    return ResponseEntity.ok("Category successfully updated");
  }

  /**
   * Deletes an existing category.
   *
   * @param categoryId the ID of the category to delete
   * @return a confirmation message
   */
  @Operation(summary = "Delete a category", description = "Deletes a category by ID. Requires ADMIN role.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Category successfully deleted"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
          @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
          @ApiResponse(responseCode = "404", description = "Category not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<String> delete(
          @Parameter(description = "ID of the category to delete", example = "1")
          @PathVariable Long categoryId
  ) {
    logger.info("Admin: Deleting category with ID {}", categoryId);
    categoryService.deleteCategory(categoryId);
    logger.info("Admin: Category deleted with ID {}", categoryId);
    return ResponseEntity.ok("Category successfully deleted");
  }
}
