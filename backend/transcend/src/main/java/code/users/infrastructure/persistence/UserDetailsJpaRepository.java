package code.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsJpaRepository extends JpaRepository<UserDetailsEntity, UserIdEntity> {
}