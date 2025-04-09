package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.config.JWTUtil;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.AttributeResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.attribute.ListingAttributeRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.listing.ListingResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.AdminUserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.MyUserRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserLiteResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.entities.UserRoles;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
  @Autowired
  private SaleRepository saleRepository;
  @Autowired
  private AttributeService attributeService;
  @Autowired
  private ListingService listingService;
  @Autowired
  private ListingRepository listingRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  UserRolesRepository userRolesRepository;
  private User user1;
  private User user2;
  private User user3;
  private User user4;
  private long category1Id;
  private ListingResponse listing;
  @Autowired
  private CategoryRepository categoryRepository;
  ListingAttributeRequest listingAttributeRequest;
  ListingResponse listing2;

  @BeforeEach
  public void setUp() {
    saleRepository.deleteAll();
    listingRepository.deleteAll();
    userRepository.deleteAll();
    categoryRepository.deleteAll();

    // Registrer user1 og gi admin-rolle
    AuthRequest registerRequest1 = new AuthRequest();
    registerRequest1.setUsername("existingUser");
    registerRequest1.setPassword("password123");
    userService.register(registerRequest1);
    user1 = userService.getUserByUsername("existingUser");
    UserRoles user1Role = new UserRoles();
    user1Role.setUser(user1);
    user1Role.setRole(Role.ROLE_ADMIN);
    userRolesRepository.save(user1Role);

    // Registrer user2, user3, user4
    AuthRequest registerRequest2 = new AuthRequest().setUsername("user2").setPassword("password123");
    userService.register(registerRequest2);
    user2 = userService.getUserByUsername("user2");

    AuthRequest registerRequest3 = new AuthRequest().setUsername("user3").setPassword("password123");
    userService.register(registerRequest3);
    user3 = userService.getUserByUsername("user3");

    AuthRequest registerRequest4 = new AuthRequest().setUsername("user4").setPassword("password123");
    userService.register(registerRequest4);
    user4 = userService.getUserByUsername("user4");

    // Sett user1 (admin) i SecurityContext før admin-operasjoner
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    // Opprett kategori og attributter
    CategoryRequest categoryRequest = new CategoryRequest("category1");
    category1Id = categoryService.createCategory(categoryRequest).getId();

    AttributeRequest attributeRequest = new AttributeRequest("att1", "string", category1Id);
    AttributeRequest attributeRequest2 = new AttributeRequest("att2", "string", category1Id);

    AttributeResponse attributeResponse1 = attributeService.createAttribute(attributeRequest);
    AttributeResponse attributeResponse2 = attributeService.createAttribute(attributeRequest2);

    listingAttributeRequest = new ListingAttributeRequest()
            .setAttributeId(attributeResponse1.getId())
            .setValue("value1");
    ListingAttributeRequest listingAttributeRequest2 = new ListingAttributeRequest()
            .setAttributeId(attributeResponse2.getId())
            .setValue("value2");

    // Bytt til user2 og opprett listing
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));

    ListingRequest listingRequest = new ListingRequest()
            .setAddress("Test Address")
            .setBriefDescription("Test Description")
            .setFullDescription("Test Full Description")
            .setLatitude(63.4305)
            .setLongitude(10.3951)
            .setPrice(1500.00)
            .setCategoryId(category1Id)
            .setPostalCode("3012")
            .setAttributes(List.of(listingAttributeRequest, listingAttributeRequest2));
    listing = listingService.addListing(listingRequest); // Will be made by user 2

    // Bytt til user1 (admin) og opprett listing
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));

    ListingRequest listingRequest2 = new ListingRequest()
            .setAddress("Test Address")
            .setBriefDescription("Test Description")
            .setFullDescription("Test Full Description")
            .setLatitude(63.4305)
            .setLongitude(10.3951)
            .setPrice(1500.00)
            .setCategoryId(category1Id)
            .setPostalCode("3012")
            .setAttributes(List.of(listingAttributeRequest, listingAttributeRequest2));
    listing2 = listingService.addListing(listingRequest2); // Will be made by user 1
  }


  @Test
  public void testGetUserByUsername() {
    User foundUser = userService.getUserByUsername("existingUser");
    assertEquals(user1.getId(), foundUser.getId());
  }

  @Test
  public void testGetUserById() {
    User foundUser = userService.getUserById(user1.getId());
    assertEquals(user1.getId(), foundUser.getId());
  }

  @Test
  public void testGetUserByIdNotFound() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      userService.getUserById(999L);
    });
  }

  @Test
  public void testGetUserByUsernameNotFound() {
    assertThrows(AppEntityNotFoundException.class, () -> {
      userService.getUserByUsername("nonExistingUser");
    });
  }

  @Test
  public void testRegisterUser() {
    AuthRequest registerRequest = new AuthRequest();
    registerRequest.setUsername("newUser");
    registerRequest.setPassword("password123");
    userService.register(registerRequest);

    User foundUser = userService.getUserByUsername("newUser");
    assertEquals("newUser", foundUser.getUsername());
    userRepository.deleteById(foundUser.getId());
  }

  @Test
  public void testRegisterWithAlreadyExistingUsername() {
    AuthRequest registerRequest = new AuthRequest();
    registerRequest.setUsername("existingUser");
    registerRequest.setPassword("password123");

    assertThrows(EntityAlreadyExistsException.class, () -> {
      userService.register(registerRequest);
    });
  }

  @Test
  public void testAuthenticateUser() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("existingUser");
    authRequest.setPassword("password123");

    AuthResponse authenticatedUser = userService.authenticate(authRequest);
    assertNotNull(authenticatedUser);
    assertFalse(authenticatedUser.getToken().isEmpty());
  }

  @Test
  public void testAuthenticateWithInvalidCredentials() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("existingUser");
    authRequest.setPassword("wrongPassword");

    assertThrows(AuthenticationException.class, () -> {
      userService.authenticate(authRequest);
    });
  }

  @Test
  public void testGetAllUsers() {
    assertEquals(4, userService.getAllUsers().size());
  }

  @Test
  public void testGetUserDtoById() {
    UserResponse userResponse = userService.getUserDtoById(user1.getId());
    assertEquals(user1.getId(), userResponse.getId());
  }

  @Test
  public void testEditMyUserDetails() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user4, null, user4.getAuthorities()));
    MyUserRequest myUserRequest = new MyUserRequest().setPhoneNumber("12345678").setUsername("newUsername").setPassword("newPassword");
    userService.editMyUserDetails(myUserRequest);
    user4 = userService.getUserById(user4.getId()); //Refresh the user object to get the updated values
    //User is now edited, time to check that the edit went thorugh
    assertEquals("12345678", user4.getPhoneNumber());
    assertEquals("newUsername", user4.getUsername());
    assertFalse(userService.authenticate(new AuthRequest()
        .setUsername("newUsername")
        .setPassword("newPassword")).getToken().isEmpty()); //Check that the new password works
  }

  @Test
  public void testEditMyUserDetailsAlreadyExistingUsername() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user4, null, user4.getAuthorities()));
    MyUserRequest myUserRequest = new MyUserRequest().setPhoneNumber("12345678").setUsername("existingUser").setPassword("newPassword");

    assertThrows(EntityAlreadyExistsException.class, () -> {
      userService.editMyUserDetails(myUserRequest);
    });
  }

  @Test
  public void testEditMyUserDetailsSameUsername() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user3, null, user3.getAuthorities()));
    MyUserRequest myUserRequest = new MyUserRequest().setPhoneNumber("12345678").setUsername("user3").setPassword("newPassword");
    userService.editMyUserDetails(myUserRequest);
    user3 = userService.getUserById(user3.getId()); //Refresh the user object to get the updated values
    //User is now edited, time to check that the edit went thorugh
    assertEquals("12345678", user3.getPhoneNumber());
    assertEquals("user3", user3.getUsername());
  }
  @Test
  public void testEditMyUserDetailsInvalidPassword() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user4, null, user4.getAuthorities()));
    MyUserRequest myUserRequest = new MyUserRequest().setPhoneNumber("12345678").setUsername("newUsername").setPassword("");

    assertThrows(IllegalArgumentException.class, () -> {
      userService.editMyUserDetails(myUserRequest);
    });
    MyUserRequest myUserRequest2 = new MyUserRequest().setPhoneNumber("12345678").setUsername("newUsername").setPassword(null);

    assertThrows(IllegalArgumentException.class, () -> {
      userService.editMyUserDetails(myUserRequest2);
    });
  }

  @Test
  public void testGetMyActiveListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));

    assertEquals(1, userService.getMyActiveListings().size());
  }

  @Test
  public void testGetMyActiveListingsNoListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user3, null, user3.getAuthorities()));

    assertEquals(0, userService.getMyActiveListings().size());
  }

  @Test
  public void testGetMySoldListingNoListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user3, null, user3.getAuthorities()));

    assertEquals(0, userService.getMySoldListings().size());
  }

  @Test
  public void testGetMyArchivedListingNoListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user3, null, user3.getAuthorities()));

    assertEquals(0, userService.getMyArchivedListings().size());
  }

  @Test
  public void testGetMyArchivedListingsAndSoldListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    listingService.markListingAsArchived(listing2.getId());
    assertEquals(1, userService.getMyArchivedListings().size());
    listingService.markListingAsActive(listing2.getId());
    listingService.markListingAsSold(listing2.getId());
    assertEquals(1, userService.getMySoldListings().size());
  }
  @Test
  public void testGetMyFavoritesAndDeleteFavorite() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user2, null, user2.getAuthorities()));
    userService.addFavorite(listing.getId());
    assertEquals(1, userService.getFavorites().size());
    userService.deleteFavorite(listing.getId());
    assertEquals(0, userService.getFavorites().size());
  }

  @Test
  public void testGetUserListings() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    assertEquals(1, userService.getUserListings(user1.getId()).size());
  }

  @Test
  public void testCreateUser() {
    AdminUserRequest registerRequest = new AdminUserRequest();
    registerRequest.setUsername("anotherNewUser");
    registerRequest.setPassword("password123");
    registerRequest.setPhoneNumber("12345678");
    registerRequest.setRoles(Set.of(Role.ROLE_USER));
    userService.createUser(registerRequest);

    User foundUser = userService.getUserByUsername("anotherNewUser");
    assertEquals("anotherNewUser", foundUser.getUsername());

    assertThrows(EntityAlreadyExistsException.class, () -> {
      userService.createUser(registerRequest);
    });
    userRepository.deleteById(foundUser.getId());
  }

  @Test
  public void testGetCurrentUser() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities()));
    User foundUser = userService.getCurrentUser();
    assertEquals(user1.getId(), foundUser.getId());
    UserResponse userResponse = userService.getCurrentDtoUser();
    assertEquals(user1.getId(), userResponse.getId());
  }

  @Test
  public void testAuthenticateAndGetCookie() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("existingUser");
    authRequest.setPassword("password123");

    ResponseCookie cookie = userService.authenticateAndGetCookie(authRequest);
    assertNotNull(cookie);
    assertEquals("auth-token", cookie.getName());
    assertEquals("/", cookie.getPath());
    assertFalse(cookie.getValue().isEmpty());
    assertTrue(userService.validateToken(cookie.getValue()));
    assertThrows(AuthenticationException.class, () -> {
      userService.authenticateAndGetCookie(new AuthRequest().setUsername("nonExistingUser").setPassword("wrongPassword"));
    });
  }

  @Test
  public void testCreateLogoutCookie() {
    ResponseCookie cookie = userService.createLogoutCookie();
    assertNotNull(cookie);
    assertEquals("auth-token", cookie.getName());
    assertEquals("/", cookie.getPath());
    assertEquals("", cookie.getValue());
  }



}
