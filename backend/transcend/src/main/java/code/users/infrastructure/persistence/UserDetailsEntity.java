package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_details")
public class UserDetailsEntity {

  @EmbeddedId @EqualsAndHashCode.Include private UserIdEntity id;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "avatar_id")
  private UUID avatarId;
}
