<<<<<<< HEAD:backend/transcend/src/main/java/code/infrastructure/security/BCryptHashingService.java
package code.infrastructure.security;

import code.modules.users.ports.out.HashingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BCryptHashingService implements HashingService {

  private final PasswordEncoder passwordEncoder;

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }
}
=======
package code.users.infrastructure.security;

import code.users.ports.out.HashingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class BCryptHashingService implements HashingService {

  private final PasswordEncoder passwordEncoder;

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }
}
>>>>>>> main:backend/transcend/src/main/java/code/users/infrastructure/security/BCryptHashingService.java
