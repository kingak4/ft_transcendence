package code.users.infrastructure.persistence;

import code.users.domain.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(columnDefinition = "user_role", nullable = false)
  private Role role;
}
