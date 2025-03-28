package stud.ntnu.no.idatt2105.Findigo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.dtos.auth.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class CustomUserDetailsServiceTest {

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private UserService userService;

  @Test
  public void testLoadUserByUsername() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("existingUser");
    registerRequest.setPassword("password123");

    assertThrows(UsernameNotFoundException.class, ()-> customUserDetailsService.loadUserByUsername("existingUser"));
    //Register user into system
    userService.register(registerRequest);

    assertNotNull(customUserDetailsService.loadUserByUsername("existingUser"));

  }
}
