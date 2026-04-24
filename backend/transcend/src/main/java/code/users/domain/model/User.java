package code.users.domain.model;

import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record User(UUID id, String email, String password, UserDetails details) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
