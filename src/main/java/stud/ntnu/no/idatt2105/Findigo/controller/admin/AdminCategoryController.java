package stud.ntnu.no.idatt2105.Findigo.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.service.CategoryService;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class AdminCategoryController {

  private final CategoryService categoryService;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<CategoryResponse> createCategory(
          @RequestBody CategoryRequest request
  ) {
    CategoryResponse createdCategory = categoryService.createCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
  }

}
