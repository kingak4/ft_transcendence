package code.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "user_details")
public class UserDetailsEntity implements Persistable<UserIdEntity> {

  @EmbeddedId @EqualsAndHashCode.Include private UserIdEntity id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "val")
  private UserEntity user;

  @Column(name = "display_name")
  private String displayName;

  @Embedded
  @AttributeOverride(name = "val", column = @Column(name = "avatar_id"))
  private AvatarIdEntity avatarId;

  public UserDetailsEntity(UserEntity user, String displayName, AvatarIdEntity avatarId) {
    this.user = user;
    this.id = user.getId();
    this.displayName = displayName;
    this.avatarId = avatarId;
  }

  @Transient private boolean isNew = true;

  @Override
  public UserIdEntity getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }

  @PostPersist
  @PostLoad
  void markNotNew() {
    this.isNew = false;
  }
}
