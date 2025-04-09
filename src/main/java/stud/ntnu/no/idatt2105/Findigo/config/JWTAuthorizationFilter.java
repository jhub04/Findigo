package stud.ntnu.no.idatt2105.Findigo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
 * <p>
 * This filter intercepts each incoming HTTP request, checks for a JWT token in cookies,
 * validates the token, and if valid, sets the authentication in the security context.
 * <p>
 * It extends {@link OncePerRequestFilter} to ensure it is executed once per request.
 */
@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  /**
   * Filters incoming HTTP requests for JWT authentication.
   * <p>
   * If the request is targeting an authentication endpoint, it bypasses the filter.
   * Otherwise, it looks for the JWT token in cookies, validates it, and sets the security context.
   *
   * @param request  the HTTP servlet request
   * @param response the HTTP servlet response
   * @param chain    the filter chain
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
          throws ServletException, IOException {



    // Skip filtering for authentication endpoints
    if (request.getRequestURI().startsWith("/api/auth/")) {
      logger.debug("Skipping JWT filter for authentication endpoint: " + request.getRequestURI());
      chain.doFilter(request, response);
      return;
    }

    logger.info("JWTAuthorizationFilter: Processing request: " + request.getRequestURI());

    String token = extractTokenFromCookies(request.getCookies());

    // If no token is found, proceed without authentication
    if (token == null) {
      chain.doFilter(request, response);
      return;
    }

    String username = jwtUtil.extractUsername(token);

    logger.info("Token username: " + username);
    logger.info("User authorities: " + userDetailsService.loadUserByUsername(username).getAuthorities());


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

  /**
   * Extracts the JWT token from the request cookies.
   *
   * @param cookies an array of cookies from the HTTP request
   * @return the JWT token if found; otherwise, {@code null}
   */
  private String extractTokenFromCookies(Cookie[] cookies) {
    if (cookies == null) {
      return null;
    }

    for (Cookie cookie : cookies) {
      if ("auth-token".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
