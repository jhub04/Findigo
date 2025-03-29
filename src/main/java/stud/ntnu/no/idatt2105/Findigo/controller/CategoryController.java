package stud.ntnu.no.idatt2105.Findigo.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.service.CategoryService;

import java.util.List;

/**
 * Controller for handling category-related operations.
 * <p>
 * This controller provides an endpoint to retrieve all categories available in the system.
 * </p>
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
  //TODO swagger doc

  private static final Logger logger = LogManager.getLogger(CategoryController.class);
  private final CategoryService categoryService;

  /**
   * Retrieves all available categories.
   *
   * @return a ResponseEntity containing a list of {@link CategoryResponse} objects.
   */
  @GetMapping("")
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    logger.info("Fetching all categories");
    return ResponseEntity.ok(categoryService.getAllCategories());
  }
}
