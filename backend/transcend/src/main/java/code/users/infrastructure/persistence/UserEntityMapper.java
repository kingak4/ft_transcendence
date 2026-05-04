package code.users.infrastructure.persistence;

import code.users.domain.model.User;
import code.users.domain.model.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
  @Mapping(source = "hash", target = "password")
  @Mapping(target = "details", ignore = true)
  User toDomain(UserEntity entity);

  @Mapping(source = "password", target = "hash")
  UserEntity toEntity(User user);

  default UserId map(UserIdEntity id) {
    return id == null ? null : UserId.of(id.val());
  }

  default UserIdEntity map(UserId id) {
    return id == null ? null : new UserIdEntity(id.getVal());
  }
}
