package stud.ntnu.no.idatt2105.Findigo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class for setting up authentication, authorization,
 * CORS, and other security-related settings in the application.
 *
 * <p>This class configures:
 * <ul>
 *     <li>Cross-Origin Resource Sharing (CORS)</li>
 *     <li>JWT authentication filter</li>
 *     <li>Session management (stateless)</li>
 *     <li>Password encoding</li>
 *     <li>Authentication and authorization rules</li>
 * </ul>
 * </p>
 */

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JWTAuthorizationFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  /**
   * Configures Cross-Origin Resource Sharing (CORS) settings.
   *
   * <p>Allows requests from the frontend running on:
   * <ul>
   *     <li><a href="http://localhost:5173/">http://localhost:5173/</a></li>
   *     <li><a href="http://localhost:5174/">http://localhost:5174/</a></li>
   * </ul>
   * </p>
   *
   * @return a {@link CorsConfigurationSource} with the defined CORS rules
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://idatt2105-09.idi.ntnu.no"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Cookie", "Content-Type"));
    configuration.setExposedHeaders(List.of("Set-Cookie"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /**
   * Configures the security filter chain.
   *
   * <p>This method:
   * <ul>
   *     <li>Disables CSRF protection (not needed for stateless JWT authentication)</li>
   *     <li>Applies CORS configuration</li>
   *     <li>Allows unauthenticated access to authentication and API documentation endpoints</li>
   *     <li>Requires authentication for all other endpoints</li>
   *     <li>Uses stateless session management</li>
   *     <li>Adds the JWT authentication filter before the default username-password filter</li>
   * </ul>
   * </p>
   *
   * @param http the {@link HttpSecurity} instance for configuring security settings
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/auth/**",
                            "/api/auth/logout",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html"
                    ).permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
  }

  /**
   * Configures the password encoder for hashing and verifying passwords.
   *
   * @return a {@link PasswordEncoder} using the BCrypt hashing algorithm
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures and provides an {@link AuthenticationManager}.
   *
   * <p>The authentication manager handles authentication requests using the provided
   * {@link UserDetailsService} and password encoder.</p>
   *
   * @return an {@link AuthenticationManager} instance
   */
  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(provider);
  }

  /**
   * Configures and provides an {@link AuthenticationProvider} for Spring Security.
   *
   * <p>The authentication provider verifies user credentials using the configured
   * {@link UserDetailsService} and password encoder.</p>
   *
   * @return an {@link AuthenticationProvider} instance
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }
}
