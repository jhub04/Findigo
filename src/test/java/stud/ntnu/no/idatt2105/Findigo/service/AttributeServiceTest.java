package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.entities.*;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EditedValueUnchangedException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRolesRepository;

import java.util.List;
import java.util.Set;

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
  @Autowired
  UserRepository userRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private UserRolesRepository userRolesRepository;
  private User user;

  @BeforeEach
  public void setUp() {
    categoryRepository.deleteAll();
    userRolesRepository.deleteAll();
    userRepository.deleteAll();

    AuthRequest registerRequest = new AuthRequest().setUsername("user").setPassword("123");
    userService.register(registerRequest);
    user = userService.getUserByUsername("user");

    UserRoles userRole = new UserRoles();
    userRole.setUser(user);
    userRole.setRole(Role.ROLE_ADMIN);
    userRolesRepository.save(userRole);

    authenticateTestUser();

    CategoryRequest categoryRequest = new CategoryRequest("category1");
    category1Id = categoryService.createCategory(categoryRequest).getId();

    AttributeRequest attributeRequest = new AttributeRequest("attribute1", "string", category1Id);
    attribute1 = attributeService.createAttribute(attributeRequest);
  }

  private void authenticateTestUser() {
    SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            )
    );
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
