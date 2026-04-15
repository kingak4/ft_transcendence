package code.infrastructure.security;

import code.bootstrap.config.JwtProperties;
import code.modules.users.ports.out.AccessTokenIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService implements AccessTokenIssuer {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtTokenService(JwtProperties jwtProperties) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    this.expirationMs = jwtProperties.getExpirationMs();
  }

  public String generateToken(String username) {
    Instant now = Instant.now();

    return Jwts.builder()
      .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(expirationMs)))
        .signWith(signingKey)
        .compact();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}