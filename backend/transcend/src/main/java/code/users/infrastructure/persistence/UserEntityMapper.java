package code.users.infrastructure.persistence;

import code.users.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getHash()
        );
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.email(),
                user.password()
        );
    }
}
