package code.infrastructure.users;

import code.modules.User;

public class UserEntityMapper {
    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword()
        );
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.email(),
                user.password()
        );
    }
}
