package stud.ntnu.no.idatt2105.Findigo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import stud.ntnu.no.idatt2105.Findigo.service.CustomUserDetailsService;

import java.io.IOException;

/**
 * Filter responsible for handling JWT authorization.
 *
 * <p>This filter intercepts incoming HTTP requests, extracts and validates the JWT token
 * from the Authorization header, and sets the security context if the token is valid.</p>
 *
 * <p>It extends {@link OncePerRequestFilter} to ensure execution only once per request.</p>
 */
@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
  private final JWTUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  /**
   * Performs JWT authentication filtering for each request.
   *
   * <p>Extracts the JWT token from the {@code Authorization} header, validates it,
   * and sets the security context with the authenticated user if the token is valid.</p>
   *
   * @param request  the HTTP request
   * @param response the HTTP response
   * @param chain    the filter chain
   * @throws ServletException if an error occurs during filtering
   * @throws IOException      if an input or output error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    Cookie[] cookies = request.getCookies();
    String token = null;
    logger.info("JWTAuthorizationFilter: doFilterInternal called");
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("auth-token".equals(cookie.getName())) { // This is the name of your JWT cookie
          token = cookie.getValue();
          break;
        }
      }
    }

    // If there is no token in the cookie, proceed with the filter chain
    if (token == null) {
      chain.doFilter(request, response);
      return;
    }

    String username = jwtUtil.extractUsername(token);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtUtil.isTokenValid(token)) {

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    chain.doFilter(request, response);
  }
}
