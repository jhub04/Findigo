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

    //Register user into syste
    try{
      userService.register(registerRequest);
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
    //Mock auth request
    AuthRequest authRequest = new AuthRequest();
    authRequest.setUsername("user");
    authRequest.setPassword("password123");


    assertThrows(AuthenticationException.class, ()->userService.authenticate(authRequest));
  }

}
