package code.infrastructure.security;

import code.bootstrap.config.JwtProperties;
import code.modules.users.ports.out.AccessTokenIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    Claims claims = extractAllClaims(token);
    String username = claims.getSubject();
    boolean isExpired = claims.getExpiration().before(new Date());
    return username.equals(userDetails.getUsername()) && !isExpired;
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }
}
