package stud.ntnu.no.idatt2105.Findigo.controller;

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

  @PostMapping("/seed")
  public ResponseEntity<?> seedUser() {
    userService.seedTestUsers();
    return ResponseEntity.ok("Database seeded with test data.");
  }

  @PostMapping("/reset")
  public ResponseEntity<?> resetDatabase() {
    listingService.clearAll();
    categoryService.clearAll();
    userService.clearAll();
    return ResponseEntity.ok("Database reset to initial state.");
  }

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
