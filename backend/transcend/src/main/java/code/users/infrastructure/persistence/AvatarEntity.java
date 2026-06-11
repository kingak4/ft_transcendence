package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avatars")
public class AvatarEntity {

  @Id
  @EqualsAndHashCode.Include
  @Column(name = "val")
  private UUID val;

  @Column(name = "content", nullable = false)
  private byte[] content;
}