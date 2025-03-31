package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.CategoryAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryServiceTest {
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private CategoryRepository categoryRepository;

  private long category1Id;
  @BeforeEach
  public void setUp() {
    categoryRepository.deleteAll();
    AttributeRequest attributeRequest = new AttributeRequest("att1", "string");
    AttributeRequest attributeRequest2 = new AttributeRequest("att2", "string");
    CategoryRequest categoryRequest = new CategoryRequest("category1", List.of(attributeRequest, attributeRequest2));
    category1Id = categoryService.createCategory(categoryRequest).getId();
  }

  @Test
  public void testCreateCategoryAndGetAllCategories() {
    AttributeRequest attributeRequest = new AttributeRequest("att3", "string");
    CategoryRequest categoryRequest = new CategoryRequest("category2", List.of(attributeRequest));
    categoryService.createCategory(categoryRequest);
    assertTrue(categoryRepository.findByCategoryName("category2").isPresent());
    List<CategoryResponse> categories = categoryService.getAllCategories();
    assertEquals(2, categories.size());
  }

  @Test
  public void testCreateCategoryFail() {
    AttributeRequest attributeRequest = new AttributeRequest("att1", "string");
    CategoryRequest categoryRequest = new CategoryRequest("category1", List.of(attributeRequest));
    assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(categoryRequest));
  }

  @Test
  public void testGetCategory() {
    CategoryResponse category = categoryService.getCategory(category1Id);
    assertEquals("category1", category.getName());
    assertEquals(2, category.getAttributes().size());
  }

  @Test
  public void testGetCategoryFail() {
    assertThrows(NoSuchElementException.class, () -> categoryService.getCategory(10098765672L));
  }
}
