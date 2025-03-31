package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.EditUserDto;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Role;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.UsernameAlreadyExistsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTests {

  @Autowired
  private UserService userService;
  @Autowired
  CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  public void setUp() {
    RegisterRequest registerRequest1 = new RegisterRequest();//Should get id 1
    registerRequest1.setUsername("existingUser");
    registerRequest1.setPassword("password123");


    //Register users into system
    try{
      userService.register(registerRequest1);
      System.out.println(userService.getUserByUsername("existingUser").getId());
    } catch(Exception ignored) {
      //the database already has this user in db
    }

  }

  @Test
  public void testRegisterUserWithExistingUsername() {
    // new user to register
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("existingUser");
    registerRequest.setPassword("password123");

    // When & Then
    assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(registerRequest));
  }

  @Test
  public void testAuthenticate_SuccessfulAuthentication() {
    //Mock auth request
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("existingUser");
    authRequest.setPassword("password123");

    // When
    AuthResponse authResponse = userService.authenticate(authRequest);

    // Then
    assertNotNull(authResponse);
  }

  @Test
  public void testAuthenticate_UnsuccessfulAuthentication() {
    //Mock auth request that should be unsuccessful
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("user");
    authRequest.setPassword("password123");


    assertThrows(AuthenticationException.class, ()->userService.authenticate(authRequest));
  }

  @Test
  public void testGetAllUsers() {
    List<User> allUsers = userService.getAllUsers();//Should only contain existingUser

    assertEquals("existingUser", allUsers.get(0).getUsername());

  }

  @Test
  public void testGetUserById() {
    User user = userService.getUserById(1L);
    System.out.println(user.getUsername());

    assertEquals(1L, user.getId());
  }

  @Test
  public void testGetUserByIdFail() {
    assertThrows(NoSuchElementException.class, () -> userService.getUserById(3L));
  }

  @Test
  public void testGetUserByUsername() {
    User user = userService.getUserByUsername("existingUser");//Should have id 1

    assertEquals(1L, user.getId());
  }

  @Test
  public void testGetUserByUsernameFail() {
    assertThrows(NoSuchElementException.class, () -> userService.getUserByUsername("NotAUser"));
  }

  @Test
  public void testGetCurrentUser() {
    UserDetails userDetails = userService.getUserByUsername("existingUser");
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserResponse user = userService.getCurrentUser();
    System.out.println(user.getUsername());
    assertEquals(1L, user.getId());
    assertEquals("existingUser", user.getUsername());
  }

  @Test
  public void testGetCurrentUserFail() {
    UserDetails userDetails = new User().setPassword("password").setUsername("NotRegisteredUser");
    List<GrantedAuthority> roles = new ArrayList<>();
    roles.add(new SimpleGrantedAuthority("ROLE_USER"));
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, roles);
    SecurityContextHolder.getContext().setAuthentication(authentication);


    assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser());
  }

  @Test
  public void testEditUserDetails() {
    RegisterRequest newUser = new RegisterRequest();
    newUser.setUsername("anotherUser");
    newUser.setPassword("password");
    try {
      userService.register(newUser);
    }catch (Exception ignored){}
    EditUserDto editedUser = new EditUserDto(2L, "anotherUserEdited", "newPassword");

    UserDetails userDetails = userService.getUserByUsername("anotherUser");
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    userService.editUserDetails(editedUser);

    User user = userService.getUserById(2L);

    assertEquals("anotherUserEdited", user.getUsername());
  }

  @Test
  public void testEditUserDetailsFail() {
    EditUserDto editedUser = new EditUserDto(3L, "existingUserEdited", "newPassword");

    RegisterRequest newUser = new RegisterRequest();
    newUser.setUsername("anotherUser");
    newUser.setPassword("password");
    try {
      userService.register(newUser);
    }catch (Exception ignored){}

    User anotherUser = userService.getUserById(2L);
    Authentication authentication = new UsernamePasswordAuthenticationToken(anotherUser, null, anotherUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertThrows(NoSuchElementException.class, () -> userService.editUserDetails(editedUser));

    editedUser.setId(1L);

    assertThrows(AccessDeniedException.class, () -> userService.editUserDetails(editedUser));
  }


}
