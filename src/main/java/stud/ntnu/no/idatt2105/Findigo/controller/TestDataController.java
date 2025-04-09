package stud.ntnu.no.idatt2105.Findigo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.service.CategoryService;
import stud.ntnu.no.idatt2105.Findigo.service.UserService;
import stud.ntnu.no.idatt2105.Findigo.service.ListingService;

@Profile("test")
@RestController
@RequestMapping("/api/test")
public class TestDataController {
  private final UserService userService;
  private final ListingService listingService;
  private final CategoryService categoryService;
  private static final Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(TestDataController.class);

  public TestDataController(UserService userService, ListingService listingService, CategoryService categoryService) {
    this.userService = userService;
    this.listingService = listingService;
    this.categoryService = categoryService;
  }


  /**
   * Set a user in the database with user permissions for e2e testing
   * @return A ResponseEntity with a success message
   */
  @Operation(summary = "Seed test users", description = "Adds test users to the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Test users seeded successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping("/seed")
  public ResponseEntity<?> seedUser() {
    userService.seedTestUsers();
    return ResponseEntity.ok("Database seeded with test data.");
  }

  /**
   * Clears the test database for e2e tests
   * @return A ResponseEntity with a success message
   */
  @Operation(summary = "Reset test database", description = "Clears all data from the test database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Test database reset successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping("/reset")
  public ResponseEntity<?> resetDatabase() {
    listingService.clearAll();
    categoryService.clearAll();
    userService.clearAll();
    return ResponseEntity.ok("Database reset to initial state.");
  }

  /**
   * Initializes the database for e2e testing. Adds a user, category and listing to test database.
   *
   * @return A ResponseEntity with a success message.
   */
  @Operation(summary = "Initialize test database", description = "Adds a user, category, and listing to the test database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Test database initialized successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping("/init")
  public ResponseEntity<?> initTestData() {
    userService.clearAll();
    userService.seedTestUsers();

    CategoryResponse cars = categoryService.createCategory(new CategoryRequest("Cars"));
    CategoryResponse house = categoryService.createCategory(new CategoryRequest("House"));

    listingService.seedTestListingsFor("testuser", cars.getId(), house.getId());

    return ResponseEntity.ok("Test database initialized with user, categories, and listings.");
  }
}
