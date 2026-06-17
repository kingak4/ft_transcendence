package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avatars")
public class AvatarEntity {

  @EmbeddedId @EqualsAndHashCode.Include private AvatarIdEntity id;

  @Column(name = "content", nullable = false)
  private byte[] content;
}