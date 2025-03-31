package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryServiceTest {
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private CategoryRepository categoryRepository;
  @BeforeEach
  public void setUp() {
    categoryRepository.deleteAll();
    AttributeResponse attributeDefinitionDto = new AttributeResponse();
    attributeDefinitionDto.setName("TestAttribute1");
    attributeDefinitionDto.setType("String");
    CategoryRequest categoryRequest = new CategoryRequest("TestCategory", List.of(attributeDefinitionDto));

  }
}
