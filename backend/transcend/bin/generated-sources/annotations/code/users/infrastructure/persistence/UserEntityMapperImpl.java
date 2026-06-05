package code.users.infrastructure.persistence;

import code.users.domain.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserEntityMapperImpl implements UserEntityMapper {

    @Override
    public User toDomain(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.password( entity.getHash() );
        user.email( entity.getEmail() );
        user.id( map( entity.getId() ) );
        user.role( entity.getRole() );

        return user.build();
    }

    @Override
    public UserEntity toEntity(User user) {
        if ( user == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setHash( user.getPassword() );
        userEntity.setEmail( user.getEmail() );
        userEntity.setId( map( user.getId() ) );
        userEntity.setRole( user.getRole() );

        return userEntity;
    }
}
