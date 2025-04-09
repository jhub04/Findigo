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
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.entities.UserRoles;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EditedValueUnchangedException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRolesRepository;

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
  @Autowired
  UserRepository userRepository;
  private long category1Id;
  @Autowired
  private UserService userService;

  @Autowired
  private UserRolesRepository userRolesRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    userRolesRepository.deleteAll();
    categoryRepository.deleteAll();
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
  public void testCreateCategoryAndGetAllCategories() {
    CategoryRequest categoryRequest = new CategoryRequest("category2");
    categoryService.createCategory(categoryRequest);
    assertTrue(categoryRepository.findByCategoryName("category2").isPresent());
    List<CategoryResponse> categories = categoryService.getAllCategories();
    assertEquals(2, categories.size());
  }

  @Test
  public void testCreateCategoryFail() {
    CategoryRequest categoryRequest = new CategoryRequest("category1");
    assertThrows(EntityAlreadyExistsException.class, () -> categoryService.createCategory(categoryRequest));
  }

  @Test
  public void testGetCategory() {
    Category category = categoryService.getCategoryById(category1Id);
    assertEquals("category1", category.getCategoryName());
  }

  @Test
  public void testGetCategoryFail() {
    assertThrows(AppEntityNotFoundException.class, () -> categoryService.getCategoryById(10098765672L));
  }

  @Test
  public void testGetCategoryDtoById() {
    CategoryResponse categoryResponse = categoryService.getCategoryDtoById(category1Id);
    assertEquals("category1", categoryResponse.getName());
    assertThrows(AppEntityNotFoundException.class, () -> categoryService.getCategoryDtoById(10098765672L));
  }

  @Test
  public void testEditCategory() {
    CategoryRequest updatedCategoryRequest = new CategoryRequest("category2");
    categoryService.editCategory(category1Id, updatedCategoryRequest);
    assertEquals("category2", categoryService.getCategoryById(category1Id).getCategoryName());
    assertThrows(EditedValueUnchangedException.class, () -> categoryService.editCategory(category1Id, new CategoryRequest("category2")));
    updatedCategoryRequest = new CategoryRequest("category1");
    categoryService.editCategory(category1Id, updatedCategoryRequest);
  }

  @Test
  public void testDeleteCategory() {
    CategoryRequest categoryRequest = new CategoryRequest("category2");
    long category2Id = categoryService.createCategory(categoryRequest).getId();
    assertTrue(categoryRepository.findByCategoryName("category2").isPresent());
    categoryService.deleteCategory(category2Id);
    assertThrows(AppEntityNotFoundException.class, () -> categoryService.getCategoryById(category2Id));
    assertThrows(AppEntityNotFoundException.class, () -> categoryService.deleteCategory(123456789L));
  }
}