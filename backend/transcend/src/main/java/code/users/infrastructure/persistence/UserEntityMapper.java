package code.users.infrastructure.persistence;

import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
  @Mapping(source = "hash", target = "password")
  @Mapping(target = "details", ignore = true)
  @Mapping(target = "sessions", ignore = true)
  @Mapping(target = "friends", ignore = true)
  User toDomain(UserEntity entity);

  @Mapping(source = "password", target = "hash")
  @Mapping(target = "userDetailsId", ignore = true)
  @Mapping(target = "friends", ignore = true)
  UserEntity toEntity(User user);

  default AvatarId map(UUID value) {
    if (value == null) return null;
    return AvatarId.of(value);
  }

  default UUID map(AvatarId value) {
    if (value == null) return null;
    return value.val();
  }

  UserDetails toDomain(UserDetailsEntity entity);

  default UserId map(UserIdEntity id) {
    return id == null ? null : UserId.of(id.val());
  }

  default UserIdEntity map(UserId id) {
    return id == null ? null : new UserIdEntity(id.val());
  }

  default FriendId mapToFriendId(UserIdEntity id) {
    return id == null ? null : FriendId.of(id.val());
  }

  default UserIdEntity mapFromFriendId(FriendId id) {
    return id == null ? null : new UserIdEntity(id.val());
  }

  @Mapping(target = "id", ignore = true)
  UserDetailsEntity toEntity(UserDetails details);

  @Mapping(source = "val", target = "id")
  Avatar toDomain(AvatarEntity avatarEntity);
}
