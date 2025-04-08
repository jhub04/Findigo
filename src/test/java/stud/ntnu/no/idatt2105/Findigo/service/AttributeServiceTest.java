package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.entities.Attribute;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EditedValueUnchangedException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
public class AttributeServiceTest {
  @Autowired
  private AttributeService attributeService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private CategoryRepository categoryRepository;
  private long category1Id;
  private AttributeResponse attribute1;
  @BeforeEach
  public void setUp() {
    categoryRepository.deleteAll();
    CategoryRequest categoryRequest = new CategoryRequest("category1");
    category1Id = categoryService.createCategory(categoryRequest).getId();
    AttributeRequest attributeRequest = new AttributeRequest("attribute1","string", category1Id);
    attribute1 = attributeService.createAttribute(attributeRequest);
  }

  @Test
  public void testGetAllAttributes() {
    List<AttributeResponse> attributes = attributeService.getAllAttributes();
    assertEquals(1, attributes.size());
  }

  @Test
  public void testGetAttributeById() {
    Attribute attribute = attributeService.getAttributeById(attribute1.getId());
    assertEquals("attribute1", attribute.getAttributeName());

    assertThrows(AppEntityNotFoundException.class, () -> {
      attributeService.getAttributeById(99876549L);
    });
  }

  @Test
  public void testCreateAttribute() {
    AttributeRequest attributeRequest = new AttributeRequest("attribute2", "string", category1Id);
    AttributeResponse createdAttribute = attributeService.createAttribute(attributeRequest);

    assertEquals("attribute2", createdAttribute.getName());
    assertEquals("string", createdAttribute.getType());

    assertThrows(EntityAlreadyExistsException.class, () -> {
      attributeService.createAttribute(attributeRequest);
    });

    attributeService.deleteAttribute(createdAttribute.getId());
  }

  @Test
  public void testEditAttribute() {
    AttributeRequest attributeRequest = new AttributeRequest("attribute5", "string", category1Id);
    AttributeResponse attribute = attributeService.createAttribute(attributeRequest);

    AttributeRequest editedRequest = new AttributeRequest("attribute10", "string", category1Id);
    attributeService.editAttribute(attribute.getId(), editedRequest);

    Attribute updatedAttribute = attributeService.getAttributeById(attribute.getId());

    assertEquals("attribute10", updatedAttribute.getAttributeName());


    assertThrows(EditedValueUnchangedException.class, () -> {
      attributeService.editAttribute(attribute1.getId(), editedRequest);
    });
    attributeService.deleteAttribute(attribute.getId());
  }

  @Test
  public void testDeleteAttribute() {
    AttributeRequest attributeRequest = new AttributeRequest("attribute3", "string", category1Id);
    AttributeResponse createdAttribute = attributeService.createAttribute(attributeRequest);

    attributeService.deleteAttribute(createdAttribute.getId());

    assertThrows(AppEntityNotFoundException.class, () -> {
      attributeService.getAttributeById(createdAttribute.getId());
    });
  }

}
