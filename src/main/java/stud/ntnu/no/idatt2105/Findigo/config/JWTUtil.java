package stud.ntnu.no.idatt2105.Findigo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for handling JWT token generation, validation, and extraction.
 *
 * <p>This class provides methods to create JWT tokens, extract claims from tokens,
 * and validate their authenticity. It is used in authentication and authorization processes.</p>
 */
@Service
public class JWTUtil {
  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.access-token-expiration}")
  private long accessTokenExpiration;

  /**
   * Generates a JWT token for the given user.
   *
   * @param userDetails the user details containing the username
   * @return a signed JWT token
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(userDetails, accessTokenExpiration);
  }

  /**
   * Generates a JWT token with a specific expiration time.
   *
   * @param userDetails the user details containing the username
   * @param expirationTime the expiration time in milliseconds
   * @return a signed JWT token
   */
  private String generateToken(UserDetails userDetails, long expirationTime) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts the username from the given JWT token.
   *
   * @param token the JWT token
   * @return the username contained in the token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Retrieves the signing key used to sign and verify JWT tokens.
   *
   * @return the signing key
   */
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  /**
   * Extracts a specific claim from the given JWT token.
   *
   * @param token the JWT token
   * @param claimsResolver a function to extract a claim from the token
   * @param <T> the type of the claim
   * @return the extracted claim value
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claimsResolver.apply(claims);
  }

  /**
   * Validates whether the given JWT token is still valid.
   *
   * <p>A token is considered valid if it has not expired and can be successfully parsed.</p>
   *
   * @param token the JWT token
   * @return {@code true} if the token is valid, {@code false} otherwise
   */
  public boolean isTokenValid(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      return !claims.getExpiration().before(new Date());
    } catch (JwtException e) {
      return false;
    }
  }
}
