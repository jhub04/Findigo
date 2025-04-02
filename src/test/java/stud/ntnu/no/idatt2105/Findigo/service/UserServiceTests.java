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
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.user.UserResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.User;
import stud.ntnu.no.idatt2105.Findigo.exception.customExceptions.UsernameAlreadyExistsException;
import stud.ntnu.no.idatt2105.Findigo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTests {
  @Autowired
  UserRepository userRepository;
  @Autowired
  private UserService userService;
  @Autowired
  CustomUserDetailsService customUserDetailsService;
  private long existingUserId;
  private long anotherUserId;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
    RegisterRequest registerRequest1 = new RegisterRequest();//Should get id 1
    registerRequest1.setUsername("existingUser");
    registerRequest1.setPassword("password123");


    //Register users into system
    try{
      userService.register(registerRequest1);
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
    List<UserResponse> allUsers = userService.getAllUsers();//Should only contain existingUser

    assertEquals("existingUser", allUsers.getFirst().getUsername());
  }

  @Test
  public void testGetUserById() {
    User user = userService.getUserById(existingUserId);

    assertEquals(existingUserId, user.getId());
  }

  @Test
  public void testGetUserByIdFail() {
    assertThrows(NoSuchElementException.class, () -> userService.getUserById(9999999L));
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

}
