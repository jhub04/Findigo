package stud.ntnu.no.idatt2105.Findigo.config;

import org.springframework.stereotype.Service;

import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtil {
  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.access-token-expiration}")
  private long accessTokenExpiration;

  public String generateToken(UserDetails userDetails) {
    return generateToken(userDetails, accessTokenExpiration);
  }

  private String generateToken(UserDetails userDetails, long expirationTime) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claimsResolver.apply(claims);
  }

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
