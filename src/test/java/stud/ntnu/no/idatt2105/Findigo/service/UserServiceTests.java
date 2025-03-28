package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthRequest;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.AuthResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;
import stud.ntnu.no.idatt2105.Findigo.exception.UsernameAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTests {

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setUp() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("existingUser");
    registerRequest.setPassword("password123");

    //Register user into system
    userService.register(registerRequest);


  }

  @Test
  public void testRegisterNewUserAndRegisterUserWithExistingUsername() {
    // new user to register
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("newUser");
    registerRequest.setPassword("password123");

    //Register user into system
    String response = userService.register(registerRequest);

    assertEquals("User registered successfully!", response);

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
    //Mock auth request
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("newUser");
    authRequest.setPassword("password123");


    assertThrows(AuthenticationException.class, ()->userService.authenticate(authRequest));
  }

}
