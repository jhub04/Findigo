package stud.ntnu.no.idatt2105.Findigo.config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.idatt2105.Findigo.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

@ActiveProfiles("test")
@SpringBootTest
public class JWTAuthorizationFilterTest {

  private JWTAuthorizationFilter jwtAuthorizationFilter;

  @Mock
  private JWTUtil jwtUtil;

  @Mock
  private CustomUserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;
  @Mock
  private SecurityContext securityContext;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    jwtAuthorizationFilter = new JWTAuthorizationFilter(jwtUtil, userDetailsService);
  }

  @Test
  public void testDoFilterInternal_NoToken() throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/api/listings");
    // Mock no cookies
    when(request.getCookies()).thenReturn(null);

    // Call the filter
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // Verify the filter chain is called without setting authentication
    verify(filterChain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
    SecurityContextHolder.setContext(securityContext);
    when(request.getRequestURI()).thenReturn("/api/listings");
    String validToken = "valid-token";
    String username = "user1";

    // Mock valid token in cookies
    Cookie tokenCookie = new Cookie("auth-token", validToken);
    when(request.getCookies()).thenReturn(new Cookie[]{tokenCookie});
    when(jwtUtil.extractUsername(validToken)).thenReturn(username);

    // Mock user details service
    User mockUser = new User(username, "password", new ArrayList<>());
    when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUser);
    when(jwtUtil.isTokenValid(validToken)).thenReturn(true);

    // Call the filter
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // Verify authentication is set in security context
    ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    verify(filterChain, times(1)).doFilter(request, response);
    verify(securityContext).setAuthentication(captor.capture());

    UsernamePasswordAuthenticationToken authToken = captor.getValue();
    assertNotNull(authToken);
    assertEquals(username, authToken.getName());
  }

  @Test
  public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/api/listings");
    String invalidToken = "invalid-token";

    // Mock invalid token in cookies
    Cookie tokenCookie = new Cookie("auth-token", invalidToken);
    when(request.getCookies()).thenReturn(new Cookie[]{tokenCookie});
    when(jwtUtil.extractUsername(invalidToken)).thenReturn("user1");
    when(jwtUtil.isTokenValid(invalidToken)).thenReturn(false);

    // Call the filter
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // Verify the filter chain is called without setting authentication
    verify(filterChain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternal_SkipAuthEndpoint() throws ServletException, IOException {
    // Mock request URI to be an authentication endpoint
    when(request.getRequestURI()).thenReturn("/api/auth/login");

    // Call the filter
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // Verify the filter chain is called without setting authentication
    verify(filterChain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
