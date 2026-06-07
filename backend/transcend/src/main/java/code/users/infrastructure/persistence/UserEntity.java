package code.users.infrastructure.persistence;

import code.users.domain.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

  @EmbeddedId
  @AttributeOverride(name = "val", column = @Column(name = "id"))
  @EqualsAndHashCode.Include
  private UserIdEntity id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String hash;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false)
  private Role role;

  private String displayName;

  @Lob
  private byte[] avatar;

  @ElementCollection
  @CollectionTable(
          name = "user_friends",
          joinColumns = @JoinColumn(name = "val")
  )
  @AttributeOverride(name = "val", column = @Column(name = "friend_id"))
  private Set<UserIdEntity> friends = new HashSet<>();
}