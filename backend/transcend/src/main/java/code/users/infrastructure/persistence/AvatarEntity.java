package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avatars")
public class AvatarEntity {

    @EmbeddedId @EqualsAndHashCode.Include private UserIdEntity id;

    @Column(name = "content", nullable = false)
    private byte[] content;

}
