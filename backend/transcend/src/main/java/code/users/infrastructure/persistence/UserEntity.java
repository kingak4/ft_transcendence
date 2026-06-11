package code.users.infrastructure.persistence;

import code.users.domain.model.Role;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
  @Column(nullable = false)
  private Role role;

  @Column(name = "user_details_id")
  private UUID userDetailsId;

  //  private byte[] avatar;

  @ElementCollection
  @CollectionTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "friend_id", nullable = false)
  private Set<UUID> friends = new HashSet<>();
}
