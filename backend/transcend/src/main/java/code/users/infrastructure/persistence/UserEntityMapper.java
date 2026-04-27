package code.users.infrastructure.persistence;

import code.users.domain.model.User;
import code.users.domain.model.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(new UserId(entity.getId()))
                .email(entity.getEmail())
                .password(entity.getHash())
                .details(null)
                .build();
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.getEmail(),
                user.getPassword()
        );
    }
}
