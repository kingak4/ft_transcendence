package code.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AvatarJpaRepository extends JpaRepository<AvatarEntity, UUID> {
}