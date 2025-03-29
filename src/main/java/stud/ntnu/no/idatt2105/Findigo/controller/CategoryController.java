package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryNotFoundException;
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

  private static final Logger logger = LogManager.getLogger(CategoryController.class);
  private final CategoryService categoryService;
  private final ListingService listingService;

  /**
   * Retrieves all available categories.
   *
   * @return a ResponseEntity containing a list of {@link CategoryResponse} objects.
   */
  @Operation(summary = "Get all categories", description = "Fetches all categories that exist in the database")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Categories fetched"),
      @ApiResponse(responseCode = "404", description = "No categories found in the database")
  })
  @GetMapping("")
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    logger.info("Fetching all categories");
    List<CategoryResponse> categories = categoryService.getAllCategories();
    logger.info("All categories fetched");
    return ResponseEntity.ok(categories);
  }

  /**
   * Retrieves all listings associated with a specific category.
   *
   * @param categoryID the ID of the category whose listings are to be retrieved
   * @return a ResponseEntity containing a list of listing responses within the specified category
   */
  @Operation(summary = "Gets all listings form a category", description = "Gets all listings that are associated with the given categoryID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listings with the given categoryID are found"),
      @ApiResponse(responseCode = "404", description = "No listings associated with the given categoryID are found")
  })
  @GetMapping("/{categoryID}/listings")
  public ResponseEntity<List<ListingResponse>> getAllListingsFromCategory(@PathVariable long categoryID) {
    logger.info("Fetching all listings from category with ID " + categoryID);
    List<ListingResponse> listings = listingService.getListingsInCategory(categoryID);
    logger.info("Fetched listings in category with ID " + categoryID);
    return ResponseEntity.ok(listings);
  }

  /**
   * Retrieves details of a specific category.
   *
   * @param categoryID the ID of the category to retrieve
   * @return a ResponseEntity containing the details of the requested category
   */
  @Operation(summary = "Get a specific category", description = "Get the details of a category associated with a given categoryID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category with the given ID was found"),
      @ApiResponse(responseCode = "404", description = "Category with the given ID was not found")
  })
  @GetMapping("/{categoryID}")
  public ResponseEntity<CategoryResponse> getCategory(@PathVariable long categoryID) {
    logger.info("Fetching category with ID " + categoryID);
    CategoryResponse category = categoryService.getCategory(categoryID);
    logger.info("Category found with ID " + categoryID);
    return ResponseEntity.ok(category);
  }
}
