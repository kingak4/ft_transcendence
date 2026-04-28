package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

  @EmbeddedId @EqualsAndHashCode.Include private UserIdEntity id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String hash;
}
