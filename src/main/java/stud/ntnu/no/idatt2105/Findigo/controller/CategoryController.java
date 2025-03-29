package stud.ntnu.no.idatt2105.Findigo.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Listing;
import stud.ntnu.no.idatt2105.Findigo.service.CategoryService;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;

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
  private final ListingService listingService;

  /**
   * Retrieves all available categories.
   *
   * @return a ResponseEntity containing a list of {@link CategoryResponse} objects.
   */
  @GetMapping("")
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    logger.info("Fetching all categories");
    List<CategoryResponse> categories = categoryService.getAllCategories();
    logger.info("All categories fetched");
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/{categoryID}")
  public ResponseEntity<List<ListingResponse>> getAllListingsFromCategory(@PathVariable long categoryID) {
    logger.info("Fetching all listings from category with ID " + categoryID);
    List<ListingResponse> listings = listingService.getListingsInCategory(categoryID);
    logger.info("Fetched listings in category with id");
    return ResponseEntity.ok(listings);
  }
}
