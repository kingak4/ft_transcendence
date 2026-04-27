package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

  @Id @GeneratedValue private UUID id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String hash;

  public UserEntity(String email, String hash) {
    this.email = email;
    this.hash = hash;
  }
}
