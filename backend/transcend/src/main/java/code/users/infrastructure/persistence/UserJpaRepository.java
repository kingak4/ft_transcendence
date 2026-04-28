package code.users.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UserIdEntity> {

  Optional<UserEntity> findByEmail(String email);
}
